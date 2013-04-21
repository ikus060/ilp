package com.patrikdufresne.ilp.ortools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ORLinearProblemTest.class, ORSolverTest.class,
		ORVariableTest.class, ORConstraintTest.class })
public class AllTests {

}
