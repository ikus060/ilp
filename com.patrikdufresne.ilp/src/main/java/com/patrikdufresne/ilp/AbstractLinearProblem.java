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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Partial implementation of the {@link LinearProblem} interface.
 * 
 * @author Patrik Dufresne
 * 
 */
public abstract class AbstractLinearProblem implements LinearProblem {

    /**
     * Check if the linear reference disposed variables.
     * 
     * @param linear
     *            the linear
     */
    public static void checkLinear(Linear linear) {
        if (linear == null || linear.size() == 0) {
            return;
        }
        for (Term term : linear) {
            checkVariable(term.getVariable());
        }
    }

    /**
     * Throw an exception if the variable is disposed.
     * 
     * @param variable
     *            the variable to validate or null.
     */
    public static void checkVariable(Variable variable) {
        if (variable != null && variable.isDisposed()) {
            throw new IllegalArgumentException("variables is disposed.");
        }
    }

    /**
     * This implementation is creating a new variable using {@link #addVariable()} and sets the name and type using the
     * variable setters.
     */
    @Override
    public Variable addBinaryVariable(String name) {
        return addVariable(name, VarType.BOOL);
    }

    /**
     * This implementation use the function {@link #createLinear(int[], Variable[])} to create a new Linear object for
     * <code>coefficients</code> and <code>variables</code>. Then {@link #addConstraint(String, Linear, Number, Number)}
     * is called to create the new constraint.
     */
    @Override
    public Constraint addConstraint(String name, int[] coefficients, Variable[] variables, int lowerBound, int upperBound) {
        return addConstraint(name, createLinear(coefficients, variables), Integer.valueOf(lowerBound), Integer.valueOf(upperBound));
    }

    @Override
    public Constraint addConstraint(String name, int[] coefficients, Variable[] variables, Number lowerBound, Number upperBound) {
        return addConstraint(name, createLinear(coefficients, variables), lowerBound, upperBound);
    }

    /**
     * This implementation is calling {@link #addConstraint(String, Linear, Number, Number)}
     */
    @Override
    public Constraint addConstraint(String name, Linear linear, int lowerBound, int upperBound) {
        return addConstraint(name, linear, Integer.valueOf(lowerBound), Integer.valueOf(upperBound));
    }

    /**
     * This implementation is calling {@link #addConstraint()} and then sets the name, the linear expression and the
     * bounds using the constraint setters.
     */
    @Override
    public Constraint addConstraint(String name, Linear linear, Number lowerBound, Number upperBound) {
        Constraint constraint = addConstraint(name);
        constraint.setLinear(linear);
        constraint.setLowerBound(lowerBound);
        constraint.setUpperBound(upperBound);
        return constraint;
    }

    @Override
    public Variable addIntegerVariable(String name, Number lowerBound, Number upperBound) {
        Variable var = addVariable(name, VarType.INTEGER);
        var.setLowerBound(lowerBound);
        var.setUpperBound(upperBound);
        return var;
    }

