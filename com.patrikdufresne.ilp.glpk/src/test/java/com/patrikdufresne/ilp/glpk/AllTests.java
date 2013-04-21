package com.patrikdufresne.ilp.glpk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ GLPKLinearProblemTest.class, GLPKSolverTest.class,
		GLPKVariableTest.class, GLPKConstraintTest.class })
public class AllTests {

}
