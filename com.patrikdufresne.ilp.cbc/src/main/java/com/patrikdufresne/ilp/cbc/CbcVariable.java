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
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

public class CbcVariable implements Variable {
    /**
     * The col index.
     */
    int col;

    /**
     * Reference to the problem.
     */
    CbcLinearProblem parent;

    /**
     * Create a new variable.
     * 
     * @param parent
     *            the parent problem.
     */
    CbcVariable(CbcLinearProblem parent, String name) {
        if (parent == null) {
            throw new NullPointerException();
        }
        parent.checkProblem();

        // Call the parent function to complete the opperation.
        parent.addCol(this, name);
    }

    /**
     * Check if the variable is disposed.
     */
    void checkVariable() {
        if (isDisposed()) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
        this.parent.checkProblem();
    }

    /**
     * This implementation remove the column from the linear problem.
     */
    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        // Call the parent function to complete the operation.
        this.parent.removeCol(this);
    }

    @Override
    public Double getLowerBound() {
        checkVariable();
        double value = cbc4j.getColLower(this.parent.lp, this.col);
        if (value == -this.parent.infinity) {
            return null;
        }
        return Double.valueOf(value);
    }

    @Override
    public String getName() {
        this.parent.checkProblem();
        return cbc4j.getColName(this.parent.lp, this.col);
    }

    @Override
    public VarType getType() {
        checkVariable();
        if (cbc4j.isBinary(this.parent.lp, this.col)) {
            return VarType.BOOL;
        } else if (cbc4j.isInteger(this.parent.lp, this.col)) {
            return VarType.INTEGER;
        } else if (cbc4j.isContinuous(this.parent.lp, this.col)) {
            return VarType.REAL;
        }
        throw new ILPException("unknown var type returns invalid value"); //$NON-NLS-1$
    }

    @Override
    public Double getUpperBound() {
        checkVariable();
        double value = cbc4j.getColUpper(this.parent.lp, this.col);
        if (value == this.parent.infinity) {
            return null;
        }
        return Double.valueOf(value);
    }

    /**
     * Retrieve the column primal value.
     */
    @Override
    public Double getValue() {
        checkVariable();
        this.parent.checkSolution();
        return CbcLinearProblem.round(this.parent.bestSolution[this.col]);
    }

    /**
     * This implementation check if the {@link #col} property is set to zero (0) and if the parent is nul.
     */
    @Override
    public boolean isDisposed() {
        return this.parent == null || this.col < 0;
    }

    /**
     * Sets the columns bounding type and bounds.
     */
    @Override
    public void setLowerBound(Number lb) {
        checkVariable();
        cbc4j.setColLower(this.parent.lp, this.col, lb != null ? lb.doubleValue() : -this.parent.infinity);
    }

    /**
     * Sets the variable type.
     * 
     * @param type
     */
    public void setType(VarType type) {
        checkVariable();
        if (type.equals(VarType.BOOL)) {
            cbc4j.setInteger(this.parent.lp, this.col);
            cbc4j.setColBounds(this.parent.lp, this.col, 0, 1);
        } else if (type.equals(VarType.INTEGER)) {
            cbc4j.setInteger(this.parent.lp, this.col);
        } else if (type.equals(VarType.REAL)) {
            cbc4j.setContinuous(this.parent.lp, this.col);
        }
    }

    /**
     * Sets the columns bounding type and bounds
     */
    @Override
    public void setUpperBound(Number ub) {
        checkVariable();
        cbc4j.setColUpper(this.parent.lp, this.col, ub != null ? ub.doubleValue() : this.parent.infinity);
    }

    @Override
    public String toString() {
        if (isDisposed()) {
            return "GcbVariable [disposed]"; //$NON-NLS-1$
        }
        return getName();
    }
}
