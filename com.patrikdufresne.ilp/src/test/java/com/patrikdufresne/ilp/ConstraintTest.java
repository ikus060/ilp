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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class ConstraintTest {

    private static final Double ONE = Double.valueOf(1);

    private static final Double ZERO = Double.valueOf(1);

    protected LinearProblem lp;

    protected Solver solver;

    @Before
    public void createEmptyLP() {
        SolverFactory solverFactory = doGetSolverFactory();
        solver = solverFactory.createSolver();
        lp = solver.createLinearProblem();
    }

    protected abstract SolverFactory doGetSolverFactory();

    @After
    public void releaseLP() {
        if (lp != null) {
            lp.dispose();
        }
    }

    /**
     * Check bounds setter and getters for an integer variable.
     */
    @Test
    public void testBounds() {

        Constraint myConstraint = lp.addConstraint("my_constraint");

        // By default expect null..null bounds
        assertEquals(null, myConstraint.getLowerBound());
        assertEquals(null, myConstraint.getUpperBound());

        // Sets fixed bounds
        myConstraint.setLowerBound(Integer.valueOf(10));
        myConstraint.setUpperBound(Integer.valueOf(20));
        assertEquals(Double.valueOf(10), myConstraint.getLowerBound());
        assertEquals(Double.valueOf(20), myConstraint.getUpperBound());

        // Set unbounded lower bound.
        myConstraint.setLowerBound(null);
        assertEquals(null, myConstraint.getLowerBound());

        // Set unbounded upper bound.
        myConstraint.setUpperBound(null);
        assertEquals(null, myConstraint.getUpperBound());

    }

    /**
     * Check if it's possible to create two constraint with the same name.
     */
    @Test
    public void testCreate_WithSameName() {
        Linear linear = lp.createLinear();
        lp.addConstraint("test2", linear, ONE, null);
        try {
            lp.addConstraint("test2", linear, ONE, null);
            fail("Should throw an exception");
        } catch (ILPException e) {
            assertEquals(ILPException.ERROR_DUPLICATE_NAME, e.code);
        }
    }

    @Test
    public void testCreate_WithoutName() {

        Linear linear = lp.createLinear();
        try {
            lp.addConstraint(null, linear, ONE, null);
            fail("Should throw an exception");
        } catch (ILPException e) {
            assertEquals(ILPException.ERROR_DUPLICATE_NAME, e.code);
        }

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
         * Add constraint
         */
        Constraint const1 = lp.addConstraint("const1", lp.createLinear(new int[] { 1, 2 }, new Variable[] { var1, var2 }), null, 5);
        assertNotNull(const1);
        Constraint const2 = lp.addConstraint("const2", lp.createLinear(new int[] { 3, 4 }, new Variable[] { var1, var2 }), null, 5);
        assertNotNull(const2);

        assertEquals(2, lp.getConstraints().size());
        assertEquals(const1, lp.getConstraints().toArray()[0]);
        assertEquals(const2, lp.getConstraints().toArray()[1]);

        /*
         * Dispose first constraint.
         */
        const1.dispose();

        assertEquals(1, lp.getConstraints().size());
        assertEquals(const2, lp.getConstraints().toArray()[0]);
        assertEquals("const2", const2.getName());

        /*
         * Dispose variables
         */
        var1.dispose();
        var2.dispose();
        assertEquals(0, lp.getVariables().size());

        /*
         * Check constraint value.
         */
        Linear linear = const2.getLinear();
        assertNotNull(linear);
        assertEquals(0, linear.size());

        /*
         * Dispose the last linear
         */
        const2.dispose();
        assertEquals(0, lp.getConstraints().size());

    }

    /**
     * Check usage of getLinear function.
     */
    @Test
    public void testGetLinear() {

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
         * Set linear / Get linear
         */
        Linear linear = lp.createLinear(new int[] { 1, 2 }, new Variable[] { var1, var2 });
        Constraint constraint = lp.addConstraint("test1", linear, ONE, null);
        assertEquals(linear, constraint.getLinear());

        /*
         * Set empty linear
         */
        linear = lp.createLinear();
        constraint = lp.addConstraint("test2", linear, ONE, null);
        linear = constraint.getLinear();
        assertNotNull(linear);
        assertEquals(0, linear.size());

        /*
         * Sets the linear with different vars
         */
        linear = lp.createLinear(new int[] { 1, 2 }, new Variable[] { var1, var2 });
        constraint = lp.addConstraint("test3", linear, ONE, null);
        assertEquals(linear, constraint.getLinear());
        // Update the linear
        linear = lp.createLinear(new int[] { 4, 5 }, new Variable[] { var1, var2 });
        constraint.setLinear(linear);
        assertEquals(linear, constraint.getLinear());
        // Update the linear
        linear = lp.createLinear(new int[] { 3 }, new Variable[] { var1 });
        constraint.setLinear(linear);
        assertEquals(linear, constraint.getLinear());
        // Update the linear
        linear = lp.createLinear(new int[] { 5 }, new Variable[] { var2 });
        constraint.setLinear(linear);
        assertEquals(linear, constraint.getLinear());

    }

    /**
     * Check if it's possible to re-create a constraint with the same name if the original constraint as been disposed.
     */
    @Test
    public void testCreateDispose_WithSameName() {
        Linear linear = lp.createLinear();
        Constraint c = lp.addConstraint("test2", linear, ONE, null);
        c.dispose();
        c = lp.addConstraint("test2", linear, ONE, null);
    }

}
