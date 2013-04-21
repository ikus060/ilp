package com.patrikdufresne.ilp.ortools;


import com.patrikdufresne.ilp.LinearProblemTest;
import com.patrikdufresne.ilp.SolverFactory;

public class ORLinearProblemTest extends LinearProblemTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return ORSolverFactory.instance();
	}

}
