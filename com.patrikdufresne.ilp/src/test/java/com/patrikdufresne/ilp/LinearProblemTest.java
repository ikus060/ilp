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
import java.util.Iterator;

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

    @Test
    public void testAddConstraints() {
        Variable x = lp.addIntegerVariable("x", ZERO, null);
        Variable y = lp.addIntegerVariable("y", ZERO, null);
        Linear l = lp.createLinear(new int[] { 23, 15 }, new Variable[] { x, y });
        Constraint c1 = lp.addConstraint("name1", l, -45, 78);
        assertEquals("name1", c1.getName());
        assertEquals(l, c1.getLinear());
        assertEquals(-45, c1.getLowerBound().doubleValue(), 0.0001);
        assertEquals(78, c1.getUpperBound().doubleValue(), 0.0001);
    }

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
        Variable dummy1 = lp.addBinaryVariable("dummy1");
        Variable dummy2 = lp.addBinaryVariable("dummy2");
        Variable dummy3 = lp.addBinaryVariable("dummy3");
        double[] coef = new double[] { 13, 24 };
        Variable[] var = new Variable[] { dummy1, dummy3 };
        lp.setObjectiveLinear(lp.createLinear(coef, var));
        Linear linear;
        assertNotNull(linear = lp.getObjectiveLinear());
        assertEquals(2, linear.size());
        Iterator<Term> iter = linear.iterator();
        while (iter.hasNext()) {
            boolean found = false;
            Term t = iter.next();
            for (int i = 0; i < coef.length; i++) {
                found |= t.getVariable().equals(var[i]) && t.getCoefficient().doubleValue() == coef[i];
            }
            assertTrue(found);
        }

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
     * Check if the status of the linear problem is unchanged after touching the bound of a variable.
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
        x.setUpperBound(null);

        // Check if the state is unchanged.
        assertEquals(Status.OPTIMAL, lp.getStatus());

        // Dispose a variable
        y.dispose();

        // Check if the state is unchanged.
        assertEquals(Status.OPTIMAL, lp.getStatus());
    }

}
