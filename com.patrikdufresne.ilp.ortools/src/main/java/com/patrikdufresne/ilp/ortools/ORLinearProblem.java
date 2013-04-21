package com.patrikdufresne.ilp.ortools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.patrikdufresne.ilp.AbstractLinearProblem;
import com.patrikdufresne.ilp.ConcreteLinear;
import com.patrikdufresne.ilp.ConcreteTerm;
import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Term;
import com.patrikdufresne.ilp.VarType;
import com.patrikdufresne.ilp.Variable;

public class ORLinearProblem extends AbstractLinearProblem {

	/**
	 * The infinity value.
	 */
	static final double INFINITY = MPSolver.infinity();

	/**
	 * Need to keep reference on every constraint (row).
	 */
	private List<ORConstraint> constraints;

	/**
	 * Represent the linear problem.
	 */
	MPSolver lp;

	/**
	 * The solver status. This value is set by the ORSolver.
	 */
	Status status;

	private Collection<ORConstraint> unmodifiableConstraints;

	private Collection<ORVariable> unmodifiableVariables;

	/**
	 * Need to keep reference on every variable (col).
	 */
	private List<ORVariable> variables;

	/**
	 * Create a new linear problem.
	 * 
	 * @param solver
	 */
	ORLinearProblem(MPSolver lp) {
		this.lp = lp;
		this.constraints = new ArrayList<ORConstraint>();
		this.variables = new ArrayList<ORVariable>();
	}

	/**
	 * Create the new constraint reference.
	 * 
	 * @param constraint
	 *            the instance
	 * @param name
	 *            the constraint name
	 */
	void addConstraint(ORConstraint constraint, String name) {
		constraint.parent = this;
		if (name == null) {
			constraint.c = this.lp.makeConstraint();
		} else {
			constraint.c = this.lp.makeConstraint(name);
		}
		if (constraint.c == null) {
			throw new RuntimeException("Failure to create the constraint"); //$NON-NLS-1$
		}
		this.constraints.add(constraint);
	}

	@Override
	public Constraint addConstraint(String name) {
		return new ORConstraint(this, name);
	}

	/**
	 * Create the new variable reference.
	 * 
	 * @param variable
	 *            the variable instance
	 * @param name
	 *            the variable name
	 * @param type
	 *            the variable type
	 */
	void addVariable(ORVariable variable, String name, VarType type) {
		variable.parent = this;

		if (VarType.BOOL.equals(type)) {
			variable.v = this.lp.makeBoolVar(name);
		} else {
			variable.v = this.lp.makeVar(-INFINITY, INFINITY,
					VarType.INTEGER.equals(type), name);
		}
		if (variable.v == null) {
			throw new RuntimeException("Failure to create the variable"); //$NON-NLS-1$
		}
		this.variables.add(variable);
	}

	@Override
	public Variable addVariable(String name, VarType type) {
		return new ORVariable(this, name, type);
	}

	/**
	 * Throw an exception if the problem is disposed.
	 */
	void checkProblem() {
		if (isDisposed()) {
			throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
		}
	}

	void checkSolution() {
		Status status = getStatus();
		if (!status.equals(Status.FEASIBLE) && !status.equals(Status.OPTIMAL)) {
			throw new ILPException("solution not available"); //$NON-NLS-1$
		}
	}

