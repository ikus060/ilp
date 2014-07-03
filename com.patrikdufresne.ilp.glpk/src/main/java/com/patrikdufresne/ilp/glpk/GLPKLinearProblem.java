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
package com.patrikdufresne.ilp.glpk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;

import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.ILPLogger;
import com.patrikdufresne.ilp.ILPPolicy;
import com.patrikdufresne.ilp.IPersistentLinearProblem;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

/**
 * This implementation represent a GLPK linear problem.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKLinearProblem extends AbstractLinearProblem implements IPersistentLinearProblem {

    /**
     * Value return by glp_get_row_ub, glp_get_row_lb, glp_get_col_ub and glp_get_col_lb when it's not bounded.
     */
    static final double DBL_MAX = Double.MAX_VALUE;

    /**
     * Determine the column bounding type according to the bounding value.
     * 
     * @param lb
     *            lower bound value
     * @param ub
     *            upper bound value
     * 
     * @return one of the GLP_DB, GLP_FX, GLP_LO, GLP_UP, GLP_FR
     */
    static int boundedType(Number lb, Number ub) {
        int type;
        if (ub != null && lb != null) {
            // Double-bounded
            type = GLPKConstants.GLP_DB;
            if (ub.doubleValue() == lb.doubleValue()) {
                // Fixed
                type = GLPKConstants.GLP_FX;
            }
        } else if (ub == null && lb != null) {
            // Lower bounded
            type = GLPKConstants.GLP_LO;
        } else if (ub != null && lb == null) {
            // Upper bounded
            type = GLPKConstants.GLP_UP;
        } else {
            // Free (unbounded)
            type = GLPKConstants.GLP_FR;
        }
        return type;
    }

    /**
     * Need to keep reference on every constraint (row).
     */
    private List<GLPKConstraint> constraints;

    private Set<String> constraintNames;

    private Set<String> variableNames;

    /**
     * Private reference on the glpk problem.
     */
    glp_prob lp;

    /**
     * Cached value. True if the problem is a MIP.
     */
    Boolean mip;

    /**
     * The status of the problem. This value is sets the UNKNOWN when any variable or constraints is changed.
     */
    Status status = Status.UNKNOWN;;

    /**
     * Need to keep reference on every variable (col).
     */
    private List<GLPKVariable> variables;

    /**
     * Create a new linear problem.
     */
    public GLPKLinearProblem() {
        this.lp = GLPK.glp_create_prob();
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

    /**
     * Should be called for every new instance of GLPKVariable
     * 
     * @param var
     *            the variable to be added to the problem.
     */
    synchronized void addCol(GLPKVariable var, String name) {
        this.mip = null;
        if (this.variables == null) {
            this.variables = new ArrayList<GLPKVariable>();
        }
        if (this.variableNames == null) {
            this.variableNames = new HashSet<String>();
        }

        // Create a new column using GLPK API.
        var.parent = this;
        var.col = GLPK.glp_add_cols(this.lp, 1);

        if (var.col != this.variables.size() + 1) {
            throw new RuntimeException("GLPKVariable.col is not set properly."); //$NON-NLS-1$
        }
        this.variables.add(var);
        this.variableNames.add(name);
    }

    @Override
    public Constraint addConstraint(String name) {
        checkProblem();
        checkConstraintName(name);
        GLPKConstraint c = new GLPKConstraint(this, name);
        return c;
    }

    /**
     * Should be called for every instance of GLPKConstraint.
     * 
     * @param constraint
     *            the constraint to be added to the problem.
     */
    synchronized void addRow(GLPKConstraint constraint, String name) {
        if (this.constraints == null) {
            this.constraints = new ArrayList<GLPKConstraint>();
        }
        if (this.constraintNames == null) {
            this.constraintNames = new HashSet<String>();
        }

        constraint.parent = this;
        constraint.row = GLPK.glp_add_rows(this.lp, 1);

        if (constraint.row != this.constraints.size() + 1) {
            throw new RuntimeException("GLPKConstraint.row is not set properly."); //$NON-NLS-1$
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
        GLPKVariable v = new GLPKVariable(this, name);
        v.setType(type);
        if (VarType.INTEGER.equals(type) || VarType.REAL.equals(type)) {
            v.setLowerBound(null);
            v.setUpperBound(null);
        }
        return v;
    }

    /**
     * Check if the problem is disposed.
     */
    void checkProblem() {
        if (isDisposed()) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
    }

    /**
     * Check the solution state. Throw an exception if the solution is not available.
     */
    void checkSolution() {
        Status status = getStatus();
        if (!status.equals(Status.FEASIBLE) && !status.equals(Status.OPTIMAL)) {
            throw new ILPException("solution not available"); //$NON-NLS-1$
        }
    }

    @Override
    public void dispose() {
        // Free the lp problem.
        if (this.lp != null) {
            GLPK.glp_delete_prob(this.lp);
        }
        this.lp = null;
    }

    /**
     * Return the variable for the given column index.
     * 
     * @param col
     *            the column index.
     * @return the variable object
     */
    Variable getCol(int col) {
        GLPKVariable var = this.variables.get(col - 1);
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
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.constraints);
    }

    /**
     * Return the problem name.
     */
    @Override
    public String getName() {
        checkProblem();
        return GLPK.glp_get_prob_name(this.lp);
    }

    /**
     * Return the problem's direction.
     * 
     * @return
     */
    @Override
    public int getObjectiveDirection() {

        checkProblem();

        int direction = GLPK.glp_get_obj_dir(this.lp);
        if (direction == GLPKConstants.GLP_MAX) {
            return MAXIMIZE;
        }
        return MINIMIZE;

    }

    /**
     * This implementation rebuild the linear object.
     * 
     * @return
     */

    @Override
    public Linear getObjectiveLinear() {
        checkProblem();

        // Get the number of columns
        int count = GLPK.glp_get_num_cols(this.lp);

        if (count == 0) {
            return createLinear();
        }

        // Get the coefficient of each column.
        Linear linear = createLinear();
        for (int col = 1; col < count + 1; col++) {
            double coef = GLPK.glp_get_obj_coef(this.lp, col);
            if (coef != 0) {
                linear.add(createTerm(coef, getCol(col)));
            }
        }

        if (linear.size() == 0) {
            return null;
        }

        return linear;
    }

    @Override
    public String getObjectiveName() {
        checkProblem();

        return GLPK.glp_get_obj_name(this.lp);
    }

    /**
     * This implementation return the objective value.
     */
    @Override
    public Double getObjectiveValue() {
        checkProblem();

        checkSolution();

        if (isMIP()) {
            return Double.valueOf(GLPK.glp_mip_obj_val(this.lp));
        }

        return Double.valueOf(GLPK.glp_get_obj_val(this.lp));
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
            return Collections.EMPTY_LIST;
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
     * Cached version of this function to avoid calling JNI over and over.
     */
    @Override
    public boolean isMIP() {

        if (this.mip == null) {
            this.mip = Boolean.valueOf(super.isMIP());
        }
        return this.mip.booleanValue();

    }

    /**
     * This implementations read the file as CPLEX format using glp_read_lp() function.
     */
    @Override
    public void load(File file) throws IOException {
        checkProblem();

        if (file == null) {
            throw new NullPointerException();
        }

        if (GLPK.glp_read_lp(this.lp, null, file.getAbsolutePath()) != 0) {
            // TODO retrieved the error message
            throw new IOException("Error reading the problem from file."); //$NON-NLS-1$
        }

    }

    /**
     * Remove the column fr om the linear problem.
     * 
     * @param col
     */
    synchronized void removeCol(GLPKVariable var) {
        int index;
        if (this.variables == null || (index = this.variables.indexOf(var)) < 0) {
            throw new RuntimeException("GLPKVariable not in the variable list."); //$NON-NLS-1$
        }

        String name = var.getName();
        SWIGTYPE_p_int cols = GLPK.new_intArray(2);
        GLPK.intArray_setitem(cols, 1, var.col);
        GLPK.glp_del_cols(this.lp, 1, cols);

        this.variables.remove(index);
        this.variableNames.remove(name);

        var.col = 0;
        var.parent = null;

        for (; index < this.variables.size(); index++) {
            GLPKVariable sub = this.variables.get(index);
            sub.col = index + 1;
        }

    }

    synchronized void removeRow(GLPKConstraint constraint) {
        int index;
        if (this.constraints == null || (index = this.constraints.indexOf(constraint)) < 0) {
            throw new RuntimeException("GLPKConstraint not in the constraint list."); //$NON-NLS-1$
        }

        String name = constraint.getName();
        SWIGTYPE_p_int rows = GLPK.new_intArray(2);
        GLPK.intArray_setitem(rows, 1, constraint.row);
        GLPK.glp_del_rows(this.lp, 1, rows);

        this.constraints.remove(index);
        this.constraintNames.remove(name);

        constraint.row = 0;
        constraint.parent = null;

        for (; index < this.constraints.size(); index++) {
            GLPKConstraint sub = this.constraints.get(index);
            sub.row = index + 1;
        }

    }

    /**
     * This implementation is writing the problem in GLPK LP/MIP format to text file.
     */
    @Override
    public void save(File file) throws IOException {
        checkProblem();

        if (file == null) {
            throw new NullPointerException();
        }

        if (GLPK.glp_write_lp(this.lp, null, file.getAbsolutePath()) != 0) {
            // TODO retrieved the error message
            throw new IOException("Error writing the problem to file."); //$NON-NLS-1$
        }

    }

    /**
     * Sets the problem's name
     */
    void setName(String name) {
        checkProblem();

        // Check string size
        if (name != null && name.length() > 255) {
            throw new IndexOutOfBoundsException("name > 255"); //$NON-NLS-1$
        }
        GLPK.glp_set_prob_name(this.lp, name);
    }

    /**
     * Sets the objective direction.
     */
    @Override
    public void setObjectiveDirection(int direction) {
        checkProblem();
        switch (direction) {
        case MAXIMIZE:
            GLPK.glp_set_obj_dir(this.lp, GLPKConstants.GLP_MAX);
            break;
        case MINIMIZE:
            GLPK.glp_set_obj_dir(this.lp, GLPKConstants.GLP_MIN);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * This implementation sets the objective name and coefficient.
     */
    @Override
    public void setObjectiveLinear(Linear objective) {
        checkProblem();

        // Sets all the coefs to zero
        int colCount = GLPK.glp_get_num_cols(this.lp);
        if (colCount > 0) {
            for (int i = 1; i <= colCount; i++) {
                GLPK.glp_set_obj_coef(this.lp, i, 0);
            }
        }

        if (objective == null || objective.size() == 0) {
            return;
        }

        // Then sets the real coef value
        for (Term term : objective) {
            GLPK.glp_set_obj_coef(this.lp, ((GLPKVariable) term.getVariable()).col, term.getCoefficient().doubleValue());
        }

    }

    /**
     * Sets the objective name
     */
    public void setObjectiveName(String name) {
        checkProblem();
        // Check string size
        if (name != null && name.length() > 255) {
            throw new IndexOutOfBoundsException("name > 255"); //$NON-NLS-1$
        }
        GLPK.glp_set_obj_name(this.lp, name);
    }

}
