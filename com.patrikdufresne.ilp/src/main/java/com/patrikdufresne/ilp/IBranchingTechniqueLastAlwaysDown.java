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
 * This interface force the solver to use a specific algorithm to select the
 * next node and it's direction within the branch-and-bound algorithm.
 * <p>
 * This branching technique will select the last fractional variables and round
 * it down.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IBranchingTechniqueLastAlwaysDown extends SolverOption {

    /**
     * Enable or disable this branching technique.
     * 
     * @param enabled
     *            True to enable
     */
    void setBranchingLastAlwaysDown(boolean enabled);

    /**
     * Check if this branching technique is enabled.
     * 
     * @return True if enabled.
     */
    boolean getBranchingLastAlwaysDown();

}
