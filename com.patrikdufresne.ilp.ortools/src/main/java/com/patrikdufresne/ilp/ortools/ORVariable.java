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
package com.patrikdufresne.ilp.ortools;

import com.google.ortools.linearsolver.MPVariable;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

/**
 * This class implement the Variable interface for OR-tools. Each instance of
 * this class is wrapping a MPVariable.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ORVariable implements Variable {

    /**
     * Reference to the variable.
     */
    MPVariable v;
    /**
     * The linear problem.
     */
    public ORLinearProblem parent;

    /**
     * Create a new variable.
     * 
     * @param parent
     *            the linear parent
     * @param type
     *            the variable type
     * @param name
     *            the variable name.
     */
    public ORVariable(ORLinearProblem parent, String name, VarType type) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        parent.addVariable(this, name, type);
    }

    /**
     * Return the lower bound of null if unbounded.
     */
    @Override
    public Double getLowerBound() {
        checkVariable();
        double lower = this.v.lb();
        if (lower == -ORLinearProblem.INFINITY) {
            return null;
        }
        return Double.valueOf(lower);
    }

    @Override
    public String getName() {
        checkVariable();
        return this.v.name();
    }

    /**
     * Check the state of the variable. Throw an exception if the variable is
     * disposed.
     */
    private void checkVariable() {
        if (isDisposed()) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
        this.parent.checkProblem();
    }

    /**
     * Return the solution value.
     */
    @Override
    public Double getValue() {
        checkVariable();
        this.parent.checkSolution();
        return this.v.solutionValue();
    }

    /**
     * Return the variable type according to the variable integrity and bounds.
     */
    @Override
    public VarType getType() {
        checkVariable();
        if (!this.v.integer()) {
            return VarType.REAL;
        }
        if (this.v.lb() == 0 && this.v.ub() == 1) {
            return VarType.BOOL;
        }
        return VarType.INTEGER;
    }

    /**
     * Return the upper bound value or null if unbounded.
     */
    @Override
    public Double getUpperBound() {
        checkVariable();
        double upper = this.v.ub();
        if (upper == ORLinearProblem.INFINITY) {
            return null;
        }
        return Double.valueOf(upper);
    }

    /**
     * Sets the lower bound.
     */
    @Override
    public void setLowerBound(Number bound) {
        checkVariable();
        if (bound == null) {
            this.v.setLb(-ORLinearProblem.INFINITY);
            this.parent.status = null;
        } else {
            this.v.setLb(bound.doubleValue());
            this.parent.status = null;
        }
    }

    /**
     * Sets the upper bound.
     */
    @Override
    public void setUpperBound(Number bound) {
        checkVariable();
        if (bound == null) {
            this.v.setUb(ORLinearProblem.INFINITY);
            this.parent.status = null;
        } else {
            this.v.setUb(bound.doubleValue());
            this.parent.status = null;
        }
    }

    /**
     * Return true if the variable or the parent is null.
     */
    @Override
    public boolean isDisposed() {
        return this.v == null || this.parent == null;
    }

    /**
     * This implementation remove the variable from the linear problem.
     */
    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        // Call the parent function to complete the operation.
        this.parent.removeVariable(this);
    }

}
