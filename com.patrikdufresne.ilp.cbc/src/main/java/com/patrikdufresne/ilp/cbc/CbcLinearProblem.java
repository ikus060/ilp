/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.patrikdufresne.ilp.cbc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.patrikdufresne.cbc4j.SWIGTYPE_p_OsiClpSolverInterface;
import com.patrikdufresne.cbc4j.cbc4j;
import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.IPersistentLinearProblem;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

public class CbcLinearProblem extends AbstractLinearProblem implements IPersistentLinearProblem {

    /**
     * The scale for the returned value.
     */
    private static final int SCALE = 9;

    /**
     * Rounds the value,
     * Implemented to solve the problems where the value was not returning an rounded number for close values value
     * ex: 0.9999999999988346 instead of 1.0
     * 
     * @param value
     *      The double to be rounded
     * @return The rounded value
     */
    public static Double round(Double value) {
        return new BigDecimal(value).setScale(SCALE, RoundingMode.HALF_EVEN).doubleValue();
    }

    /**
     * Used to keep the best solution previously computed by the solver.
     */
    double[] bestSolution;
    /**
     * The objective value.
     */
    Double objValue;

    private Set<String> constraintNames;
    /**
     * Need to keep reference on every constraint (row).
     */
    private List<CbcConstraint> constraints;

    final double infinity;

    /**
     * Reference to the
     */
    SWIGTYPE_p_OsiClpSolverInterface lp;

    String name;

    private String objName;

    /**
     * The status of the problem. This value is sets the UNKNOWN when any variable or constraints is changed.
     */
    Status status = Status.UNKNOWN;

    private Set<String> variableNames;

    /**
     * Need to keep reference on every variable (col).
     */
    List<CbcVariable> variables;

    CbcLinearProblem() {
        // Create a new lp
        this.lp = cbc4j.newOsiClpSolverInterface();
        this.infinity = cbc4j.getInfinity(this.lp);
    }

    /**
     * Should be called for every new instance of CbcVariable
     * 
     * @param var
     *            the variable to be added to the problem.
     */
    synchronized void addCol(CbcVariable var, String name) {
        if (this.variables == null) {
            this.variables = new ArrayList<CbcVariable>();
        }
        if (this.variableNames == null) {
            this.variableNames = new HashSet<String>();
        }

        // Create a new column using CBC API.
        var.parent = this;
        var.col = cbc4j.getNumCols(this.lp);
        cbc4j.addCol(this.lp, 0, new int[0], new double[0], -this.infinity, this.infinity, 0);
        cbc4j.setColName(this.lp, var.col, name);
        if (var.col != this.variables.size()) {
            throw new RuntimeException("CbcVariable.col is not set properly."); //$NON-NLS-1$
        }
        this.variables.add(var);
        this.variableNames.add(name);
    }

    @Override
    public Constraint addConstraint(String name) {
        checkProblem();
        checkConstraintName(name);
        CbcConstraint c = new CbcConstraint(this, name, null, null, null);
        return c;
    }

    /**
     * To reduce the number of JNI call, this method will create the contrains in one step.
     */
    @Override
    public Constraint addConstraint(String name, Linear linear, Number lowerBound, Number upperBound) {
        checkProblem();
        checkConstraintName(name);
        checkLinear(linear);
        CbcConstraint c = new CbcConstraint(this, name, linear, lowerBound, upperBound);
        return c;
    }

    /**
     * Should be called for every instance of CbcConstraint.
     * 
     * @param constraint
     *            the constraint to be added to the problem.
     * @param upperBound
     * @param lowerBound
     * @param linear
     */
    synchronized void addRow(CbcConstraint constraint, String name, Linear linear, Number lowerBound, Number upperBound) {
        if (this.constraints == null) {
            this.constraints = new ArrayList<CbcConstraint>();
        }
        if (this.constraintNames == null) {
            this.constraintNames = new HashSet<String>();
        }
        constraint.parent = this;
        constraint.row = cbc4j.getNumRows(this.lp);
        int[] columns;
        double[] coefs;
        if (linear != null) {
            columns = new int[linear.size()];
            coefs = new double[linear.size()];
            int i = 0;
            for (Term t : linear) {
                columns[i] = ((CbcVariable) t.getVariable()).col;
                coefs[i] = t.getCoefficient().doubleValue();
                i++;
            }
        } else {
            columns = new int[0];
            coefs = new double[0];
        }
        cbc4j.addRow(this.lp, columns.length, columns, coefs, lowerBound != null ? lowerBound.doubleValue() : -this.infinity, upperBound != null ? upperBound
                .doubleValue() : this.infinity);
        if (constraint.row != this.constraints.size()) {
            throw new RuntimeException("CbcConstraint.row is not set properly."); //$NON-NLS-1$
        }
        this.constraints.add(constraint);
        this.constraintNames.add(name);
    }

