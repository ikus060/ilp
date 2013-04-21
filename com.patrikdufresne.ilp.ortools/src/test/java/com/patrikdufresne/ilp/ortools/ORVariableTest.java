package com.patrikdufresne.ilp.ortools;

import com.patrikdufresne.ilp.SolverFactory;
import com.patrikdufresne.ilp.VariableTest;

public class ORVariableTest extends VariableTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return ORSolverFactory.instance();
	}

}
