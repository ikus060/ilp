/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * The <code>Status</code> class is an enumeration of the possible solution
 * status values.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Status {
	
	/**
	 * The solver has found an optimal solution that can be queried with the
	 * method {@link Variable#getValue()}.
	 */
	public static final Status OPTIMAL = new Status("OPTIMAL"); //$NON-NLS-1$

	/**
	 * The solver has found a feasible solution that can be queried with the
	 * method {@link Variable#getValue()}. However, its optimality
(or non-optimality) has not been proven, perhaps due
to premature termination of the search.

	 */
	public static final Status FEASIBLE = new Status("FEASIBLE"); //$NON-NLS-1$

	/**
	 * The solver has determined that the problem is infeasible.
	 */
	public static final Status INFEASIBLE = new Status("INFEASIBLE"); //$NON-NLS-1$

	/**
	 * The solver has determined that the problem is unbounded.
	 */
	public static final Status UNBOUNDED = new Status("UNBOUNDED"); //$NON-NLS-1$

	/**
	 * The solver has determine the solution is undefined.
	 */
	public static final Status UNKNOWN = new Status("UNKNOWN"); //$NON-NLS-1$

	@Override
	public int hashCode() {
		return this.val.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status other = (Status) obj;
		if (!this.val.equals(other.val))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Status [" + val + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String val;

	/**
	 * Private construtor.
	 * 
	 * @param val
	 */
	private Status(String val) {
		this.val = val;
	}

}
