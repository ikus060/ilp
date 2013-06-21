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
package com.patrikdufresne.ilp;

/**
 * Represent a variable in the linear problem.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Variable {

    /**
     * Returns the lower bound.
     * 
     * @return the lower bound or null if not bounded
     */
    Double getLowerBound();

    /**
     * Returns the variable name.
     * 
     * @return the name.
     */
    String getName();

    /**
     * Returns the solution value for a variable.
     * <p>
     * If the problem was solved using the simplex algorithm value or the
     * variable. The dual value may be retrieved using {@link #getDual()}.
     * </p>
     * 
     * @return the solution value or null if the solution is not available
     * @throws ILPException
     *             is no solution are available
     */
    Double getValue();

    /**
     * Returns the variable type.
     * 
     * @return the variable type.
     */
    VarType getType();

    /**
     * Returns the upper bound.
     * 
     * @return the upper bound or null if unbounded
     */
    Double getUpperBound();

    /**
     * Sets the variable lower bound.
     * 
     * @param bound
     *            the new lower bound value or null for −∞
     */
    void setLowerBound(Number bound);

    /**
     * Sets the variable upper bound.
     * 
     * @param bound
     *            the new upper bound or null for +∞
     */
    void setUpperBound(Number bound);

    /**
     * Check if the variable object is disposed.
     * 
     * @return True if the variable is disposed.
     */
    public boolean isDisposed();

    /**
     * Remove the variable from the linear problem. Does nothing if the variable
     * is already disposed.
     * <p>
     * Sub classes must consider the case when this function is called multiple
     * time for the same object.
     */
    void dispose();

}