    /**
     * Add a new variables to the problem.
     */
    @Override
    public Variable addVariable(String name, VarType type) {
        checkProblem();
        checkVariableName(name);
        CbcVariable v = new CbcVariable(this, name);
        v.setType(type);
        if (VarType.INTEGER.equals(type) || VarType.REAL.equals(type)) {
            v.setLowerBound(null);
            v.setUpperBound(null);
        }
        return v;
    }

    /**
     * Check if the constraint name is unique. Otherwise throw an exception.
     * 
     * @param name
     *            the constraint name.
     */
    void checkConstraintName(String name) {
        if (name == null) {
            throw new ILPException(ILPException.ERROR_DUPLICATE_NAME, "Undefined constraint name.");
        }
        if (this.constraintNames != null && this.constraintNames.contains(name)) {
            throw new ILPException(ILPException.ERROR_DUPLICATE_NAME, "Duplicate constraint name: " + name);
        }
    }

    /**
     * Check if the problem is disposed.
     */
    void checkProblem() {
        if (this.lp == null) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
    }

    /**
     * Check the solution state. Throw an exception if the solution is not available.
     */
    void checkSolution() {
        switch (this.status) {
        case FEASIBLE:
        case OPTIMAL:
            break;
        default:
            throw new ILPException("solution not available"); //$NON-NLS-1$
        }
        if (this.bestSolution == null) {
            throw new ILPException("solution not available"); //$NON-NLS-1$
        }
    }

    /**
     * Check if the variable name is unique. Otherwise throw an exception.
     * 
     * @param name
     *            the constraint name.
     */
    void checkVariableName(String name) {
        if (name == null) {
            throw new ILPException(ILPException.ERROR_DUPLICATE_NAME, "Undefined variable name.");
        }
        if (this.variableNames != null && this.variableNames.contains(name)) {
            throw new ILPException(ILPException.ERROR_DUPLICATE_NAME, "Duplicate variable name: " + name);
        }
    }

    @Override
    public void dispose() {
        // Free the lp problem.
        if (this.lp != null) {
            cbc4j.deleteOsiClpSolverInterface(this.lp);
        }
        this.lp = null;
        this.bestSolution = null;
    }

    /**
     * Return the variable for the given column index.
     * 
     * @param col
     *            the column index.
     * @return the variable object
     */
    Variable getCol(int col) {
        CbcVariable var = this.variables.get(col);
        if (var.col != col) {
            throw new ILPException("Variables list corrupted"); //$NON-NLS-1$
        }
        return var;
    }

    /**
     * Return an unmodifiable collection of constraints.
     */
    @Override
    public Collection<? extends Constraint> getConstraints() {
        if (this.constraints == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.constraints);
    }

    /**
     * Return the problem name.
     */
    @Override
    public String getName() {
        checkProblem();
        return this.name;
    }

    /**
     * Return the problem's direction.
     * 
     * @return
     */
    @Override
    public int getObjectiveDirection() {
        checkProblem();
        double sense = cbc4j.getObjSense(this.lp);
        return sense > 0 ? MINIMIZE : MAXIMIZE;
    }

    /**
     * This implementation rebuild the linear object.
     * 
     * @return
     */
    @Override
    public Linear getObjectiveLinear() {
        checkProblem();
        // Get the coefficient of each column.
        Linear linear = createLinear();
        double[] coefs = cbc4j.getObjCoefficients(this.lp);
        for (int col = 0; col < coefs.length; col++) {
            if (coefs[col] != 0) {
                linear.add(createTerm(coefs[col], getCol(col)));
            }
        }
        if (linear.isEmpty()) {
            return null;
        }
        return linear;
    }

