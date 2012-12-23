/**
 * 
 */
package com.patrikdufresne.ilp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.patrikdufresne.ilp.impl.GLPKSolverFactory;

/**
 * @author ikus060
 * 
 */
public class LinearProblemTest {

	private LinearProblem lp;

	private Solver solver;

	@Before
	public void createEmptyLP() {
		SolverFactory solverFactory = GLPKSolverFactory.instance();
		solver = solverFactory.createSolver();
		lp = solver.createLinearProblem();
	}

	/**
	 * Test method for
	 * {@link com.patrikdufresne.ilp.LinearProblem#addBinaryVariable(java.lang.String)}
	 * .
	 */
	@Test
	public void testAddBinaryVariable() {

		Variable var = lp.addBinaryVariable("x");

		assertEquals(VarType.BOOL, var.getType());
		assertEquals(0, var.getLowerBound().intValue());
		assertEquals(1, var.getUpperBound().intValue());

	}

	/**
	 * Test method for
	 * {@link com.patrikdufresne.ilp.LinearProblem#getConstraints()}.
	 */
	@Test
	public void testGetConstraints() {
		Constraint c1 = lp.addConstraint();
		Constraint c2 = lp.addConstraint();

		Collection<? extends Constraint> collection = lp.getConstraints();

		assertEquals("Wrong number of constraint", 2, collection.size());

		assertTrue("Constraint object not found.", collection.contains(c1));

		assertTrue("Constraint object not found.", collection.contains(c2));

	}

	/**
	 * Test method for
	 * {@link com.patrikdufresne.ilp.LinearProblem#setObjectiveLinear(com.patrikdufresne.ilp.Linear)}
	 * .
	 */
	@Test
	public void testSetObjectiveLinear() {

		// Set dummy objective
		Variable dummy = lp.addBinaryVariable("dummy");
		lp.setObjectiveLinear(lp.createLinear(1, dummy));
		Linear linear;
		assertNotNull(linear = lp.getObjectiveLinear());
		assertEquals(1, linear.size());
		Term term = linear.iterator().next();
		assertEquals(1, term.getCoefficient().intValue());
		assertEquals(dummy, term.getVariable());

		// Set empty objective
		lp.setObjectiveLinear(lp.createLinear());
		assertNull(lp.getObjectiveLinear());

		// Remove objective
		lp.setObjectiveLinear(null);
		assertNull(lp.getObjectiveLinear());

	}

}
