/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;


/**
 * This class provide a complter implementation of the {@link Term} interface.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ConcreteTerm implements Term {
	/**
	 * The coefficient value.
	 */
	private Number coefficient;

	/**
	 * The variable.
	 */
	private Variable variable;

	/**
	 * Create a new term.
	 * 
	 * @param coefficient
	 * @param variable
	 */
	public ConcreteTerm(Number coefficient, Variable variable) {
		if (coefficient == null || variable == null) {
			throw new NullPointerException();
		}
		this.coefficient = Double.valueOf(coefficient.doubleValue());
		this.variable = variable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConcreteTerm other = (ConcreteTerm) obj;
		if (!this.coefficient.equals(other.coefficient))
			return false;
		if (!this.variable.equals(other.variable))
			return false;
		return true;
	}

	@Override
	public Number getCoefficient() {
		return this.coefficient;
	}

	@Override
	public Variable getVariable() {
		return this.variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.coefficient.hashCode();
		result = prime * result + this.variable.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.coefficient + " * " + this.variable; //$NON-NLS-1$
	}

}
