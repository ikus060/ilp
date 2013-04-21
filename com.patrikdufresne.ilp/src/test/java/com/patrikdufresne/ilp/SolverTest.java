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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Patrik Dufresne
 * 
 */
public abstract class SolverTest {

    LinearProblem lp;

    Solver solver;

    private static final Integer ZERO = Integer.valueOf(0);
    private static final Integer ONE = Integer.valueOf(1);

    @Before
    public void createEmptyLP() {
        SolverFactory solverFactory = doGetSolverFactory();
        solver = solverFactory.createSolver();
        lp = solver.createLinearProblem();
    }

    protected abstract SolverFactory doGetSolverFactory();

    @Test
    public void testSolve_withMIP_unfeasibleProblem() {

        Variable var = lp.addBinaryVariable("x");

        Linear linear = lp.createLinear(1, var);
        Constraint constraint = lp.addConstraint("x eq 0.5", linear, 0.5, 0.5);

        assertFalse(solver.solve(lp, solver.createSolverOption()));

        assertTrue(lp.getStatus() == Status.INFEASIBLE || lp.getStatus() == Status.UNKNOWN);

    }

    /**
     * <pre>
     * Maximize 17 * x + 12* y
     *     subject to
     *       10 x + 7 y <= 40
     *          x +   y <=  5
     *     where,
     *       0.0 <= x  integer
     *       0.0 <= y  integer
     * </pre>
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSolve_UsedTwoThread_ExpectSameResult() throws InterruptedException {

        for (int i = 0; i < 2; i++) {

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {

                    SolverFactory solverFactory = doGetSolverFactory();
                    solver = solverFactory.createSolver();
                    lp = solver.createLinearProblem();

                    // Create a linear problem.
                    Variable x = lp.addIntegerVariable("x", ZERO, null);
                    Variable y = lp.addIntegerVariable("y", ZERO, null);
                    Linear linear = lp.createLinear();
                    linear.add(lp.createTerm(17, x));
                    linear.add(lp.createTerm(12, y));
                    lp.setObjectiveLinear(linear);
                    lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
                    lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 }, new Variable[] { x, y }, null, 40);
                    lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 }, new Variable[] { x, y }, null, 5);

                    // Solve the problem.
                    assertTrue(solver.solve(lp, solver.createSolverOption()));

                    // Dispose problem and solver
                    lp.dispose();
                    solver.dispose();
                    lp = null;
                    solver = null;

                }

            });
            t.start();

            while (t.isAlive()) {
                Thread.currentThread().sleep(50);
            }

        }

    }

    /**
     * <pre>
     * Maximize 17 * x + 12* y
     *     subject to
     *       10 x + 7 y <= 40
     *          x +   y <=  5
     *     where,
     *       0.0 <= x  integer
     *       0.0 <= y  integer
     * </pre>
     */
    @Test
    public void testSolve_withMIP_primalFeasibleOptimalProblem() {

        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);

        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(17, x));
        linear.add(lp.createTerm(12, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);

        lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 }, new Variable[] { x, y }, null, 40);
        lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 }, new Variable[] { x, y }, null, 5);

        System.out.println(lp.toString());

        assertTrue(solver.solve(lp, solver.createSolverOption()));

        assertEquals(Status.OPTIMAL, lp.getStatus());

        assertEquals(0, x.getValue().intValue());
        assertEquals(5, y.getValue().intValue());

    }

    /**
     * <pre>
     * Maximize y
     *     subject to
     *       -2x + 3y <= 6
     *       2x + 3y <= 12
     *     where,
     *       0.0 <= x  integer
     *       0.0 <= y  integer
     * </pre>
     */
    @Test
    public void testSolve_withCustomBranchingHeuristic_primalFeasibleOptimalProblem() {

        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);

        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(1, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);

        lp.addConstraint("-2x + 3y <= 6", new int[] { -2, 3 }, new Variable[] { x, y }, null, 6);
        lp.addConstraint("2x + 3y <= 12", new int[] { 2, 3 }, new Variable[] { x, y }, null, 12);

        SolverOption option = solver.createSolverOption();

        if (option instanceof IBranchingTechniqueLast) {

            ((IBranchingTechniqueLast) option).setBranchingLast(true);

            assertTrue(solver.solve(lp, option));

            assertEquals(Status.OPTIMAL, lp.getStatus());

            assertEquals(2, y.getValue().intValue());

        }

    }
}
