/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.util;

import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Term;
import com.patrikdufresne.ilp.Variable;

/**
 * Utility class for {@link Variable}.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Variables {

	/**
	 * Check if the variable bound is fixed.
	 * 
	 * @param var
	 *            the variable
	 * @return True if lower bound and the upper bound is fixed.
	 */
	public static boolean isFixed(Variable var) {
		return var.getLowerBound() != null
				&& var.getLowerBound().equals(var.getUpperBound());
	}

	/**
	 * Private constructor for utility class.
	 */
	private Variables() {

	}

	/**
	 * Compute the value of a linear.
	 * 
	 * @param linear
	 *            the linear
	 * @param snapshot
	 *            the snapshot
	 * @return the value
	 * @throws NullPointerException
	 *             if the snapshot doesn't contains a varialbe from the linear.
	 */
	public static Number computeWithSnapshot(Linear linear,
			ValueSnapshot snapshot) {
		double value = 0;
		for (Term term : linear) {
			value += term.getCoefficient().doubleValue()
					* snapshot.get(term.getVariable()).doubleValue();
		}
		return Double.valueOf(value);
	}

}
