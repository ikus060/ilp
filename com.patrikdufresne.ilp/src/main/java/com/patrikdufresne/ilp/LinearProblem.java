/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * Create a new unbounded constraint for this problem.
     * 
     * @param name
     *            the constraint name (can't be changed)
     * @return the new constraint
     */
    Constraint addConstraint(String name);

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
    Constraint addConstraint(String name, int[] coefficients, Variable[] variables, int lowerBound, int upperBound);

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
    Constraint addConstraint(String name, int[] coefficients, Variable[] variables, Number lowerBound, Number upperBound);

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
    Constraint addConstraint(String name, Linear linear, int lowerBound, int upperBound);

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
    Constraint addConstraint(String name, Linear linear, Number lowerBound, Number upperBound);

    /**
     * Create a new unbounded variable for this problem with the given name and
     * type.
     * 
     * @param name
     *            the variable name (can't be changed)
     * @param type
     *            the variable type (can't be changed)
     * @return the variable instance.
     */
    Variable addVariable(String name, VarType type);

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
    Variable addIntegerVariable(String name, Number lowerBound, Number upperBound);

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
    Linear createLinear(List<? extends Number> coefficients, List<Variable> variables);

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
     * Returns the objective value. Only valid after solving the problem.
     * 
     * @return the objective value.
     */
    Double getObjectiveValue();

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
     * Sets the objective direction.
     * 
     * @param direction
     *            the new direction value. Should be one of the MAXIMIZE or
     *            MINIMIZE constant.
     * @throws IllegalArgumentException
     *             if the direction is not MAXIMIZE or MINIMIZE
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

}