	/**
	 * Dispose the problem.Should remove any resources allocated for the
	 * variable and the constraints.
	 */
	@Override
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		this.lp.delete();
		this.lp = null;
		for (ORConstraint c : this.constraints) {
			c.c = null;
			c.parent = null;
		}
		for (ORVariable v : this.variables) {
			v.v = null;
			v.parent = null;
		}
	}

	/**
	 * Return an unmodifiable constraint collection.
	 */
	@Override
	public Collection<? extends Constraint> getConstraints() {
		if (this.unmodifiableConstraints == null) {
			this.unmodifiableConstraints = Collections
					.unmodifiableCollection(this.constraints);
		}
		return this.unmodifiableConstraints;
	}

	/**
	 * Return the problem name.
	 */
	@Override
	public String getName() {
		checkProblem();
		return this.lp.Name();
	}

	/**
	 * Return the direction according to minimization or maximization value.
	 */
	@Override
	public int getObjectiveDirection() {
		checkProblem();
		MPObjective objective = this.lp.objective();
		if (objective.minimization()) {
			return MINIMIZE;
		}
		if (objective.maximization()) {
			return MAXIMIZE;
		}
		throw new ILPException("Unknown direction.");
	}

	/**
	 * Create a linear from the objective coefficient.
	 */
	@Override
	public Linear getObjectiveLinear() {
		checkProblem();
		Linear linear = new ConcreteLinear();
		MPObjective objective = this.lp.objective();
		for (ORVariable v : this.variables) {
			double coef = objective.getCoefficient(v.v);
			if (coef != 0) {
				linear.add(new ConcreteTerm(Double.valueOf(coef), v));
			}
		}
		if (linear.size() == 0) {
			return null;
		}
		return linear;
	}

	/**
	 * Not supported by the backend.
	 */
	@Override
	public String getObjectiveName() {
		checkProblem();
		return "";
	}

	/**
	 * Return the objective value.
	 */
	@Override
	public Number getObjectiveValue() {
		checkProblem();
		return Double.valueOf(this.lp.objectiveValue());
	}

	/**
	 * Return the status.
	 */
	@Override
	public Status getStatus() {
		checkProblem();
		if (status == null) {
			return Status.UNKNOWN;
		}
		return status;
	}

	/**
	 * Return an unmodifiable collection of variable.
	 */
	@Override
	public Collection<ORVariable> getVariables() {
		if (this.unmodifiableVariables == null) {
			this.unmodifiableVariables = Collections
					.unmodifiableCollection(this.variables);
		}
		return this.variables;
	}

	/**
	 * Return true if the linear problem is null.
	 */
	@Override
	public boolean isDisposed() {
		return this.lp == null;
	}

	/**
	 * Remove the constraint from the linear problem.
	 * 
	 * @param constraint
	 */
	void removeConstraint(ORConstraint constraint) {
		int index;
		if (this.constraints == null
				|| (index = this.constraints.indexOf(constraint)) < 0) {
			throw new RuntimeException("ORVariable not in the variable list."); //$NON-NLS-1$
		}

		// Delete the variable
		constraint.c.delete();

		// Remove the reference from the list.
		this.constraints.remove(index);

		constraint.c = null;
		constraint.parent = null;
	}

	/**
	 * Remove the reference to the variable.
	 * 
	 * @param orVariable
	 */
	void removeVariable(ORVariable var) {
		int index;
		if (this.variables == null || (index = this.variables.indexOf(var)) < 0) {
			throw new RuntimeException("ORVariable not in the variable list."); //$NON-NLS-1$
		}

		// Delete the variable
		var.v.delete();

		// Remove the reference from the list.
		this.variables.remove(index);

		var.v = null;
		var.parent = null;

	}

	/**
	 * Sets the objective direction.
	 */
	@Override
	public void setObjectiveDirection(int direction) {
		checkProblem();
		switch (direction) {
		case MAXIMIZE:
			this.lp.setMaximization();
			break;
		case MINIMIZE:
			this.lp.setMinimization();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Sets the objective linear.
	 */
	@Override
	public void setObjectiveLinear(Linear linear) {
		checkProblem();
		// Clear the previous objective linear.
		MPObjective objective = this.lp.objective();
		objective.clear();
		// Sets the objective
		if (linear != null && linear.size() > 0) {
			for (Term term : linear) {
				double coef = term.getCoefficient().doubleValue();
				if (coef != 0) {
					objective.setCoefficient(
							((ORVariable) term.getVariable()).v, coef);
				}
			}
		}
	}
}
