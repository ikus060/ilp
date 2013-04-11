/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.util;

import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Term;

/**
 * Utility class to manipulate linears.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Linears {

	/**
	 * Private constructor to avoid creating instances of utility class.
	 */
	private Linears() {

	}

	/**
	 * Compute the value of a linear using the variable value from a snapshot.
	 * 
	 * @param linear
	 *            the linear to be computed
	 * @param snapshot
	 *            the value snapshot
	 * @return the computed value.
	 * @throws NullPointerException
	 *             if the snapshot doesn't contains a variable from the linear.
	 */
	public static Number compute(Linear linear, ValueSnapshot snapshot) {
		double value = 0;
		for (Term term : linear) {
			value += term.getCoefficient().doubleValue()
					* snapshot.get(term.getVariable()).doubleValue();
		}
		return Double.valueOf(value);
	}

}
