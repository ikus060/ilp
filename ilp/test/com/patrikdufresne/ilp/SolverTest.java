package com.patrikdufresne.ilp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.patrikdufresne.ilp.impl.GLPKSolverFactory;

public class SolverTest {

	private LinearProblem lp;

	private Solver solver;

	private static final Integer ZERO = Integer.valueOf(0);

	@Before
	public void createEmptyLP() {
		ISolverFactory solverFactory = GLPKSolverFactory.instance();
		solver = solverFactory.createSolver();
		lp = solver.createLinearProblem();
	}

	@Test
	public void testSolve_withMIP_unfeasibleProblem() {

		Variable var = lp.addBinaryVariable("x");

		Linear linear = lp.createLinear(1, var);
		Constraint constraint = lp.addConstraint("x=0.5", linear, 0.5, 0.5);

		assertFalse(solver.solve(lp));

		assertEquals(Status.UNKNOWN, lp.getStatus());

		assertFalse(lp.isPrimalFeasible());

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
	public void testSolve_UsedTwoThread_ExpectSameResult()
			throws InterruptedException {

		for (int i = 0; i < 2; i++) {

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					ISolverFactory solverFactory = GLPKSolverFactory.instance();
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
					lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 },
							new Variable[] { x, y }, null, 40);
					lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 },
							new Variable[] { x, y }, null, 5);

					// Solve the problem.
					assertTrue(solver.solve(lp));

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

		lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 },
				new Variable[] { x, y }, null, 40);
		lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 },
				new Variable[] { x, y }, null, 5);

		assertTrue(solver.solve(lp));

		assertFalse(lp.isPrimalFeasible());

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

		lp.addConstraint("-2x + 3y <= 6", new int[] { -2, 3 }, new Variable[] {
				x, y }, null, 6);
		lp.addConstraint("2x + 3y <= 12", new int[] { 2, 3 }, new Variable[] {
				x, y }, null, 12);

		((IBranchingTechniqueLast) solver).setBranchingLast(true);

		assertTrue(solver.solve(lp));

		assertFalse(lp.isPrimalFeasible());

		assertEquals(Status.OPTIMAL, lp.getStatus());

		assertEquals(2, y.getValue().intValue());

	}
}
