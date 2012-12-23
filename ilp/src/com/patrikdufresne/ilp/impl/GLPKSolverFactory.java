/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.impl;

import com.patrikdufresne.ilp.SolverFactory;
import com.patrikdufresne.ilp.Solver;

/**
 * This implementation create instance of GLPK solver.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKSolverFactory implements SolverFactory {

	private static GLPKSolverFactory instance;

	/**
	 * Private constructor for singleton.
	 */
	private GLPKSolverFactory() {
		// Nothing to do.
	}

	/**
	 * Return the unique instance of this class.
	 * 
	 * @return
	 */
	public static GLPKSolverFactory instance() {
		if (instance == null) {
			instance = new GLPKSolverFactory();
		}
		return instance;
	}

	@Override
	public Solver createSolver() {
		return new GLPKSolver();
	}

}
