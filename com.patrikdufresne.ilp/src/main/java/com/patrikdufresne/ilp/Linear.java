/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * The interface is a linear expression consisting of variables and their
 * coefficients.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Linear extends Iterable<Term> {

	/**
	 * Add a new term to the linear expression.
	 * 
	 * @param term
	 *            the term to be added
	 */
	void add(Term term);

	/**
	 * Remove a term from the linear expression.
	 * 
	 * @param term
	 *            the term to remove
	 */
	void remove(Term term);

	/**
	 * Remove all terms from this linear expression.
	 */
	void clear();

	/**
	 * Returns the number of term in the linear expression.
	 * 
	 * @return the number of term
	 */
	int size();

}