    /**
     * This implementation return the objective value.
     */
    @Override
    public Double getObjectiveValue() {
        checkProblem();
        checkSolution();
        if (this.objValue == null) return null;
        return round(this.objValue);
    }

    /**
     * Get the problem status
     */
    @Override
    public Status getStatus() {
        checkProblem();
        if (this.status == null) {
            return Status.UNKNOWN;
        }
        return this.status;
    }

    @Override
    public Collection<? extends Variable> getVariables() {
        if (this.variables == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.variables);
    }

    /**
     * Return true if <code>lp</code> is null.
     */
    @Override
    public boolean isDisposed() {
        return this.lp == null;
    }

    /**
     * Load the problem from Mps file.
     */
    @Override
    public void load(File file) throws IOException {
        checkProblem();
        cbc4j.readLp(this.lp, file.getAbsolutePath());
    }

    /**
     * Remove the column from the linear problem.
     * 
     * @param col
     */
    synchronized void removeCol(CbcVariable var) {
        int index;
        if (this.variables == null || (index = this.variables.indexOf(var)) < 0) {
            throw new RuntimeException("CbcVariable not in the variable list."); //$NON-NLS-1$
        }

        String varName = var.getName();
        cbc4j.deleteCols(this.lp, 1, new int[] { var.col });
        this.variables.remove(index);
        this.variableNames.remove(varName);

        var.col = 0;
        var.parent = null;

        // Adjust the bestSolution
        if (this.bestSolution != null && this.bestSolution.length > 1) {
            double[] newSolution = new double[this.bestSolution.length - 1];
            System.arraycopy(bestSolution, 0, newSolution, 0, index);
            System.arraycopy(bestSolution, index + 1, newSolution, index, bestSolution.length - index - 1);
            this.bestSolution = newSolution;
        }

        for (; index < this.variables.size(); index++) {
            CbcVariable sub = this.variables.get(index);
            sub.col = index;
        }

    }

    synchronized void removeRow(CbcConstraint constraint) {
        int index;
        if (this.constraints == null || (index = this.constraints.indexOf(constraint)) < 0) {
            throw new RuntimeException("CbcConstraint not in the constraint list."); //$NON-NLS-1$
        }
        String constName = constraint.getName();
        cbc4j.deleteRows(this.lp, 1, new int[] { constraint.row });
        this.constraints.remove(index);
        this.constraintNames.remove(constName);
        constraint.row = 0;
        constraint.parent = null;
        for (; index < this.constraints.size(); index++) {
            CbcConstraint sub = this.constraints.get(index);
            sub.row = index;
        }
    }

    /**
     * This implementation write the lp into Mps.
     */
    @Override
    public void save(File file) throws IOException {
        checkProblem();
        // Write the lp
        cbc4j.writeLp(this.lp, file.getAbsolutePath());
    }

    /**
     * Sets the problem's name
     */
    void setName(String name) {
        checkProblem();
        this.name = name;
    }

    /**
     * Sets the objective direction.
     */
    @Override
    public void setObjectiveDirection(int direction) {
        checkProblem();
        switch (direction) {
        case MAXIMIZE:
            cbc4j.setObjSense(this.lp, -1.0);
            break;
        case MINIMIZE:
            cbc4j.setObjSense(this.lp, 1.0);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * This implementation sets the objective name and coefficient.
     */
    @Override
    public void setObjectiveLinear(Linear linear) {
        checkProblem();
        checkLinear(linear);
        int[] columns;
        double[] coefs;
        if (linear != null) {
            columns = new int[linear.size()];
            coefs = new double[linear.size()];
            int i = 0;
            for (Term t : linear) {
                columns[i] = ((CbcVariable) t.getVariable()).col;
                coefs[i] = t.getCoefficient().doubleValue();
                i++;
            }
        } else {
            columns = new int[0];
            coefs = new double[0];
        }
        cbc4j.setObjCoefficients(this.lp, columns.length, columns, coefs);
    }

}
