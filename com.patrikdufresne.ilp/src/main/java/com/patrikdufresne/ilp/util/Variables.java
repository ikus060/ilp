/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.util;

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

}
