package com.patrikdufresne.ilp.ortools;

import com.patrikdufresne.ilp.ConstraintTest;
import com.patrikdufresne.ilp.SolverFactory;

public class ORConstraintTest extends ConstraintTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return ORSolverFactory.instance();
	}

}