    /**
     * This implementation return a {@link ConcreteLinear} object since GLPK doesn't support dynamic array.
     */
    @Override
    public Linear createLinear() {
        return new ConcreteLinear();
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficient and variable.
     */
    @Override
    public Linear createLinear(double coefficient, Variable variable) {
        return createLinear(Double.valueOf(coefficient), variable);
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficients and variables.
     */
    @Override
    public Linear createLinear(double[] coefficients, Variable[] variables) {
        Double[] list = new Double[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            list[i] = Double.valueOf(coefficients[i]);
        }
        return createLinear(list, variables);
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficient and variable.
     */
    @Override
    public Linear createLinear(float coefficient, Variable variable) {
        return createLinear(Float.valueOf(coefficient), variable);
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficients and variables.
     */
    @Override
    public Linear createLinear(float[] coefficients, Variable[] variables) {
        Float[] list = new Float[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            list[i] = Float.valueOf(coefficients[i]);
        }
        return createLinear(list, variables);
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficient and variable.
     */
    @Override
    public Linear createLinear(int coefficient, Variable variable) {
        return createLinear(Integer.valueOf(coefficient), variable);
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficients and variables.
     */
    @Override
    public Linear createLinear(int[] coefficients, Variable[] variables) {
        Integer[] list = new Integer[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            list[i] = Integer.valueOf(coefficients[i]);
        }
        return createLinear(list, variables);
    }

    /**
     * This implementation is creating a new {@link Linear} instance for the current problem. This function make
     * repetitive call to {@link #createTerm(Number, Variable)}. If the coefficient list contains null value or zero,
     * the term is not added. If the variable list contains null value, the term is not added.
     */
    @Override
    public Linear createLinear(List<? extends Number> coefficients, List<Variable> variables) {
        if (coefficients == null || variables == null) {
            throw new NullPointerException();
        }
        if (coefficients.size() != variables.size()) {
            throw new IllegalArgumentException("coefficients size != variables size"); //$NON-NLS-1$
        }

        Linear linear = createLinear();
        Iterator<? extends Number> iter1 = coefficients.iterator();
        Iterator<Variable> iter2 = variables.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            Number coef = iter1.next();
            Variable var = iter2.next();
            if (coef != null && coef.doubleValue() != 0 && var != null) {
                linear.add(createTerm(coef, var));
            }
        }

        return linear;
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficient and variable.
     */
    @Override
    public Linear createLinear(Number coefficient, Variable variable) {
        if (coefficient == null || variable == null) {
            throw new NullPointerException();
        }
        Linear linear = createLinear();
        linear.add(createTerm(coefficient, variable));
        return linear;
    }

    /**
     * This implementation is creating a new Linear object for the current problem using {@link #createLinear()} and
     * then add the new coefficients and variables.
     */
    @Override
    public Linear createLinear(Number[] coefficients, Variable[] variables) {
        return createLinear(Arrays.asList(coefficients), Arrays.asList(variables));
    }

    /**
     * This implementation is calling the function {@link #createTerm(Number, Variable)}.
     */
    @Override
    public Term createTerm(double coefficient, Variable variable) {
        return createTerm(Double.valueOf(coefficient), variable);
    }

    /**
     * This implementation is calling the function {@link #createTerm(Number, Variable)}.
     */
    @Override
    public Term createTerm(float coefficient, Variable variable) {
        return createTerm(Float.valueOf(coefficient), variable);
    }

    /**
     * This implementation is calling the function {@link #createTerm(Number, Variable)}.
     */
    @Override
    public Term createTerm(int coefficient, Variable variable) {
        return createTerm(Integer.valueOf(coefficient), variable);
    }

    @Override
    public Term createTerm(Number coefficient, Variable variable) {
        return new ConcreteTerm(coefficient, variable);
    }

    /**
     * This implementation check each variable type to determine their types.
     */
    @Override
    public boolean isMIP() {
        for (Variable var : getVariables()) {
            if (var.getType().equals(VarType.REAL)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        // Print the solution
        Status status = getStatus();
        boolean solutionAvailable = status.equals(Status.OPTIMAL) || status.equals(Status.FEASIBLE);
        if (solutionAvailable) {
            buf.append(getObjectiveName());
            buf.append("="); //$NON-NLS-1$
            buf.append(getObjectiveValue());
            buf.append("\r\n"); //$NON-NLS-1$
        }
        if (solutionAvailable) {
            for (Variable var : getVariables()) {
                buf.append(var.getName());
                buf.append("="); //$NON-NLS-1$
                buf.append(var.getValue());
                buf.append("\r\n"); //$NON-NLS-1$
            }
            for (Constraint constraint : getConstraints()) {
                buf.append("Constraint["); //$NON-NLS-1$
                buf.append(constraint.getName());
                buf.append("]="); //$NON-NLS-1$
                buf.append(constraint.getValue());
                buf.append("\r\n"); //$NON-NLS-1$
            }
        } else {
            for (Variable var : getVariables()) {
                buf.append(var.getName());
                buf.append(":{"); //$NON-NLS-1$
                buf.append(var.getLowerBound());
                buf.append(".."); //$NON-NLS-1$
                buf.append(var.getUpperBound());
                buf.append("}\r\n"); //$NON-NLS-1$
            }
            for (Constraint constraint : getConstraints()) {
                buf.append("Constraint["); //$NON-NLS-1$
                buf.append(constraint.getName());
                buf.append("]:{"); //$NON-NLS-1$
                buf.append(constraint.getLowerBound());
                buf.append(".."); //$NON-NLS-1$
                buf.append(constraint.getUpperBound());
                buf.append("}\r\n"); //$NON-NLS-1$
            }
        }
        return buf.toString();
    }

}
