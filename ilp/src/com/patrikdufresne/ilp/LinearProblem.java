/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

import java.util.Collection;
import java.util.List;

/**
 * The class {@link LinearProblem} represents a linear problem consisting of
 * multiple constraints and up to one objective function.
 * <p>
 * If the model contains integer or boolean variables, the model is referred to
 * as a mixed integer program (MIP). You can query whether the active model is a
 * MIP with the method {@link #isMIP()}.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface LinearProblem {
	/**
	 * Constant used to sets the objective direction.
	 */
	public static final int MAXIMIZE = 0;
	/**
	 * Constant used to sets the objective direction.
	 */
	public static final int MINIMIZE = 1;

	/**
	 * Create a new binary variable.
	 * 
	 * @param name
	 *            the variable name
	 * @return the variable
	 */
	Variable addBinaryVariable(String name);

	/**
	 * Create a new constraint for this problem.
	 * 
	 * @return the new constraint
	 */
	Constraint addConstraint();

	/**
	 * Create a new constraint for this problem with the given name,
	 * coefficients, variables and bounds.
	 * 
	 * @param name
	 *            the constraint name
	 * @param coefficients
	 *            the coefficients of the linear expression
	 * @param variables
	 *            the variables of the linear expression
	 * @param lowerBound
	 *            the lower bound
	 * @param upperBound
	 *            the upper bound
	 * @return a new constraint object
	 */
	Constraint addConstraint(String name, int[] coefficients,
			Variable[] variables, int lowerBound, int upperBound);

	/**
	 * Create a new constraint for this problem with the given name,
	 * coefficients, variables and bounds.
	 * 
	 * @param name
	 *            the constraint name
	 * @param coefficients
	 *            the coefficients of the linear expression
	 * @param variables
	 *            the variables of the linear expression
	 * @param lowerBound
	 *            the lower bound or null for unbounded
	 * @param upperBound
	 *            the upper bound or null for unbounded
	 * @return a new constraint object
	 */
	Constraint addConstraint(String name, int[] coefficients,
			Variable[] variables, Number lowerBound, Number upperBound);

	/**
	 * Create a new constraint for this problem with the given name, linear
	 * expression and bounds.
	 * 
	 * @param name
	 *            the constraint name or null
	 * @param linear
	 *            the constraint linear expression
	 * @param lowerBound
	 *            the lower bound
	 * @param upperBound
	 *            the upper bound
	 * @return the constraint object
	 */
	Constraint addConstraint(String name, Linear linear, int lowerBound,
			int upperBound);

	/**
	 * Create a new constraint for this problem.
	 * 
	 * @param name
	 *            the constraint name or null
	 * @param linear
	 *            the constraint linear expression
	 * @param lowerBound
	 *            the lower bound or null for unbounded
	 * @param upperBound
	 *            the upper bound or null for unbounded
	 * @return the constraint object
	 */
	Constraint addConstraint(String name, Linear linear, Number lowerBound,
			Number upperBound);

	/**
	 * Create a new variable for this problem.
	 * 
	 * @return
	 */
	Variable addVariable();

	/**
	 * Create a new bounded integer variable.
	 * 
	 * @param name
	 *            the variable name
	 * @param lowerBound
	 *            the lower bound or null for unbounded
	 * @param upperBound
	 *            the upper bound or null for unbounded
	 * @return
	 */
	Variable addIntegerVariable(String name, Integer lowerBound,
			Integer upperBound);

	/**
	 * Create a Linear object.
	 * 
	 * @return the Linear object
	 */
	Linear createLinear();

	/**
	 * Create a {@link Linear} object for this problem.
	 * <p>
	 * If the coefficient is zero or the variable is null, the term is not added
	 * to the linear expression.
	 * 
	 * @param coefficient
	 *            the coefficient value
	 * @param variable
	 *            the variable
	 * @return the new {@link Linear}
	 */
	Linear createLinear(int coefficient, Variable variable);

	Linear createLinear(float coefficient, Variable variable);

	Linear createLinear(double coefficient, Variable variable);

	Linear createLinear(Number coefficient, Variable variable);

	/**
	 * Create a {@link Linear} object for this problem. If the coefficient of
	 * the variable is zero (0), the term is not added to the the Linear
	 * expression.
	 * <p>
	 * If the coefficient is zero or the variable is null, the term is not added
	 * to the linear expression.
	 * 
	 * 
	 * @param coefficients
	 *            the coefficients value.
	 * @param variables
	 *            the variables
	 * @return the {@link Linear} object.
	 */
	Linear createLinear(int[] coefficients, Variable[] variables);

	/**
	 * Create a {@link Linear} object for this problem. If the coefficient of is
	 * zero (0), the term is not added to the the Linear expression.
	 * 
	 * @param coefficients
	 *            the coefficients value.
	 * @param variables
	 *            the variables
	 * @return the {@link Linear} object.
	 */
	Linear createLinear(float[] coefficients, Variable[] variables);

	/**
	 * Create a {@link Linear} object for this problem. If the coefficient of is
	 * zero (0), the term is not added to the the Linear expression.
	 * 
	 * 
	 * @param coefficients
	 *            the coefficients value.
	 * @param variables
	 *            the variables
	 * @return the {@link Linear} object.
	 */
	Linear createLinear(double[] coefficients, Variable[] variables);

	/**
	 * Create a {@link Linear} object for this problem. If the coefficient of is
	 * zero (0), the term is not added to the the Linear expression.
	 * 
	 * 
	 * @param coefficients
	 *            the coefficients value.
	 * @param variables
	 *            the variables
	 * @return the {@link Linear} object.
	 */
	Linear createLinear(Number[] coefficients, Variable[] variables);

	/**
	 * Create a {@link Linear} object for this problem. If the coefficient of is
	 * zero (0) or null, the term is not added to the the Linear expression.
	 * 
	 * 
	 * @param coefficients
	 *            the coefficients value, may contain null
	 * @param variables
	 *            the variables
	 * @return the {@link Linear} object.
	 */
	Linear createLinear(List<? extends Number> coefficients,
			List<Variable> variables);

	/**
	 * Create a new Term for this problem.
	 * <p>
	 * If the coefficient is zero or the variable is null, an exception is
	 * thrown.
	 * 
	 * 
	 * @param coefficient
	 *            the coefficient value
	 * @param variable
	 *            the variable, can't be null
	 * @return the new Term object
	 */
	Term createTerm(int coefficient, Variable variable);

	/**
	 * Create a new Term for this problem.
	 * <p>
	 * If the coefficient is null or the variable is null, an exception is
	 * thrown.
	 * 
	 * @param coefficient
	 *            the coefficient value
	 * @param variable
	 *            the variable, can't be null
	 * @return the new Term object
	 */
	Term createTerm(float coefficient, Variable variable);

	/**
	 * Create a new Term for this problem.
	 * <p>
	 * If the coefficient is null or the variable is null, an exception is
	 * thrown.
	 * 
	 * @param coefficient
	 *            the coefficient value
	 * @param variable
	 *            the variable, can't be null
	 * @return the new Term object
	 */
	Term createTerm(double coefficient, Variable variable);

	/**
	 * Create a new Term
	 * 
	 * @param coefficient
	 *            the coefficient value, can't be null
	 * @param variable
	 *            the variable variable, can't be null
	 * @return the new Term object
	 */
	Term createTerm(Number coefficient, Variable variable);

	/**
	 * Dispose any resource allocated by this problem.
	 */
	void dispose();

	/**
	 * Returns the contraints.
	 * 
	 * @return the contraints
	 */
	Collection<? extends Constraint> getConstraints();

	/**
	 * Returns the problem name
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the objective direction.
	 * 
	 * @return
	 */
	int getObjectiveDirection();

	/**
	 * Returns the objective.
	 * 
	 * @return the objective linear expression or null if not set.
	 */
	Linear getObjectiveLinear();

	/**
	 * Return the objective name.
	 * 
	 * @return
	 */
	String getObjectiveName();

	/**
	 * Returns the objective value. Only valid after solving the problem.
	 * 
	 * @return the objective value.
	 */
	Number getObjectiveValue();

	/**
	 * Returns the solution status of the linear problem. This value is only
	 * relevant after solving the linear problem.
	 * 
	 * @return return the status.
	 * @see Status
	 */
	Status getStatus();

	/**
	 * Returns the variables.
	 * 
	 * @return the variables
	 */
	Collection<? extends Variable> getVariables();

	/**
	 * Check if the linear problem is dirty. A problem become dirty every time a
	 * constraint or a variable is added, removed or modified. A problem becore
	 * clear, after being solved.
	 * 
	 * @return True if the problem is dirty.
	 */
	boolean isDirty();

	/**
	 * Check if the problem is disposed.
	 * 
	 * @return true if the problem is disposed.
	 */
	boolean isDisposed();

	/**
	 * Check if the active model is a MIP.
	 * 
	 * @return true if the active model is a MIP
	 */
	boolean isMIP();

	/**
	 * Returns <code>true</code> if a primal feasible solution is available and
	 * can be query using {@link Variable#getValue()}.
	 * <p>
	 * If false is returned, the solution may still be primal feasible, but the
	 * algorithm did not determine the feasibility before it terminated.
	 * 
	 * @return <code>true</code> if primal feasible
	 */
	boolean isPrimalFeasible();

	/**
	 * Returns <code>true</code> if a dual feasible solution is available. If
	 * false is returned, the solution may still be dual feasible, but the
	 * algorithm did not determine the feasibility before it terminated.
	 * 
	 * @return <code>true</code> if dual feasible.
	 */
	boolean isDualFeasible();

	/**
	 * Sets the probme's name.
	 * 
	 * @param name
	 *            the new name
	 */
	void setName(String name);

	/**
	 * Sets the objective direction.
	 * 
	 * @param direction
	 *            the new direction value. Should be one of the MAXIMIZE or
	 *            MINIMIZE constant.
	 */
	void setObjectiveDirection(int direction);

	/**
	 * Sets the objective linear expression
	 * 
	 * @param objective
	 *            the new objective linear expression, may be empty or null to
	 *            remove the objective function.
	 */
	void setObjectiveLinear(Linear objective);

	/**
	 * Sets the objective name.
	 * 
	 * @param name
	 *            the new name or null to unset
	 */
	void setObjectiveName(String name);

}
