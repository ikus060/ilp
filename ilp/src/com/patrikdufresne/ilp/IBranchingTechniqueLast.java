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
 * to the nearest integer value.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IBranchingTechniqueLast extends SolverOption {

	/**
	 * Enable or disable this branching technique.
	 * 
	 * @param enabled
	 *            True to enable
	 */
	void setBranchingLast(boolean enabled);

	/**
	 * Check if this branching technique is enabled.
	 * 
	 * @return True if enabled.
	 */
	boolean getBranchingLast();

}
