package com.patrikdufresne.ilp.glpk;

import com.patrikdufresne.ilp.ConstraintTest;
import com.patrikdufresne.ilp.SolverFactory;

public class GLPKConstraintTest extends ConstraintTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return GLPKSolverFactory.instance();
	}

}
