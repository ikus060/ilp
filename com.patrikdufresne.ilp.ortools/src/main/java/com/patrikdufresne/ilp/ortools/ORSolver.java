package com.patrikdufresne.ilp.ortools;

import com.google.ortools.linearsolver.MPSolver;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.Solver;
import com.patrikdufresne.ilp.SolverOption;
import com.patrikdufresne.ilp.Status;

public class ORSolver implements Solver {

	static {
		ORLibrary.load();
	}

	private int solvertype;

	public ORSolver() throws ClassNotFoundException, NoSuchFieldException,
			IllegalAccessException {
		this.solvertype = MPSolver
				.getSolverEnum("CBC_MIXED_INTEGER_PROGRAMMING");
	}

	@Override
	public LinearProblem createLinearProblem() {
		MPSolver solver = new MPSolver("MyProblemNAME", this.solvertype);
		return new ORLinearProblem(solver);
	}

	@Override
	public SolverOption createSolverOption() {
		return new ORSolverOption();
	}

	@Override
	public void dispose() {
		// Nothing to do
	}

	@Override
	public boolean solve(LinearProblem lp, SolverOption option) {
		if (!(lp instanceof ORLinearProblem)) {
			throw new IllegalArgumentException(
					"lp should be a GLPKLinearProblem");
		}
		if (!(option instanceof ORSolverOption)) {
			throw new IllegalArgumentException(
					"option should be a GLPKSolverOption");
		}
		ORLinearProblem orlp = (ORLinearProblem) lp;
		ORSolverOption orso = (ORSolverOption) option;

		orlp.lp.EnableOutput();

		System.out.println(orlp.lp.numVariables());

		System.out.println(orlp.lp.numConstraints());

		int resultStatus = orlp.lp.solve();

		orlp.status = status(resultStatus);

		return orlp.status.equals(Status.FEASIBLE)
				|| orlp.status.equals(Status.OPTIMAL);
	}

	/**
	 * Convert the MP result status into ILP status.
	 */
	private static Status status(int status) {
		if (status == MPSolver.OPTIMAL) {
			return Status.OPTIMAL;
		} else if (status == MPSolver.FEASIBLE) {
			return Status.FEASIBLE;
		} else if (status == MPSolver.INFEASIBLE) {
			return Status.INFEASIBLE;
		} else if (status == MPSolver.UNBOUNDED) {
			return Status.UNBOUNDED;
		}
		// MPSolver.ABNORMAL
		// MPSolver.NOT_SOLVED
		return Status.UNKNOWN;
	}

}
