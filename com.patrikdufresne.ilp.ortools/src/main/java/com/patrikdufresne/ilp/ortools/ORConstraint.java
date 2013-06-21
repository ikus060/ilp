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

import com.google.ortools.linearsolver.MPConstraint;
import com.patrikdufresne.ilp.ConcreteLinear;
import com.patrikdufresne.ilp.ConcreteTerm;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Term;

public class ORConstraint implements Constraint {
    /**
     * Reference to the constraint.
     */
    MPConstraint c;
    /**
     * Reference to the parent linear
     */
    ORLinearProblem parent;

    /**
     * Create a new constraint instance
     * 
     * @param parent
     *            the linear problem
     * @param name
     *            the constraint's name.
     */
    public ORConstraint(ORLinearProblem parent, String name) {
        this.parent = parent;
        if (parent == null) {
            throw new NullPointerException();
        }
        parent.checkProblem();

        // Call the parent function to complete the work
        parent.addConstraint(this, name);
    }

    /**
     * Check the state of the constraint. Throw an exception if the variable is disposed.
     */
    private void checkConstraint() {
        if (isDisposed()) {
            throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
        }
        this.parent.checkProblem();
    }

    /**
     * Return a linear from the coefficient list.
     */
    @Override
    public Linear getLinear() {
        checkConstraint();
        Linear linear = new ConcreteLinear();
        for (ORVariable var : parent.getVariables()) {
            double coef = c.getCoefficient(var.v);
            if (coef != 0) {
                linear.add(new ConcreteTerm(coef, var));
            }
        }
        return linear;
    }

    /**
     * Returns the lower bound or null if unbounded.
     */
    @Override
    public Number getLowerBound() {
        checkConstraint();
        double lower = this.c.lb();
        if (lower == -ORLinearProblem.INFINITY) {
            return null;
        }
        return Double.valueOf(lower);
    }

    /**
     * Return the constraint name.
     */
    @Override
    public String getName() {
        checkConstraint();
        return this.c.name();
    }

    /**
     * Return the constraint value.
     */
    @Override
    public Number getValue() {
        checkConstraint();
        return this.c.dualValue();
    }

    @Override
    public Number getUpperBound() {
        checkConstraint();
        double upper = this.c.ub();
        if (upper == ORLinearProblem.INFINITY) {
            return null;
        }
        return Double.valueOf(upper);
    }

    /**
     * Sets the linear constraint.
     */
    @Override
    public void setLinear(Linear linear) {
        checkConstraint();
        c.Clear();
        this.parent.status = null;
        for (Term term : linear) {
            c.setCoefficient(((ORVariable) term.getVariable()).v, term.getCoefficient().doubleValue());
        }
    }

    /**
     * Sets the lowerbound.
     */
    @Override
    public void setLowerBound(Number bound) {
        checkConstraint();
        if (bound == null) {
            this.c.setLb(-ORLinearProblem.INFINITY);
        } else {
            this.c.setLb(bound.doubleValue());
        }
    }

    /**
     * Sets the upper bound.
     */
    @Override
    public void setUpperBound(Number bound) {
        checkConstraint();
        if (bound == null) {
            this.c.setUb(ORLinearProblem.INFINITY);
        } else {
            this.c.setUb(bound.doubleValue());
        }
    }

    /**
     * Return True if
     */
    @Override
    public boolean isDisposed() {
        return this.c == null || this.parent == null;
    }

    /**
     * Build the linear and check it's size.
     */
    @Override
    public boolean isEmpty() {
        return getLinear().size() == 0;
    }

    /**
     * Delete the constraints
     */
    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        this.parent.removeConstraint(this);
    }

}
