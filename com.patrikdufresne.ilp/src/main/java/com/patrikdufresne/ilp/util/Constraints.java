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
import com.patrikdufresne.ilp.Linear;

/**
 * Utility class to manipulates constraints and associated snapshot.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Constraints {

    /**
     * Check if the constraint is already feasible with the given value
     * snapshot.
     * 
     * @param constraint
     *            the constraint to check
     * @param snapshot
     *            the value snapshot used to evaluate the feasibility
     * @return True if the constraint is satisfied
     */
    public static boolean isSatisfied(Constraint constraint, ValueSnapshot snapshot) {
        if (constraint == null || snapshot == null) {
            throw new IllegalArgumentException();
        }
        // Compute the linear value
        Linear linear = constraint.getLinear();
        double value = linear == null ? 0 : Linears.compute(linear, snapshot).doubleValue();
        // Check if the value is in range
        return (constraint.getLowerBound() == null || constraint.getLowerBound().doubleValue() <= value)
                && (constraint.getUpperBound() == null || constraint.getUpperBound().doubleValue() >= value);
    }

    /**
     * Take a snapshot of the given constraints and then dispose it.
     * 
     * @param constraint
     *            the constraint to be release.
     * @return the snapshot representing the constraints
     */
    public static ConstraintSnapshot release(Constraint constraint) {
        if (constraint == null || constraint.isDisposed()) {
            throw new IllegalArgumentException();
        }
        ConstraintSnapshot snapshot = ConstraintSnapshot.create(constraint);
        constraint.dispose();
        return snapshot;
    }

    /**
     * Private constructor to avoid creating instances of utility class.
     */
    private Constraints() {

    }
}
