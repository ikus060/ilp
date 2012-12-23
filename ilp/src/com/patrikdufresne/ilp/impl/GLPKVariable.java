/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.impl;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;

import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

/**
 * This class is a complete implementation of the {@link Variable} interface for
 * GLPK solver.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKVariable implements Variable {
	/**
	 * Used to convert the value into the correction number object.
	 * 
	 * @param type
	 *            the variable type.
	 * @param value
	 *            the double value to be converted
	 * @return the Number object matching the type.
	 */
	static Number value(VarType type, double value) {
		if (type.equals(VarType.BOOL) || type.equals(VarType.INTEGER)) {
			return Integer.valueOf((int) value);
		}

		return Double.valueOf(value);
	}

	/**
	 * The col index.
	 */
	int col;

	GLPKLinearProblem parent;

	/**
	 * Create a new variable.
	 * 
	 * @param parent
	 *            the parent problem.
	 */
	GLPKVariable(GLPKLinearProblem parent) {
		if (parent == null) {
			throw new NullPointerException();
		}
		parent.checkProblem();

		// Call the parent function to complete the opperation.
		parent.addCol(this);

	}

	/**
	 * Check if the variable is disposed.
	 */
	void checkVariable() {
		if (isDisposed()) {
			throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
		}
		this.parent.checkProblem();
	}

	/**
	 * This implementation remove the column fromthe linear problem.
	 */
	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		// Call the parent function to complete the operation.
		this.parent.removeCol(this);

	}

	/**
	 * Retrieve the column dual value.
	 * <p>
	 * Report components of the solution to LP relaxation. Ref.:
	 * http://lists.gnu.org/archive/html/help-glpk/2011-09/msg00034.html
	 * </p>
	 */
	@Override
	public Number getDual() {
		checkVariable();

		this.parent.checkSolution();

		this.parent.checkDual();

		return Double.valueOf(GLPK.glp_get_col_dual(this.parent.lp, this.col));
	}

	@Override
	public Number getLowerBound() {
		checkVariable();

		double value = GLPK.glp_get_col_lb(this.parent.lp, this.col);
		if (value == -GLPKLinearProblem.DBL_MAX) {
			return null;
		}

		return value(getType(), value);
	}

	@Override
	public String getName() {
		this.parent.checkProblem();
		return GLPK.glp_get_col_name(this.parent.lp, this.col);
	}

	@Override
	public VarType getType() {
		checkVariable();

		int kind = GLPK.glp_get_col_kind(this.parent.lp, this.col);

		if (kind == GLPKConstants.GLP_BV) {
			return VarType.BOOL;
		} else if (kind == GLPKConstants.GLP_IV) {
			return VarType.INTEGER;
		} else if (kind == GLPKConstants.GLP_CV) {
			return VarType.REAL;
		}

		throw new ILPException("glp_get_col_kind() returns invalid value"); //$NON-NLS-1$
	}

	@Override
	public Number getUpperBound() {
		checkVariable();

		double value = GLPK.glp_get_col_ub(this.parent.lp, this.col);
		if (value == GLPKLinearProblem.DBL_MAX) {
			return null;
		}

		return value(getType(), value);
	}

	/**
	 * Retrieve the column primal value.
	 * <p>
	 * Report components of the solution to LP relaxation. Ref.:
	 * http://lists.gnu.org/archive/html/help-glpk/2011-09/msg00034.html
	 * </p>
	 */
	@Override
	public Number getValue() {
		checkVariable();

		this.parent.checkSolution();

		if (this.parent.isMIP()) {
			return value(getType(),
					GLPK.glp_mip_col_val(this.parent.lp, this.col));
		}

		this.parent.checkPrimal();

		return Double.valueOf(GLPK.glp_get_col_prim(this.parent.lp, this.col));
	}

	/**
	 * This implementation check if the {@link #col} property is set to zero (0)
	 * and if the parent is nul.
	 */
	@Override
	public boolean isDisposed() {
		return this.parent == null || this.col == 0;
	}

	/**
	 * Sets the columns bounding type and bounds.
	 */
	@Override
	public void setLowerBound(Number lb) {
		checkVariable();

		this.parent.makeDirty();

		Number ub = getUpperBound();
		int type = GLPKLinearProblem.boundedType(lb, ub);

		GLPK.glp_set_col_bnds(this.parent.lp, this.col, type,
				lb != null ? lb.doubleValue() : 0,
				ub != null ? ub.doubleValue() : 0);

	}

	@Override
	public void setName(String name) {
		checkVariable();

		this.parent.makeDirty();

		if (name != null && name.length() > 255) {
			throw new IllegalArgumentException("name > 255"); //$NON-NLS-1$
		}

		GLPK.glp_set_col_name(this.parent.lp, this.col, name);
	}

	@Override
	public void setType(VarType type) {
		checkVariable();

		this.parent.makeDirty();

		if (type.equals(VarType.BOOL)) {

			GLPK.glp_set_col_kind(this.parent.lp, this.col,
					GLPKConstants.GLP_IV);
			GLPK.glp_set_col_bnds(this.parent.lp, this.col,
					GLPKConstants.GLP_DB, 0, 1);

		} else if (type.equals(VarType.INTEGER)) {

			GLPK.glp_set_col_kind(this.parent.lp, this.col,
					GLPKConstants.GLP_IV);

		} else if (type.equals(VarType.REAL)) {

			GLPK.glp_set_col_kind(this.parent.lp, this.col,
					GLPKConstants.GLP_CV);

			this.parent.mip = null;

		}
	}

	/**
	 * Sets the columns bounding type and bounds
	 */
	@Override
	public void setUpperBound(Number ub) {
		checkVariable();

		this.parent.makeDirty();

		Number lb = getLowerBound();
		int type = GLPKLinearProblem.boundedType(lb, ub);

		GLPK.glp_set_col_bnds(this.parent.lp, this.col, type,
				lb != null ? lb.doubleValue() : 0,
				ub != null ? ub.doubleValue() : 0);
	}

	@Override
	public String toString() {
		if (isDisposed()) {
			return "GLPKVariable [disposed]"; //$NON-NLS-1$
		}
		return getName();
	}

}
