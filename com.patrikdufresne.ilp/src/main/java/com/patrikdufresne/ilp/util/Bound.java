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
import com.patrikdufresne.ilp.Variable;

/**
 * A structure used to store the variable bound.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Bound {

    /**
     * Create a new bound object from the given constraint bounds.
     * 
     * @param constraint
     *            the constraint to be capture
     * @return the bound
     */
    public static Bound create(Constraint constraint) {
        return new Bound(constraint.getLowerBound(), constraint.getUpperBound());
    }

    /**
     * Create a new bound object from the given variable bounds.
     */
    public static Bound create(Variable variable) {
        return new Bound(variable.getLowerBound(), variable.getUpperBound());
    }

    private Number lower;

    private Number upper;

    /**
     * Create a new bound instance.
     * 
     * @param lower
     *            the lower bound
     * @param upper
     *            the upper bound
     */
    public Bound(Number lower, Number upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Bound other = (Bound) obj;
        if (this.lower == null) {
            if (other.lower != null) return false;
        } else if (!this.lower.equals(other.lower)) return false;
        if (this.upper == null) {
            if (other.upper != null) return false;
        } else if (!this.upper.equals(other.upper)) return false;
        return true;
    }

    /**
     * Return the lower bound
     * 
     * @return the lower bound value.
     */
    public Number getLower() {
        return this.lower;
    }

    /**
     * Return the upper bound
     * 
     * @return the upper bound value
     */
    public Number getUpper() {
        return this.upper;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.lower == null) ? 0 : this.lower.hashCode());
        result = prime * result + ((this.upper == null) ? 0 : this.upper.hashCode());
        return result;
    }

    /**
     * Apply the bound to the given constraint
     * 
     * @param constraint
     */
    public void restore(Constraint constraint) {
        constraint.setLowerBound(lower);
        constraint.setUpperBound(upper);
    }

    /**
     * Apply the bound to the given variable.
     * 
     * @param variable
     *            the variable
     */
    public void restore(Variable variable) {
        variable.setLowerBound(lower);
        variable.setUpperBound(upper);
    }

    @Override
    public String toString() {
        return "Bound:[" + this.lower + ".." + this.upper + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}