package com.patrikdufresne.ilp.glpk;

import com.patrikdufresne.ilp.SolverFactory;
import com.patrikdufresne.ilp.SolverTest;

public class GLPKSolverTest extends SolverTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return GLPKSolverFactory.instance();
	}

}
