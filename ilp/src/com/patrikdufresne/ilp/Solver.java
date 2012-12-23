/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
