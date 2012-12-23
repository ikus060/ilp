/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * Solvers implementing this interface are supporting the feasibility pump
 * heuristic.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IFeasibilityPumpHeuristic extends SolverOption {

	/**
	 * Enabled or disable the feasibility pump heuristic of this solver.
	 * 
	 * @param enabled
	 *            True to enabled the heuristic
	 */
	void setFeasibilityPumpHeuristic(boolean enabled);

	/**
	 * Check if feasibility pump heuristic is enabled
	 * 
	 * @return True if the heuristic is enabled
	 */
	boolean getFeasibilityPumpHeuristic();

}
