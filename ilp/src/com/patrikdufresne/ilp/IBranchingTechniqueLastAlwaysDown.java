/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * This interface force the solver to use a specific algorithm to select the
 * next node and it's direction within the branch-and-bound algorithm.
 * <p>
 * This branching technique will select the last fractional variables and round
 * it down.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IBranchingTechniqueLastAlwaysDown extends SolverOption {

	/**
	 * Enable or disable this branching technique.
	 * 
	 * @param enabled
	 *            True to enable
	 */
	void setBranchingLastAlwaysDown(boolean enabled);

	/**
	 * Check if this branching technique is enabled.
	 * 
	 * @return True if enabled.
	 */
	boolean getBranchingLastAlwaysDown();

}