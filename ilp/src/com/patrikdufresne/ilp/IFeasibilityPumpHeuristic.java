/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * This interface force the solver to use the feasibility pump
 * heuristic.
 * <p>
 * The feasibility pump is a heuristic that finds an initial feasible solution
 * even in certain very hard mixed integer programming problems (MIPs).
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IFeasibilityPumpHeuristic extends SolverOption {

	/**
	 * Enabled or disable the feasibility pump heuristic.
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
