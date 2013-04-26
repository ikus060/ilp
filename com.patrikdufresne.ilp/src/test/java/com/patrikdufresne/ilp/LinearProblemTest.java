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
/**
 * 
 */
package com.patrikdufresne.ilp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Patrik Dufresne
 * 
 */
public abstract class LinearProblemTest {

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
     * Test method for {@link com.patrikdufresne.ilp.LinearProblem#addBinaryVariable(java.lang.String)} .
     */
    @Test
    public void testAddBinaryVariable() {

        Variable var = lp.addBinaryVariable("x");

        assertEquals(VarType.BOOL, var.getType());
        assertEquals(0, var.getLowerBound().intValue());
        assertEquals(1, var.getUpperBound().intValue());

    }

    /**
     * Test method for {@link com.patrikdufresne.ilp.LinearProblem#getConstraints()}.
     */
    @Test
    public void testGetConstraints() {
        Constraint c1 = lp.addConstraint("c1");
        Constraint c2 = lp.addConstraint("c2");

        Collection<? extends Constraint> collection = lp.getConstraints();

        assertEquals("Wrong number of constraint", 2, collection.size());

        assertTrue("Constraint object not found.", collection.contains(c1));

        assertTrue("Constraint object not found.", collection.contains(c2));

    }

    /**
     * Check if an invalid direction is throwing exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetObjectiveDirection_WithInvalidDirection() {

        this.lp.setObjectiveDirection(2);

    }

    /**
     * Test method for {@link com.patrikdufresne.ilp.LinearProblem#setObjectiveLinear(com.patrikdufresne.ilp.Linear)} .
     */
    @Test
    public void testSetObjectiveLinear() {

        // Set dummy objective
        Variable dummy = lp.addBinaryVariable("dummy");
        lp.setObjectiveLinear(lp.createLinear(1, dummy));
        Linear linear;
        assertNotNull(linear = lp.getObjectiveLinear());
        assertEquals(1, linear.size());
        Term term = linear.iterator().next();
        assertEquals(1, term.getCoefficient().intValue());
        assertEquals(dummy, term.getVariable());

        // Set empty objective
        lp.setObjectiveLinear(lp.createLinear());
        assertNull(lp.getObjectiveLinear());

        // Remove objective
        lp.setObjectiveLinear(null);
        assertNull(lp.getObjectiveLinear());

    }

    /**
     * Check if an invalid direction is throwing exception.
     */
    @Test
    public void testSetObjectiveDirection() {

        this.lp.setObjectiveDirection(LinearProblem.MINIMIZE);
        assertEquals(LinearProblem.MINIMIZE, this.lp.getObjectiveDirection());

        this.lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
        assertEquals(LinearProblem.MAXIMIZE, this.lp.getObjectiveDirection());

    }

    /**
     * Check if the status of the linear problem is unknown after touching the bound of a variable.
     */
    @Test
    public void testGetStatus_AfterTouchingVariable() {

        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);

        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(17, x));
        linear.add(lp.createTerm(12, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
        lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 }, new Variable[] { x, y }, null, 40);
        lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 }, new Variable[] { x, y }, null, 5);

        assertTrue(solver.solve(lp, solver.createSolverOption()));
        assertEquals(Status.OPTIMAL, lp.getStatus());
        assertEquals(0, x.getValue().intValue());
        assertEquals(5, y.getValue().intValue());

        // Touch a variable.
        x.setLowerBound(null);

        // Check if the state is unknown.
        assertEquals(Status.UNKNOWN, lp.getStatus());

    }

}
