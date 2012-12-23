/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * Solver implementing this interface are suppurting the feasability pump
 * heuristic.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IFeasibilityPumpHeuristic extends Solver {

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
