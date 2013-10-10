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
 * The <code>Status</code> class is an enumeration of the possible solution status values.
 * 
 * @author Patrik Dufresne
 * 
 */
public enum Status {
    /**
     * The solver has found an optimal solution that can be queried with the method {@link Variable#getValue()}.
     */
    OPTIMAL,
    /**
     * The solver has found a feasible solution that can be queried with the method {@link Variable#getValue()}.
     * However, its optimality (or non-optimality) has not been proven, perhaps due to premature termination of the
     * search.
     */
    FEASIBLE,
    /**
     * The solver has determined that the problem is infeasible.
     */
    INFEASIBLE,
    /**
     * The solver has determined that the problem is unbounded.
     */
    UNBOUNDED,
    /**
     * The solver has determine the solution is undefined.
     */
    UNKNOWN

}
