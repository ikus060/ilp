/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * <code>ISolverFactory</code> instances manufacture {@link Solver}.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface SolverFactory {

	/**
	 * Create a new solver object.
	 * 
	 * @return the solver
	 */
	Solver createSolver();

}
