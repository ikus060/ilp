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
package com.patrikdufresne.ilp.util;

import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ImmutableLinear;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.LinearProblem;

/**
 * This class represent a snapshot of a constraint. This may be used to temporarily release a constraint and restore it
 * later on.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ConstraintSnapshot {

    /**
     * Create a snapshot from the given constraint object.
     * 
     * @param constraint
     *            the constraint to be snapshot.
     * 
     * @return the snaptshot.
     */
    public static ConstraintSnapshot create(Constraint constraint) {
        if (constraint == null || constraint.isDisposed()) {
            throw new IllegalArgumentException();
        }
        // Capture the name
        String name = constraint.getName();
        // Capture the bounds
        Bound bound = new Bound(constraint.getLowerBound(), constraint.getUpperBound());
        // Capture the linear
        Linear linear = constraint.getLinear();
        return new ConstraintSnapshot(name, linear, bound);
    }

    private Bound bound;

    private ImmutableLinear linear;

    private String name;

    /**
     * Create a new snapshot.
     * 
     * @param name
     *            the constraint's name
     * @param linear
     *            the constraint's linear or null if empty
     * @param bound
     *            the constraint bounds.
     */
    public ConstraintSnapshot(String name, Linear linear, Bound bound) {
        // The linear may be null if the constraint was empty. It's strange, but
        // we don't want to throw exception for this reason.
        if (bound == null) {
            throw new IllegalArgumentException();
        }
        if (linear == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.linear = new ImmutableLinear(linear);
        this.bound = bound;
    }

    /**
     * Return the linear representing the constraints.
     */
    public ImmutableLinear getLinear() {
        return this.linear;
    }

    /**
     * Return the lower bound of this snapshot.
     * 
     * @return the lower bound value.
     */
    public Number getLower() {
        return this.bound.getLower();
    }

    /**
     * Return the name of of this snapshot.
     * 
     * @return the name value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the upper bound of this snapshot.
     * 
     * @return the upper bound value.
     */
    public Number getUpper() {
        return this.bound.getUpper();
    }

    /**
     * Create a new constraints to represent the snapshot within the given linear problem.
     * 
     * @param lp
     *            the linear problem where to create the constraint.
     * @return the constraint
     */
    public Constraint restore(LinearProblem lp) {
        if (lp == null) {
            throw new IllegalArgumentException();
        }
        // Create the constraint
        return lp.addConstraint(this.name, this.linear, this.bound.getLower(), this.bound.getUpper());
    }

}
