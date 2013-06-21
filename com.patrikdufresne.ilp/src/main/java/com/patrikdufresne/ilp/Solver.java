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
 * The Solver class is used to create new problem instance and solve it.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Solver {

    /**
     * Create a new linear problem.
     * 
     * @return the linear problem.
     */
    LinearProblem createLinearProblem();

    /**
     * Create a new solver option with default parameters.
     * 
     * @return
     */
    SolverOption createSolverOption();

    /**
     * This function should be called to free any resources allocated by the
     * solver.
     */
    void dispose();

    /**
     * Solve the linear problem.
     * 
     * @param lp
     *            the linear problem.
     * @param option
     *            the solver option.
     * @return A Boolean value reporting whether a feasible solution has been
     *         found. This solution is not necessarily optimal. If
     *         <code>false</code> is returned, a feasible solution may still be
     *         present, but the solver has not been able to prove its
     *         feasibility.
     */
    boolean solve(LinearProblem lp, SolverOption option);

}
