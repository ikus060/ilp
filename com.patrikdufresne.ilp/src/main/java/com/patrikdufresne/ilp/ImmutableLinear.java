package com.patrikdufresne.ilp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable linear.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ImmutableLinear implements Linear {

	/**
	 * Immutable list of terms.
	 */
	private List<Term> terms;

	/**
	 * Create a new immutable linear
	 * 
	 * @param terms
	 *            a collection of terms.
	 */
	public ImmutableLinear(Collection<Term> terms) {
		this.terms = Collections.unmodifiableList(new ArrayList<Term>(terms));
	}

	/**
	 * Create a new immutable linear from an existing linear.
	 * 
	 * @param linear
	 */
	public ImmutableLinear(Linear linear) {
		this.terms = new ArrayList<Term>();
		for (Term term : linear) {
			this.terms.add(term);
		}
		this.terms = Collections.unmodifiableList(this.terms);
	}

	/**
	 * Return an un-modifiable iterator.
	 */
	@Override
	public Iterator<Term> iterator() {
		return this.terms.iterator();
	}

	/**
	 * Throw an exception.
	 */
	@Override
	public void add(Term term) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(Term term) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.terms.size();
	}

}
