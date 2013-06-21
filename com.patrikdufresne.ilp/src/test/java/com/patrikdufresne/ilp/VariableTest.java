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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public abstract class VariableTest {

    private static final Double ONE = Double.valueOf(1);

    private static final Double ZERO = Double.valueOf(0);

    protected LinearProblem lp;

    protected Solver solver;

    @Before
    public void createEmptyLP() {
        SolverFactory solverFactory = doGetSolverFactory();
        solver = solverFactory.createSolver();
        lp = solver.createLinearProblem();
    }

    protected abstract SolverFactory doGetSolverFactory();

    /**
     * Check if an exception is thrown when creating two variables with the same name.
     */
    @Test
    public void testCreate_WithSameName() {
        lp.addBinaryVariable("test");
        try {
            lp.addBinaryVariable("test");
            fail("Should throw an exception");
        } catch (ILPException e) {
            assertEquals(ILPException.ERROR_DUPLICATE_NAME, e.code);
        }
    }

    @Test
    public void testCreate_WithoutName() {
        try {
            lp.addBinaryVariable(null);
            fail("Should throw an exception");
        } catch (ILPException e) {
            assertEquals(ILPException.ERROR_DUPLICATE_NAME, e.code);
        }
    }

    @Test
    public void testBounds_withFixedValue_TypeShouldBeFixed() {

        Variable var = lp.addBinaryVariable("test");

        assertEquals(VarType.BOOL, var.getType());

        var.setLowerBound(ONE);
        var.setUpperBound(ONE);

        assertEquals(ONE, var.getLowerBound());

        assertEquals(ONE, var.getUpperBound());

        assertEquals(VarType.INTEGER, var.getType());

    }

    /**
     * Check bounds setter and getters for an integer variable.
     */
    @Test
    public void testBounds_WithIntegerVariable() {

        Variable myVar = lp.addVariable("my_var", VarType.INTEGER);
        assertNotNull(myVar);
        assertEquals(VarType.INTEGER, myVar.getType());

        // By default expect null..null bounds
        assertEquals(null, myVar.getLowerBound());
        assertEquals(null, myVar.getUpperBound());

        // Sets fixed bounds
        myVar.setLowerBound(Integer.valueOf(10));
        myVar.setUpperBound(Integer.valueOf(20));
        assertEquals(Double.valueOf(10), myVar.getLowerBound());
        assertEquals(Double.valueOf(20), myVar.getUpperBound());

        // Set unbounded lower bound.
        myVar.setLowerBound(null);
        assertEquals(null, myVar.getLowerBound());

        // Set unbounded upper bound.
        myVar.setUpperBound(null);
        assertEquals(null, myVar.getUpperBound());

    }

    @Test
    public void testDispose() {

        /*
         * Add variables.
         */
        Variable var1 = lp.addBinaryVariable("dummy1");
        assertNotNull(var1);

        Variable var2 = lp.addBinaryVariable("dummy2");
        assertNotNull(var2);

        assertEquals(2, lp.getVariables().size());
        assertEquals(var1, lp.getVariables().toArray()[0]);
        assertEquals(var2, lp.getVariables().toArray()[1]);

        /*
         * Dispose first variable.
         */
        var1.dispose();

        assertEquals(1, lp.getVariables().size());
        assertEquals(var2, lp.getVariables().toArray()[0]);
        assertEquals("dummy2", var2.getName());

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
    @Test(expected = ILPException.class)
    public void testGetValue_AfterDispose() {

        // Create the model
        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);
        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(1, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
        Constraint constraint1 = lp.addConstraint("-2x + 3y <= 6", new int[] { -2, 3 }, new Variable[] { x, y }, null, 6);
        Constraint constraint2 = lp.addConstraint("2x + 3y <= 12", new int[] { 2, 3 }, new Variable[] { x, y }, null, 12);

        SolverOption option = solver.createSolverOption();

        System.out.println(lp.toString());

        // Solve the model
        assertTrue(solver.solve(lp, option));
        assertEquals(Status.OPTIMAL, lp.getStatus());
        assertEquals(2, y.getValue().intValue());
        assertTrue(3 == x.getValue().intValue() || 2 == x.getValue().intValue());

        // Dispose a variable
        x.dispose();
        constraint1.dispose();
        constraint2.dispose();
        assertEquals(2, y.getValue().intValue());

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
    @Test(expected = ILPException.class)
    public void testGetValue_AfterUnfeasibleSolve() {

        // Build the problem
        Variable var = lp.addBinaryVariable("x");
        Linear linear = lp.createLinear(1, var);
        Constraint constraint = lp.addConstraint("x=0.5", linear, 0.5, 0.5);

        // solve the problem
        assertFalse(solver.solve(lp, solver.createSolverOption()));
        assertTrue(lp.getStatus() == Status.INFEASIBLE || lp.getStatus() == Status.UNKNOWN);

        var.getValue();

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
    @Test(expected = ILPException.class)
    public void testGetValue_BeforeSolve() {

        // Create the model
        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);
        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(1, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
        Constraint constraint1 = lp.addConstraint("-2x + 3y <= 6", new int[] { -2, 3 }, new Variable[] { x, y }, null, 6);
        Constraint constraint2 = lp.addConstraint("2x + 3y <= 12", new int[] { 2, 3 }, new Variable[] { x, y }, null, 12);

        y.getValue();

    }
}
