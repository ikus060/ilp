package com.patrikdufresne.ilp.glpk;

import com.patrikdufresne.ilp.LinearProblemTest;
import com.patrikdufresne.ilp.SolverFactory;

public class GLPKLinearProblemTest extends LinearProblemTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return GLPKSolverFactory.instance();
	}

}
