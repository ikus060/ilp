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

import org.gnu.glpk.GLPK;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;

import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.ConcreteLinear;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;

/**
 * This class is a complete implementation of the {@link Constraint} interface
 * for GLPK solver.
 * <p>
 * Setting the constraint's bound required adjustment for glp_set_row_bnds().
 * Here is a description of the parameters.
 * 
 * <pre>
 * Type     Bounds        Comment
 * GLP_FR   −∞ < x < +∞   Free (unbounded) variable
 * GLP_LO   lb ≤ x < +∞   Variable with lower bound
 * GLP_UP   −∞ < x ≤ ub   Variable with upper bound
 * GLP_DB   lb ≤ x ≤ ub   Double-bounded variable
 * GLP_FX   lb = x = ub   Fixed variable
 * </pre>
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKConstraint implements Constraint {

    /**
     * Reference to the GLPK problem
     */
    GLPKLinearProblem parent;
    /**
     * row index of this constraint in the problem.
     */
    int row;

    /**
     * Create a new constraint.
     * 
     * @param parent
     *            reference to the glpk problem
     * @param row
     *            the constraint row index
     */
    GLPKConstraint(GLPKLinearProblem parent, String name) {
        if (parent == null) {
            throw new NullPointerException();
        }
        parent.checkProblem();

        // Call the parent function to complete the work
        parent.addRow(this, name);
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
     * Returns the constraint linear expression. Need to rebuild the Linear
     * object.
     */
    @Override
    public Linear getLinear() {
        checkConstraint();

        // First call to get the length
        int len = GLPK.glp_get_mat_row(this.parent.lp, this.row, null, null);

        if (len == 0) {
            return new ConcreteLinear();
        }

        // Second call to get the array value
        SWIGTYPE_p_double coefs = GLPK.new_doubleArray(len + 1);
        SWIGTYPE_p_int cols = GLPK.new_intArray(len + 1);
        GLPK.glp_get_mat_row(this.parent.lp, this.row, cols, coefs);

        // Rebuild the Linear object
        ConcreteLinear linear = new ConcreteLinear();
        for (int i = 1; i <= len; i++) {

            int col = GLPK.intArray_getitem(cols, i);
            double coef = GLPK.doubleArray_getitem(coefs, i);

            linear.add(this.parent.createTerm(Double.valueOf(coef), this.parent.getCol(col)));
        }

        // Return the Linear object
        return linear;
    }

    /**
     * Return the row's lower bound or null if unbounded.
     */
    @Override
    public Double getLowerBound() {
        checkConstraint();

        double value = GLPK.glp_get_row_lb(this.parent.lp, this.row);
        if (value == -GLPKLinearProblem.DBL_MAX) {
            return null;
        }

        return Double.valueOf(value);
    }

    /**
     * Returns GLPK row name.
     */
    @Override
    public String getName() {
        checkConstraint();

        return GLPK.glp_get_row_name(this.parent.lp, this.row);
    }

    /**
     * Return the row's upper bound or null if unbounded.
     */
    @Override
    public Double getUpperBound() {
        checkConstraint();
        double value = GLPK.glp_get_row_ub(this.parent.lp, this.row);

        if (value == GLPKLinearProblem.DBL_MAX) {
            return null;
        }
        return Double.valueOf(value);
    }

    @Override
    public Double getValue() {
        checkConstraint();
        this.parent.checkSolution();
        if (this.parent.isMIP()) {
            return Double.valueOf(GLPK.glp_mip_row_val(this.parent.lp, this.row));
        }
        return Double.valueOf(GLPK.glp_get_row_prim(this.parent.lp, this.row));
    }

    @Override
    public boolean isDisposed() {
        return this.parent == null || this.row == 0;
    }

    @Override
    public boolean isEmpty() {
        checkConstraint();
        // First call to get the length
        int len = GLPK.glp_get_mat_row(this.parent.lp, this.row, null, null);
        return len == 0;
    }

    /**
     * Sets the constraint linear expression. Since GLPK implementation is using
     * ConcreteLinear object, the linear need to be converted into an array.
     */
    @Override
    public void setLinear(Linear linear) {
        checkConstraint();
        AbstractLinearProblem.checkLinear(linear);

        int size = linear != null ? linear.size() : 0;
        SWIGTYPE_p_double coefs = GLPK.new_doubleArray(size + 1);
        SWIGTYPE_p_int cols = GLPK.new_intArray(size + 1);

        int idx = 0;
        if (linear != null) {
            for (Term term : linear) {
                idx++;
                // Sets the coefficient value
                GLPK.doubleArray_setitem(coefs, idx, term.getCoefficient().doubleValue());
                // Sets the columns index
                GLPK.intArray_setitem(cols, idx, ((GLPKVariable) term.getVariable()).col);
            }
        }

        // Sets the row matrix
        GLPK.glp_set_mat_row(this.parent.lp, this.row, size, cols, coefs);
    }

    @Override
    public void setLowerBound(Number lb) {
        checkConstraint();

        Double ub = getUpperBound();
        int type = GLPKLinearProblem.boundedType(lb, ub);

        GLPK.glp_set_row_bnds(this.parent.lp, this.row, type, lb != null ? lb.doubleValue() : 0, ub != null ? ub.doubleValue() : 0);
    }

    /**
     * Sets the GLPK row name.
     */
    private void setName(String name) {
        checkConstraint();

        if (name != null && name.length() > 255) {
            throw new IllegalArgumentException("name > 255"); //$NON-NLS-1$
        }

        GLPK.glp_set_row_name(this.parent.lp, this.row, name);
    }

    /**
     * Sets the row bounds and type.
     */
    @Override
    public void setUpperBound(Number ub) {
        checkConstraint();

        Double lb = getLowerBound();
        int type = GLPKLinearProblem.boundedType(lb, ub);

        GLPK.glp_set_row_bnds(this.parent.lp, this.row, type, lb != null ? lb.doubleValue() : 0, ub != null ? ub.doubleValue() : 0);
    }

    @Override
    public String toString() {
        if (isDisposed()) {
            return "GLPKConstraint [disposed]"; //$NON-NLS-1$
        }
        return "GLPKConstraint [name=" + getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
