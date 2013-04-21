/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * The variable type.
 * 
 * @author Patrik Dufresne
 * 
 */
public class VarType {

	/**
	 * Used for binary variable.
	 */
	public static final VarType BOOL = new VarType("BOOL"); //$NON-NLS-1$
	/**
	 * Used for integer variable.
	 */
	public static final VarType INTEGER = new VarType("INT"); //$NON-NLS-1$
	/**
	 * Used for continuous variable.
	 */
	public static final VarType REAL = new VarType("REAL"); //$NON-NLS-1$

	/**
	 * The internal variable type.
	 */
	private String type;

	/**
	 * Private constructor.
	 */
	private VarType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "VarType [" + this.type + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VarType other = (VarType) obj;
		if (!this.type.equals(other.type))
			return false;
		return true;
	}

}
