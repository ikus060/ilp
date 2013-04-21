/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * Immutable object representing the variable and it's coefficient.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Term {

	/**
	 * Return the variable object.
	 * 
	 * @return the variable.
	 */
	public Variable getVariable();

	/**
	 * Return the coefficient value.
	 * 
	 * @return the value
	 */
	public Number getCoefficient();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);

}
