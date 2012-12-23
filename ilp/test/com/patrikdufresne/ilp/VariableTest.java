package com.patrikdufresne.ilp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.patrikdufresne.ilp.impl.GLPKSolverFactory;

public class VariableTest {

	private LinearProblem lp;

	private Solver solver;

	private static final Integer ONE = Integer.valueOf(1);

	private static final Integer ZERO = Integer.valueOf(0);

	@Before
	public void createEmptyLP() {
		SolverFactory solverFactory = GLPKSolverFactory.instance();
		solver = solverFactory.createSolver();
		lp = solver.createLinearProblem();
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
	@Test
	public void testGetValue_AfterDispose() {

		// Create the model
		Variable x = lp.addIntegerVariable("x", ZERO, null);
		Variable y = lp.addIntegerVariable("y", ZERO, null);
		Linear linear = lp.createLinear();
		linear.add(lp.createTerm(1, y));
		lp.setObjectiveLinear(linear);
		lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
		Constraint constraint1 = lp.addConstraint("-2x + 3y <= 6", new int[] {
				-2, 3 }, new Variable[] { x, y }, null, 6);
		Constraint constraint2 = lp.addConstraint("2x + 3y <= 12", new int[] {
				2, 3 }, new Variable[] { x, y }, null, 12);

		SolverOption option = solver.createSolverOption();
		
		((IBranchingTechniqueLast) option).setBranchingLast(true);

		// Solve the model
		assertTrue(solver.solve(lp, option));
		assertFalse(lp.isPrimalFeasible());
		assertEquals(Status.OPTIMAL, lp.getStatus());
		assertEquals(2, y.getValue().intValue());
		assertEquals(3, x.getValue().intValue());

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
	public void testGetValue_BeforeSolve() {

		// Create the model
		Variable x = lp.addIntegerVariable("x", ZERO, null);
		Variable y = lp.addIntegerVariable("y", ZERO, null);
		Linear linear = lp.createLinear();
		linear.add(lp.createTerm(1, y));
		lp.setObjectiveLinear(linear);
		lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
		Constraint constraint1 = lp.addConstraint("-2x + 3y <= 6", new int[] {
				-2, 3 }, new Variable[] { x, y }, null, 6);
		Constraint constraint2 = lp.addConstraint("2x + 3y <= 12", new int[] {
				2, 3 }, new Variable[] { x, y }, null, 12);

		y.getValue();

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
		assertEquals(Status.UNKNOWN, lp.getStatus());
		assertFalse(lp.isPrimalFeasible());

		var.getValue();

	}

}
