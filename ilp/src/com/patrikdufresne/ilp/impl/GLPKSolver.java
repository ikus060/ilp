/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp.impl;

import java.util.ArrayList;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkCallback;
import org.gnu.glpk.GlpkCallbackListener;
import org.gnu.glpk.GlpkTerminal;
import org.gnu.glpk.GlpkTerminalListener;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.ILPLogger;
import com.patrikdufresne.ilp.ILPPolicy;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.Solver;
import com.patrikdufresne.ilp.SolverOption;
import com.patrikdufresne.ilp.Status;

/**
 * This class may be used to create new instance of glpk problems.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKSolver implements Solver {

	/**
	 * Private listener to send message trough Policy logger.
	 */
	private static GlpkTerminalListener terminalListener;

	/**
	 * Attach the listener. Notice, the terminal listener doesn't required to be
	 * release, because the listener is shared across all thread and GLPKSolver.
	 */
	private static void attachTerminalListener() {

		// According to glpk-java, glp_term_hook should be called to register
		// the callback function for every thread access.
		GLPK.glp_term_hook(null, null);

		// Check if the listener already exists.
		if (terminalListener != null)
			return;

		// Create a new terminal listener to redirect the terminal output into
		// the ILogger framework.
		terminalListener = new GlpkTerminalListener() {
			@Override
			public boolean output(String str) {
				String message = str;
				if (message.endsWith("\n")) { //$NON-NLS-1$
					message = message.substring(0, str.length() - 1);
				}
				ILPPolicy.log(ILPPolicy.getLog().getLevel(), message);
				return false;
			}
		};

		// Add the listener
		GlpkTerminal.addListener(terminalListener);

	}

	/**
	 * Check the return code of glp_simplex() and glp_exact().
	 * 
	 * @param code
	 *            the return code
	 */
	static void checkSolverReturnCode(int code) {
		if (code == 0) {
			return;
		} else if (code == GLPKConstants.GLP_EBADB) {
			throw new ILPException("Unable to start the search, because "
					+ "the initial basis specified in the problem "
					+ "object is invalid -- the number of basic (auxiliary "
					+ "and structural) variables is not the same as the "
					+ "number of rows in the problem object.");
		} else if (code == GLPKConstants.GLP_ESING) {
			throw new ILPException("Unable to start the search, because the "
					+ "basis matrix corresponding to the initial basis is "
					+ "singular within the working precision.");
		} else if (code == GLPKConstants.GLP_ECOND) {
			throw new ILPException("Unable to start the search, because the "
					+ "basis matrix corresponding to the initial basis is "
					+ "ill-conditioned, i.e. its condition number "
					+ "is too large.");
		} else if (code == GLPKConstants.GLP_EBOUND) {
			throw new ILPException("Unable to start the search, because some "
					+ "double-bounded (auxiliary or structural) variables "
					+ "have incorrect bounds.");
		} else if (code == GLPKConstants.GLP_EFAIL) {
			throw new ILPException("The search was prematurely terminated due "
					+ "to the solver failure.");
		} else if (code == GLPKConstants.GLP_EOBJLL) {
			throw new ILPException("The search was prematurely terminated, "
					+ "because the objective function being "
					+ "maximized has reached its lower limit and "
					+ "continues decreasing (the dual simplex only).");
		} else if (code == GLPKConstants.GLP_EOBJUL) {
			throw new ILPException("The search was prematurely terminated, "
					+ "because the objective function being "
					+ "minimized has reached its upper limit and "
					+ "continues increasing (the dual simplex only).");
		} else if (code == GLPKConstants.GLP_EITLIM) {
			throw new ILPException("The search was prematurely terminated, "
					+ "because the simplex iteration limit has been exceeded.");
		} else if (code == GLPKConstants.GLP_ETMLIM) {
			throw new ILPException("The search was prematurely terminated, "
					+ "because the time limit has been exceeded.");
		} else if (code == GLPKConstants.GLP_ENODFS) {
			throw new ILPException("The LP problem instance has no dual "
					+ "feasible solution (only if the LP presolver is used).");
		} else if (code == GLPKConstants.GLP_ENOPFS) {
			throw new ILPException("Unable to start the search, because LP "
					+ "relaxation of the MIP problem instance has no primal "
					+ "feasible solution.");
		} else {
			throw new ILPException("unknown error occur during the "
					+ "optimization process.");
		}

	}

	private List<GlpkCallbackListener> listeners;

	/**
	 * Default constructor.
	 */
	public GLPKSolver() {
		// Nothing to do
	}

	/**
	 * This function is used to add a callback listener to GLPK. This function
	 * also make sure to keep track of every listeners attached to all of them
	 * can be detach after the solving process.
	 * 
	 * @param listener
	 */
	private void addCallbackListener(GlpkCallbackListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<GlpkCallbackListener>();
		}
		this.listeners.add(listener);
		GlpkCallback.addListener(listener);
	}

	/**
	 * Create a new GLPK linear prolem.
	 */
	@Override
	public LinearProblem createLinearProblem() {
		return new GLPKLinearProblem();
	}

	/**
	 * Create a new GLPK solver option.
	 * 
	 * @return
	 */
	public SolverOption createSolverOption() {
		return new GLPKSolverOption();
	}

	@Override
	public void dispose() {
		// Nothing to dispose.
	}

	/**
	 * Return the log level according to the ILPLogger log level.
	 * 
	 * <pre>
	 * GLP_MSG_OFF—no output;
	 * GLP_MSG_ERR—error and warning messages only;
	 * GLP_MSG_ON —normal output;
	 * GLP_MSG_ALL—full output (including informational messages).
	 * </pre>
	 */
	private int logLevel() {
		switch (ILPPolicy.getLog().getLevel()) {
		case ILPLogger.DEBUG:
			return GLPKConstants.GLP_MSG_ALL;
		case ILPLogger.INFO:
		case ILPLogger.WARNING:
		default:
			return GLPKConstants.GLP_MSG_ERR;
		}
	}

	/**
	 * This function is used to remove any previously added listener from GLPK.
	 * Does nothing it there wasn't any listener added using
	 * {@link #addCallbackListener(GlpkCallbackListener)}.
	 * 
	 */
	private void removeAllCallbackListener() {
		if (this.listeners == null) {
			return;
		}
		for (GlpkCallbackListener listener : this.listeners) {
			GlpkCallback.removeListener(listener);
		}
	}

	/**
	 * This implementation solve the GLPK linear problem.
	 * <p>
	 * If the problem is identified as MIP, using the function
	 * {@link LinearProblem#isMIP()}, the function glp_intopt() is used instead
	 * of glp_simplex() to solve the problem.
	 */
	@Override
	public boolean solve(LinearProblem lp, SolverOption option) {
		if (!(lp instanceof GLPKLinearProblem)) {
			throw new IllegalArgumentException(
					"lp should be a GLPKLinearProblem");
		}
		if (!(option instanceof GLPKSolverOption)) {
			throw new IllegalArgumentException(
					"option should be a GLPKSolverOption");
		}
		GLPKLinearProblem glpklp = (GLPKLinearProblem) lp;
		GLPKSolverOption glpkopt = (GLPKSolverOption) option;

		// Since GLPK is not thread safe, make sure only one thread is accessing
		// the solver.
		synchronized (GLPKSolverFactory.instance()) {

			// Check the problem
			if (lp.isDisposed()) {
				throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
			}

			// Make the problem as clear if a solution was found
			glpklp.primalFeasible = null;
			glpklp.dualFeasible = null;
			glpklp.status = null;

			// True if a feasible solution is found (not necessarily optimal)
			boolean found = false;

			// Attach a terminal listener.
			attachTerminalListener();

			int returns;
			if (glpkopt.intfeasible) {

				// Run the solver
				returns = GLPK.glp_intfeas1(glpklp.lp, 0, 0);

			} else if (glpklp.isMIP()) {

				glp_iocp iocp = new glp_iocp();
				GLPK.glp_init_iocp(iocp);
				iocp.setPresolve(GLPKConstants.GLP_ON);
				iocp.setMsg_lev(logLevel());

				// Set the branching technique
				if (glpkopt.brTech != null) {
					iocp.setBr_tech(GLPKSolverOption.brTech(glpkopt.brTech));
				}

				try {
					// Attach listener if a custom heuristic is provided
					if (glpkopt.brTech instanceof GlpkCallbackListener) {
						addCallbackListener((GlpkCallbackListener) glpkopt.brTech);
					}

					// Enable/disable feasibility pump heuristic according to
					// fpump value
					iocp.setFp_heur(glpkopt.fpump ? GLPKConstants.GLP_ON
							: GLPKConstants.GLP_OFF);

					// Copy the problem, and solve it. Otherwise, their is a
					// data corruption.
					glp_prob copy = GLPK.glp_create_prob();
					GLPK.glp_copy_prob(copy, glpklp.lp, GLPKConstants.GLP_ON);
					GLPK.glp_delete_prob(glpklp.lp);
					glpklp.lp = copy;
					returns = GLPK.glp_intopt(glpklp.lp, iocp);

					if (returns != GLPKConstants.GLP_ENOPFS) {
						// Generate exception according to return code
						checkSolverReturnCode(returns);
					}
				} finally {
					// Remove listeners, otherwise listener keep referencing
					// this solver.
					removeAllCallbackListener();
				}

			} else {
				glp_smcp parm = new glp_smcp();
				GLPK.glp_init_smcp(parm);

				// Run the simplex algorithm
				returns = GLPK.glp_simplex(glpklp.lp, parm);

				// Generate exception according to return code
				checkSolverReturnCode(returns);

			}

			Status status = glpklp.getStatus();
			found = status.equals(Status.FEASIBLE)
					|| status.equals(Status.OPTIMAL);

			if (found) {
				glpklp.clear();
			}

			return found;

		}

	}

}
