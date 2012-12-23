package com.patrikdufresne.ilp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LinearProblemTest.class, SolverTest.class,
		VariableTest.class })
public class AllTests {

}
