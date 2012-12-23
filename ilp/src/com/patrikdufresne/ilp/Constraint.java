/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * The class Constraint represent a linear constraint.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Constraint {
	/**
	 * Return the variable dual value. This value is only available after
	 * solving the linear problem.
	 * 
	 * @return the dual value.
	 * 
	 * @throws ILPException
	 *             if the dual solution is not available.
	 */
	Number getDual();

	/**
	 * Returns the linear expression.
	 * 
	 * @return the constraint linear expression or null if empty.
	 */
	Linear getLinear();

	/**
	 * Returns the lower bound.
	 * 
	 * @return the lower bound or null if not bounded
	 */
	Number getLowerBound();

	/**
	 * Returns the constraint name.
	 * 
	 * @return the name or null if not set
	 */
	String getName();

	/**
	 * Returns the solution value for this constraint. This value is only
	 * available after solving the linear problem.
	 * <p>
	 * If the problem was solved using simplex algorithm, this function return
	 * the primal solution value.
	 * 
	 * @return the solution value
	 * 
	 * @throws ILPException
	 *             if the solution is not available.
	 */
	Number getValue();

	/**
	 * Returns the upper bound.
	 * 
	 * @return the upper bound or null if unbounded
	 */
	Number getUpperBound();

	/**
	 * Sets the constraint linear expression.
	 * 
	 * @param linear
	 *            the new linear expression or null to reset it
	 */
	void setLinear(Linear linear);

	/**
	 * Sets the constraint lower bound.
	 * 
	 * @param bound
	 *            the new lower bound value or null for −∞
	 */
	void setLowerbound(Number bound);

	/**
	 * Sets the constraint name.
	 * 
	 * @param name
	 *            the new name
	 */
	void setName(String name);

	/**
	 * Sets the constraint upper bound.
	 * 
	 * @param bound
	 *            the new upper bound or null for +∞
	 */
	void setUpperBound(Number bound);

	/**
	 * Check if the constraint is disposed.
	 * 
	 * @return True if the problem is disposed.
	 */
	boolean isDisposed();

	/**
	 * Check if the linear expression is empty.
	 * 
	 * @return True if the linear expression is empty.
	 */
	boolean isEmpty();

	/**
	 * Remove the constraint from the linear problem.
	 */
	void dispose();

}
