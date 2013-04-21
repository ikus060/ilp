package com.patrikdufresne.ilp.glpk;
import com.patrikdufresne.ilp.SolverFactory;
import com.patrikdufresne.ilp.VariableTest;

public class GLPKVariableTest extends VariableTest {

	@Override
	protected SolverFactory doGetSolverFactory() {
		return GLPKSolverFactory.instance();
	}

}
