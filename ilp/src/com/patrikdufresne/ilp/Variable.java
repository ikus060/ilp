/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * Represent a variable in the linear problem.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Variable {

	/**
	 * Return the dual solution value of this variable. This value is only
	 * available after solving the linear problem with simplex algorithm.
	 * 
	 * @return the dual solution value.
	 */
	Number getDual();

	/**
	 * Returns the lower bound.
	 * 
	 * @return the lower bound or null if not bounded
	 */
	Number getLowerBound();

	/**
	 * Returns the variable name.
	 * 
	 * @return the name.
	 */
	String getName();

	/**
	 * Returns the solution value for a variable.
	 * <p>
	 * If the problem was solved using the simplex algorithm value or the
	 * variable. The dual value may be retrieved using {@link #getDual()}.
	 * </p>
	 * 
	 * @return the solution value or null if the solution is not available
	 * @throws ILPException
	 *             is no solution are available
	 */
	Number getValue();

	/**
	 * Returns the variable type.
	 * 
	 * @return the variable type.
	 */
	VarType getType();

	/**
	 * Returns the upper bound.
	 * 
	 * @return the upper bound or null if unbounded
	 */
	Number getUpperBound();

	/**
	 * Sets the variable lower bound.
	 * 
	 * @param bound
	 *            the new lower bound value or null for −∞
	 */
	void setLowerBound(Number bound);

	/**
	 * Sets the variable name.
	 * 
	 * @param name
	 *            the new name
	 */
	void setName(String name);

	/**
	 * Sets the variable type.
	 * 
	 * @param type
	 *            the new type
	 */
	void setType(VarType type);

	/**
	 * Sets the variable upper bound.
	 * 
	 * @param bound
	 *            the new upper bound or null for +∞
	 */
	void setUpperBound(Number bound);

	/**
	 * Check if the variable object is disposed.
	 * 
	 * @return True if the variable is disposed.
	 */
	public boolean isDisposed();

	/**
	 * Remove the variable from the linear problem. Does nothing if the variable
	 * is already disposed.
	 * <p>
	 * Sub classes must consider the case when this function is called multiple
	 * time for the same object.
	 */
	void dispose();

}
