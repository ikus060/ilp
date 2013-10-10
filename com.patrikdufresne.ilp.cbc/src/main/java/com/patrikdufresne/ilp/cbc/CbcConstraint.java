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

import com.patrikdufresne.cbc4j.cbc4j;
import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.ConcreteLinear;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;

public class CbcConstraint implements Constraint {

    /**
     * Reference to the Cbc problem
     */
    CbcLinearProblem parent;
    /**
     * Row index of this constraint in the problem.
     */
    int row;

    /**
     * Create a new constraint.
     * 
     * @param parent
     *            reference to the CBC problem
     * @param linear
     *            the linear or null
     * @param row
     *            the constraint row index
     */
    CbcConstraint(CbcLinearProblem parent, String name, Linear linear, Number lowerBound, Number upperBound) {
        if (parent == null) {
            throw new NullPointerException();
        }
        parent.checkProblem();

        // Call the parent function to complete the work
        parent.addRow(this, name, linear, lowerBound, upperBound);
        setName(name);
    }

    /**
     * Check if the constraint is disposed.
     */
    void checkConstraint() {
        if (isDisposed()) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
        this.parent.checkProblem();
    }

    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        this.parent.removeRow(this);
    }

    /**
     * Returns the constraint linear expression. Need to rebuild the Linear object.
     */
    @Override
    public Linear getLinear() {
        checkConstraint();
        // Rebuild the Linear object
        int colCount = this.parent.variables.size();
        ConcreteLinear linear = new ConcreteLinear();
        for (int col = 0; col < colCount; col++) {
            double coef = cbc4j.getCoefficient(this.parent.lp, this.row, col);
            if (coef != 0) {
                linear.add(this.parent.createTerm(coef, this.parent.getCol(col)));
            }
        }
        // Return the Linear object
        return linear;
    }

    /**
     * Return the row's lower bound or null if unbounded.
     */
    @Override
    public Number getLowerBound() {
        checkConstraint();
        double value = cbc4j.getRowLower(this.parent.lp, this.row);
        if (value == -this.parent.INFINITY) {
            return null;
        }
        return Double.valueOf(value);
    }

    /**
     * Returns row name.
     */
    @Override
    public String getName() {
        checkConstraint();
        return cbc4j.getRowName(this.parent.lp, this.row);
    }

    /**
     * Return the row's upper bound or null if unbounded.
     */
    @Override
    public Number getUpperBound() {
        checkConstraint();
        double value = cbc4j.getRowUpper(this.parent.lp, this.row);
        if (value == this.parent.INFINITY) {
            return null;
        }
        return Double.valueOf(value);
    }

    @Override
    public Number getValue() {
        checkConstraint();
        this.parent.checkSolution();
        // TODO implement this method.
        throw new UnsupportedOperationException("don't know how to implement this");
    }

    @Override
    public boolean isDisposed() {
        return this.parent == null || this.row < 0;
    }

    @Override
    public boolean isEmpty() {
        return getLinear().size() == 0;
    }

    @Override
    public void setLinear(Linear linear) {
        checkConstraint();
        this.parent.status = Status.UNKNOWN;
        AbstractLinearProblem.checkLinear(linear);
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
        cbc4j.setCoefficients(this.parent.lp, this.row, columns.length, columns, coefs);
    }

    @Override
    public void setLowerBound(Number lb) {
        checkConstraint();
        cbc4j.setRowLower(this.parent.lp, this.row, lb != null ? lb.doubleValue() : -this.parent.INFINITY);
        this.parent.status = Status.UNKNOWN;
    }

    /**
     * Sets the row name.
     */
    private void setName(String name) {
        checkConstraint();
        cbc4j.setRowName(this.parent.lp, this.row, name);
    }

    /**
     * Sets the row bounds and type.
     */
    @Override
    public void setUpperBound(Number ub) {
        checkConstraint();
        cbc4j.setRowUpper(this.parent.lp, this.row, ub != null ? ub.doubleValue() : this.parent.INFINITY);
        this.parent.status = Status.UNKNOWN;
    }

    @Override
    public String toString() {
        if (isDisposed()) {
            return "CbcConstraint [disposed]"; //$NON-NLS-1$
        }
        return "CbcConstraint [name=" + getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
