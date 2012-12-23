package com.patrikdufresne.ilp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.patrikdufresne.ilp.impl.GLPKSolverFactory;

public class ConstraintTest {

	private static final Integer ONE = Integer.valueOf(1);

	private LinearProblem lp;

	private Solver solver;

	@Before
	public void createEmptyLP() {
		SolverFactory solverFactory = GLPKSolverFactory.instance();
		solver = solverFactory.createSolver();
		lp = solver.createLinearProblem();
	}

	@After
	public void releaseLP() {
		if (lp != null) {
			lp.dispose();
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
		Constraint const1 = lp.addConstraint("const1", lp.createLinear(
				new int[] { 1, 2 }, new Variable[] { var1, var2 }), null, 5);
		assertNotNull(const1);
		Constraint const2 = lp.addConstraint("const2", lp.createLinear(
				new int[] { 3, 4 }, new Variable[] { var1, var2 }), null, 5);
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
		assertNull(linear);

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
		Linear linear = lp.createLinear(new int[] { 1, 2 }, new Variable[] {
				var1, var2 });
		Constraint constraint = lp.addConstraint("test1", linear, ONE, null);
		assertEquals(linear, constraint.getLinear());

		/*
		 * Set empty linear
		 */
		linear = lp.createLinear();
		constraint = lp.addConstraint("test2", linear, ONE, null);
		assertNull(constraint.getLinear());

	}

}
