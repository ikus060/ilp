/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;

import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.IPersistentLinearProblem;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;
import com.patrikdufresne.ilp.Variable;

/**
 * This implementation represent a GLPK linear problem.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKLinearProblem extends AbstractLinearProblem implements
		IPersistentLinearProblem {
	/**
	 * Value return by glp_get_row_ub, glp_get_row_lb, glp_get_col_ub and
	 * glp_get_col_lb when it's not bounded.
	 */
	static final double DBL_MAX = Double.MAX_VALUE;

	/**
	 * Determine the column bounding type according to the bounding value.
	 * 
	 * @param lb
	 *            lower bound value
	 * @param ub
	 *            upper bound value
	 * 
	 * @return one of the GLP_DB, GLP_FX, GLP_LO, GLP_UP, GLP_FR
	 */
	static int boundedType(Number lb, Number ub) {
		int type;
		if (ub != null && lb != null) {
			// Double-bounded
			type = GLPKConstants.GLP_DB;
			if (ub.doubleValue() == lb.doubleValue()) {
				// Fixed
				type = GLPKConstants.GLP_FX;
			}
		} else if (ub == null && lb != null) {
			// Lower bounded
			type = GLPKConstants.GLP_LO;
		} else if (ub != null && lb == null) {
			// Upper bounded
			type = GLPKConstants.GLP_UP;
		} else {
			// Free (unbounded)
			type = GLPKConstants.GLP_FR;
		}
		return type;
	}

	/**
	 * Convert the status code into {@link Status} object.
	 * 
	 * @param status
	 *            the status code return by
	 *            {@link GLPK#glp_get_status(glp_prob)}.
	 * @return a Status object
	 */
	static Status status(int status) {
		if (status == GLPKConstants.GLP_UNDEF) {
			// solution is undefined
			return Status.UNKNOWN;
		} else if (status == GLPKConstants.GLP_FEAS) {
			return Status.FEASIBLE;
		} else if (status == GLPKConstants.GLP_INFEAS) {
			return Status.INFEASIBLE;
		} else if (status == GLPKConstants.GLP_NOFEAS) {
			return Status.INFEASIBLE;
		} else if (status == GLPKConstants.GLP_OPT) {
			// solution is optimal
			return Status.OPTIMAL;
		} else if (status == GLPKConstants.GLP_UNBND) {
			return Status.UNBOUNDED;
		}
		return Status.UNKNOWN;
	}

	/**
	 * Need to keep reference on every constraint (row).
	 */
	private List<GLPKConstraint> constraints;

	/**
	 * Cached value.
	 */
	Boolean dualFeasible;

	/**
	 * Private reference on the glpk problem.
	 */
	glp_prob lp;

	/**
	 * Cached value.
	 */
	Boolean primalFeasible;

	/**
	 * Need to keep reference on every variable (col).
	 */
	private List<GLPKVariable> variables;

	/**
	 * Create a new linear problem.
	 */
	public GLPKLinearProblem() {
		this.lp = GLPK.glp_create_prob();
	}

	/**
	 * Should be called for every new instance of GLPKVariable
	 * 
	 * @param var
	 *            the variable to be added to the problem.
	 */
	synchronized void addCol(GLPKVariable var) {
		this.mip = null;
		if (this.variables == null) {
			this.variables = new ArrayList<GLPKVariable>();
		}

		// Make the problem dirty
		this.makeDirty();

		// Create a new column using GLPK API.
		var.parent = this;
		var.col = GLPK.glp_add_cols(this.lp, 1);

		if (var.col != this.variables.size() + 1) {
			throw new RuntimeException("GLPKVariable.col is not set properly."); //$NON-NLS-1$
		}
		this.variables.add(var);
	}

	@Override
	public Constraint addConstraint() {
		checkProblem();
		return new GLPKConstraint(this);
	}

	/**
	 * Should be called for every instance of GLPKConstraint.
	 * 
	 * @param constraint
	 *            the constraint to be added to the problem.
	 */
	synchronized void addRow(GLPKConstraint constraint) {
		if (this.constraints == null) {
			this.constraints = new ArrayList<GLPKConstraint>();
		}

		this.makeDirty();

		constraint.parent = this;
		constraint.row = GLPK.glp_add_rows(this.lp, 1);

		if (constraint.row != this.constraints.size() + 1) {
			throw new RuntimeException(
					"GLPKConstraint.row is not set properly."); //$NON-NLS-1$
		}
		this.constraints.add(constraint);
	}

	@Override
	public Variable addVariable() {
		checkProblem();
		return new GLPKVariable(this);
	}

	/**
	 * Throw an exception is the problem is not dual feasible
	 */
	void checkDual() {
		if (!isDualFeasible()) {
			throw new ILPException("dual solution value not available"); //$NON-NLS-1$
		}
	}

	/**
	 * Throw an exception is the primal solution value is not available.
	 */
	void checkPrimal() {
		if (!isPrimalFeasible()) {
			throw new ILPException("primal solution value not available."); //$NON-NLS-1$
		}
	}

	/**
	 * Check if the problem is disposed.
	 */
	void checkProblem() {
		if (isDisposed()) {
			throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
		}
	}

	/**
	 * Check the solution state. Throw an exception if the solution is not
	 * available.
	 */
	void checkSolution() {
		Status status = getStatus();
		if (!status.equals(Status.FEASIBLE) && !status.equals(Status.OPTIMAL)) {
			throw new ILPException("solution not available"); //$NON-NLS-1$
		}
	}

	/**
	 * Clear the problem.
	 */
	void clear() {
		this.dirty = false;
	}

	/**
	 * This implementation return a {@link ConcreteLinear} object since GLPK
	 * doesn't support dynamic array.
	 */
	@Override
	public Linear createLinear() {
		checkProblem();

		return new ConcreteLinear();
	}

	@Override
	public Term createTerm(Number coefficient, Variable variable) {
		checkProblem();
		return new ConcreteTerm(coefficient, variable);
	}

	@Override
	public void dispose() {

		// Free the lp problem.
		if (this.lp != null) {
			GLPK.glp_delete_prob(this.lp);
		}
		this.lp = null;

	}

	/**
	 * Return the variable for the given column index.
	 * 
	 * @param col
	 *            the column index.
	 * @return the variable object
	 */
	Variable getCol(int col) {
		GLPKVariable var = this.variables.get(col - 1);
		if (var.col != col) {
			throw new ILPException("Variables list corrupted"); //$NON-NLS-1$
		}
		return var;
	}

	/**
	 * Return an unmodifiable collection of constraints.
	 */
	@Override
	public Collection<? extends Constraint> getConstraints() {
		if (this.constraints == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(this.constraints);
	}

	/**
	 * Return the problem name.
	 */
	@Override
	public String getName() {
		checkProblem();
		return GLPK.glp_get_prob_name(this.lp);
	}

	/**
	 * Return the problem's direction.
	 * 
	 * @return
	 */
	@Override
	public int getObjectiveDirection() {

		checkProblem();

		int direction = GLPK.glp_get_obj_dir(this.lp);
		if (direction == GLPKConstants.GLP_MAX) {
			return MAXIMIZE;
		}
		return MINIMIZE;

	}

	/**
	 * This implementation rebuild the linear object.
	 * 
	 * @return
	 */

	@Override
	public Linear getObjectiveLinear() {
		checkProblem();

		// Get the number of columns
		int count = GLPK.glp_get_num_cols(this.lp);

		if (count == 0) {
			return createLinear();
		}

		// Get the coefficient of each column.
		Linear linear = createLinear();
		for (int col = 1; col < count + 1; col++) {
			double coef = GLPK.glp_get_obj_coef(this.lp, col);
			if (coef != 0) {
				linear.add(createTerm(coef, getCol(col)));
			}
		}

		if (linear.size() == 0) {
			return null;
		}

		return linear;
	}

	@Override
	public String getObjectiveName() {
		checkProblem();

		return GLPK.glp_get_obj_name(this.lp);
	}

	/**
	 * This implementation return the objective value.
	 */
	@Override
	public Number getObjectiveValue() {
		checkProblem();

		checkSolution();

		if (isMIP()) {
			return Double.valueOf(GLPK.glp_mip_obj_val(this.lp));
		}

		return Double.valueOf(GLPK.glp_get_obj_val(this.lp));
	}

	/**
	 * Cached value. True if the problem is a MIP.
	 */
	Boolean mip;

	/**
	 * Cached version of this function to avoid calling JNI over and over.
	 */
	@Override
	public boolean isMIP() {

		if (this.mip == null) {
			this.mip = Boolean.valueOf(super.isMIP());
		}
		return this.mip.booleanValue();

	}

	/**
	 * Cached status.
	 */
	Status status;

	/**
	 * Get the problem status
	 */
	@Override
	public Status getStatus() {
		checkProblem();

		if (this.status == null) {

			if (isMIP()) {
				// Get the MIP status
				this.status = status(GLPK.glp_mip_status(this.lp));

			} else {

				this.status = status(GLPK.glp_get_status(this.lp));
			}

		}

		return this.status;
	}

	@Override
	public Collection<? extends Variable> getVariables() {
		if (this.variables == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(this.variables);
	}

	/**
	 * Return true if <code>lp</code> is null.
	 */
	@Override
	public boolean isDisposed() {
		return this.lp == null;
	}

	@Override
	public boolean isDualFeasible() {
		checkProblem();
		if (this.dualFeasible == null) {
			this.dualFeasible = Boolean
					.valueOf(GLPK.glp_get_dual_stat(this.lp) == GLPKConstants.GLP_FEAS);
		}
		return this.dualFeasible.booleanValue();
	}

	@Override
	public boolean isPrimalFeasible() {
		checkProblem();
		// The variable primalFeasible is set to null when the problem is
		// solved.
		if (this.primalFeasible == null) {
			this.primalFeasible = Boolean.valueOf(GLPK
					.glp_get_prim_stat(this.lp) == GLPKConstants.GLP_FEAS);
		}
		return this.primalFeasible.booleanValue();
	}

	/**
	 * Sets the problem's name
	 */
	@Override
	public void setName(String name) {
		checkProblem();

		makeDirty();

		// Check string size
		if (name != null && name.length() > 255) {
			throw new IndexOutOfBoundsException("name > 255"); //$NON-NLS-1$
		}
		GLPK.glp_set_prob_name(this.lp, name);
	}

	/**
	 * Sets the objective direction.
	 */
	@Override
	public void setObjectiveDirection(int direction) {
		checkProblem();

		makeDirty();

		if (direction == MAXIMIZE) {
			GLPK.glp_set_obj_dir(this.lp, GLPKConstants.GLP_MAX);
		} else {
			GLPK.glp_set_obj_dir(this.lp, GLPKConstants.GLP_MIN);
		}

	}

	/**
	 * This implementation sets the objective name and coefficient.
	 */
	@Override
	public void setObjectiveLinear(Linear objective) {
		checkProblem();

		makeDirty();

		// Sets all the coefs to zero
		if (this.variables != null) {
			for (GLPKVariable var : this.variables) {
				GLPK.glp_set_obj_coef(this.lp, var.col, 0);
			}
		}

		if (objective == null || objective.size() == 0) {
			return;
		}

		// Then sets the real coef value
		for (Term term : objective) {
			GLPK.glp_set_obj_coef(this.lp,
					((GLPKVariable) term.getVariable()).col, term
							.getCoefficient().doubleValue());
		}
	}

	/**
	 * Sets the objective name
	 */
	@Override
	public void setObjectiveName(String name) {
		checkProblem();

		makeDirty();

		// Check string size
		if (name != null && name.length() > 255) {
			throw new IndexOutOfBoundsException("name > 255"); //$NON-NLS-1$
		}
		GLPK.glp_set_obj_name(this.lp, name);
	}

	@Override
	public void load(File file) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * This implementation is writing the problem in GLPK LP/MIP format to text
	 * file.
	 */
	@Override
	public void save(File file) throws IOException {
		checkProblem();

		if (file == null) {
			throw new NullPointerException();
		}

		if (GLPK.glp_write_lp(this.lp, null, file.getAbsolutePath()) != 0) {
			// TODO retrieved the error message
			throw new IOException("Error writing the problem to file."); //$NON-NLS-1$
		}

	}

	/**
	 * Remove the column fr om the linear problem.
	 * 
	 * @param col
	 */
	synchronized void removeCol(GLPKVariable var) {
		int index;
		if (this.variables == null || (index = this.variables.indexOf(var)) < 0) {
			throw new RuntimeException("GLPKVariable not in the variable list."); //$NON-NLS-1$
		}

		this.makeDirty();

		SWIGTYPE_p_int cols = GLPK.new_intArray(2);
		GLPK.intArray_setitem(cols, 1, var.col);
		GLPK.glp_del_cols(this.lp, 1, cols);

		this.variables.remove(index);

		var.col = 0;
		var.parent = null;

		for (; index < this.variables.size(); index++) {
			GLPKVariable sub = this.variables.get(index);
			sub.col = index + 1;
		}

	}

	synchronized void removeRow(GLPKConstraint constraint) {
		int index;
		if (this.constraints == null
				|| (index = this.constraints.indexOf(constraint)) < 0) {
			throw new RuntimeException(
					"GLPKConstraint not in the constraint list."); //$NON-NLS-1$
		}

		this.makeDirty();

		SWIGTYPE_p_int rows = GLPK.new_intArray(2);
		GLPK.intArray_setitem(rows, 1, constraint.row);
		GLPK.glp_del_rows(this.lp, 1, rows);

		this.constraints.remove(index);

		constraint.row = 0;
		constraint.parent = null;

		for (; index < this.constraints.size(); index++) {
			GLPKConstraint sub = this.constraints.get(index);
			sub.row = index + 1;
		}

	}

}