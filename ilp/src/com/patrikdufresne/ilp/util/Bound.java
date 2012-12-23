/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.util;

/**
 * A structure used to store the variable bound.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Bound {
	private Number lower;

	private Number upper;

	/**
	 * Create a new bound instance.
	 * 
	 * @param lower
	 *            the lower bound
	 * @param upper
	 *            the upper bound
	 */
	public Bound(Number lower, Number upper) {
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bound other = (Bound) obj;
		if (this.lower == null) {
			if (other.lower != null)
				return false;
		} else if (!this.lower.equals(other.lower))
			return false;
		if (this.upper == null) {
			if (other.upper != null)
				return false;
		} else if (!this.upper.equals(other.upper))
			return false;
		return true;
	}

	/**
	 * Return the lower bound
	 * 
	 * @return the lower bound value.
	 */
	public Number getLower() {
		return this.lower;
	}

	/**
	 * Return the upper bound
	 * 
	 * @return the upper bound value
	 */
	public Number getUpper() {
		return this.upper;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.lower == null) ? 0 : this.lower.hashCode());
		result = prime * result
				+ ((this.upper == null) ? 0 : this.upper.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Bound:[" + this.lower + ".." + this.upper + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}