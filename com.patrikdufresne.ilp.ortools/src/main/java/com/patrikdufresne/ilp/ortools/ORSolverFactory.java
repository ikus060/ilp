package com.patrikdufresne.ilp.ortools;

import com.patrikdufresne.ilp.Solver;
import com.patrikdufresne.ilp.SolverFactory;

public class ORSolverFactory implements SolverFactory {

	private static ORSolverFactory instance;

	/**
	 * Private constructor for singleton.
	 */
	private ORSolverFactory() {
		// Nothing to do.
	}

	/**
	 * Return the unique instance of this class.
	 * 
	 * @return
	 */
	public static ORSolverFactory instance() {
		if (instance == null) {
			instance = new ORSolverFactory();
		}
		return instance;
	}

	@Override
	public Solver createSolver() {
		try {
			return new ORSolver();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
