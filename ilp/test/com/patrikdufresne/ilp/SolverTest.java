package com.patrikdufresne.ilp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.patrikdufresne.ilp.impl.GLPKSolverFactory;

public class SolverTest {

	private LinearProblem lp;

	private Solver solver;

	private static final Integer ZERO = Integer.valueOf(0);
	private static final Integer ONE = Integer.valueOf(1);

	@Before
	public void createEmptyLP() {
		SolverFactory solverFactory = GLPKSolverFactory.instance();
		solver = solverFactory.createSolver();
		lp = solver.createLinearProblem();
	}

	/**
	 * Create a MIP problem and use the feasibility check (minisat).
	 * 
	 * <pre>
	 * (x1 or x2) and (not x2 or x3 or not x4) and (not x1 or x4)
	 * </pre>
	 */
	@Test
	public void testSolve_withIntegerFeasibilityCheck() {

		Variable x1 = lp.addBinaryVariable("x1");
		Variable x2 = lp.addBinaryVariable("x2");
		Variable x3 = lp.addBinaryVariable("x3");
		Variable x4 = lp.addBinaryVariable("x4");

		lp.addConstraint("x1 or x2", new int[] { 1, 1 }, new Variable[] { x1,
				x2 }, ONE, null);
		lp.addConstraint("not x2 or x3 or not x4", new int[] { -1, 1, -1 }, new Variable[] {
				x2, x3, x4 }, Integer.valueOf(-1), null);
		lp.addConstraint("not x1 or x4", new int[] { -1, -1 }, new Variable[] {
				x1, x4 }, ZERO, null);
		
		
		SolverOption option =  solver.createSolverOption();
		((IIntegerFeasibilityCheck)option).setIntegerFeasibilitCheck(true);
		
		assertTrue(solver.solve(lp, option));

		assertEquals(Status.FEASIBLE, lp.getStatus());
		
		assertTrue(
		// 0 1 0 0 
		(x1.getValue().equals(ZERO) && x2.getValue().equals(ONE) &&  x4.getValue().equals(ZERO) &&  x4.getValue().equals(ZERO)) ||
		// 0 1 1 0
		(x1.getValue().equals(ZERO) && x2.getValue().equals(ONE) &&  x4.getValue().equals(ONE) &&  x4.getValue().equals(ZERO)) ||
		// 1 0 0 1
		(x1.getValue().equals(ONE) && x2.getValue().equals(ZERO) &&  x4.getValue().equals(ZERO) &&  x4.getValue().equals(ONE)) ||
		// 0 1 1 1
		(x1.getValue().equals(ZERO) && x2.getValue().equals(ONE) &&  x4.getValue().equals(ONE) &&  x4.getValue().equals(ONE)) ||
		// 1 1 1 1
		(x1.getValue().equals(ONE) && x2.getValue().equals(ONE) &&  x4.getValue().equals(ONE) &&  x4.getValue().equals(ONE)));

	}
	
	/**
	 * Create a MIP problem and use the feasibility check (minisat).
	 * 
	 * <pre>
	 * (x1 or x2) and not x1 and not x2
	 * </pre>
	 */
	@Test
	public void testSolve_withFeasibilityCheck_notFeasible() {

		Variable x1 = lp.addBinaryVariable("x1");
		Variable x2 = lp.addBinaryVariable("x2");

		lp.addConstraint("x1 or x2", new int[] { 1, 1 }, new Variable[] { x1,
				x2 }, ONE, null);
		lp.addConstraint("not x1", new int[] { -1 }, new Variable[] {
				x1}, ZERO, null);
		lp.addConstraint("not x2", new int[] { -1 }, new Variable[] {
				x2}, ZERO, null);
		
		
		SolverOption option =  solver.createSolverOption();
		((IIntegerFeasibilityCheck)option).setIntegerFeasibilitCheck(true);
		
		assertFalse(solver.solve(lp, option));

		assertEquals(Status.INFEASIBLE, lp.getStatus());

	}

	@Test
	public void testSolve_withMIP_unfeasibleProblem() {

		Variable var = lp.addBinaryVariable("x");

		Linear linear = lp.createLinear(1, var);
		Constraint constraint = lp.addConstraint("x=0.5", linear, 0.5, 0.5);

		assertFalse(solver.solve(lp, solver.createSolverOption()));

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

					SolverFactory solverFactory = GLPKSolverFactory.instance();
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

		lp.addConstraint("10 x + 7 y <= 40", new int[] { 10, 7 },
				new Variable[] { x, y }, null, 40);
		lp.addConstraint("   x +   y <=  5", new int[] { 11, 1 },
				new Variable[] { x, y }, null, 5);

		assertTrue(solver.solve(lp, solver.createSolverOption()));

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

		SolverOption option = solver.createSolverOption();

		((IBranchingTechniqueLast) option).setBranchingLast(true);

		assertTrue(solver.solve(lp, option));

		assertFalse(lp.isPrimalFeasible());

		assertEquals(Status.OPTIMAL, lp.getStatus());

		assertEquals(2, y.getValue().intValue());

	}
}
