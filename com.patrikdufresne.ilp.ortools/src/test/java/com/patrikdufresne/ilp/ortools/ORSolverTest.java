package com.patrikdufresne.ilp.ortools;

import com.patrikdufresne.ilp.SolverFactory;
import com.patrikdufresne.ilp.SolverTest;

public class ORSolverTest extends SolverTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return ORSolverFactory.instance();
	}

}
