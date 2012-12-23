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
import org.gnu.glpk.glp_smcp;
import org.gnu.glpk.glp_tree;

import com.patrikdufresne.ilp.IBranchingTechniqueLast;
import com.patrikdufresne.ilp.IBranchingTechniqueLastAlwaysDown;
import com.patrikdufresne.ilp.IFeasibilityPumpHeuristic;
import com.patrikdufresne.ilp.ILPException;
import com.patrikdufresne.ilp.ILPLogger;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.ILPPolicy;
import com.patrikdufresne.ilp.Status;

/**
 * This class may be used to create new instance of glpk problems.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKSolver implements IFeasibilityPumpHeuristic,
		IBranchingTechniqueLast, IBranchingTechniqueLastAlwaysDown {
	/**
	 * Constant value for branching last.
	 */
	private static final String BRANCHING_LAST = "last"; //$NON-NLS-1$

	/**
	 * Callback function implementing last always down branching technique.
	 */
	private static final GlpkCallbackListener BRANCHING_LAST_ALWAYS_DOWN = new GlpkCallbackListener() {
		@Override
		public void callback(glp_tree tree) {
			int reason = GLPK.glp_ios_reason(tree);
			if (reason == GLPKConstants.GLP_IBRANCH) {
				int n = GLPK.glp_get_num_cols(GLPK.glp_ios_get_prob(tree));
				int j;
				for (j = n; j >= 1; j--) {
					if (GLPK.glp_ios_can_branch(tree, j) != 0)
						break;
				}
				if (j < 1) {
					return;
				}
				GLPK.glp_ios_branch_upon(tree, j, GLPKConstants.GLP_DN_BRNCH);
			}
		}
	};

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
				ILPPolicy.log(ILPLogger.DEBUG, message);
				return false;
			}
		};

		// Add the listener
		GlpkTerminal.addListener(terminalListener);

	}

	/**
	 * Used to convert the internal constant value to the GLPK constant value
	 * 
	 * @param technique
	 *            the technique or null if not set
	 * @return one of the GLP_BR_* constant
	 */
	static int brTech(Object technique) {
		if (BRANCHING_LAST.equals(technique)) {
			return GLPKConstants.GLP_BR_LFV;
		}
		return GLPKConstants.GLP_BR_DTH;
	}

	/**
	 * Private listener to redirect the callback to an heuristic.
	 */
	// private GlpkCallbackListener callbackListener;

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

	/**
	 * Define the branching technique used by this solver. May be constant value
	 * or a custom heuristic.
	 */
	private Object brTech;

	/**
	 * True to enabled Feasibility pump heuristic.
	 */
	private boolean fpump;

	/**
	 * Default constructor.
	 */
	public GLPKSolver() {
		// Nothing to do
	}

	/**
	 * Create a new GLPK linear prolem.
	 */
	@Override
	public LinearProblem createLinearProblem() {
		return new GLPKLinearProblem();
	}

	@Override
	public void dispose() {
		// Nothing to dispose.
	}

	/**
	 * This implementation check if the constant value matchs the technique.
	 */
	@Override
	public boolean getBranchingLast() {
		return BRANCHING_LAST.equals(this.brTech);
	}

	@Override
	public boolean getBranchingLastAlwaysDown() {
		return this.brTech == BRANCHING_LAST_ALWAYS_DOWN;
	}

	@Override
	public boolean getFeasibilityPumpHeuristic() {
		return this.fpump;
	}

	/**
	 * This implementation sets the interval variable to a constant value.
	 */
	@Override
	public void setBranchingLast(boolean enabled) {
		this.brTech = enabled ? BRANCHING_LAST : null;
	}

	@Override
	public void setBranchingLastAlwaysDown(boolean enabled) {
		this.brTech = enabled ? BRANCHING_LAST_ALWAYS_DOWN : null;
	}

	/**
	 * This implementation enable the feasibility pump.
	 */
	@Override
	public void setFeasibilityPumpHeuristic(boolean enabled) {
		this.fpump = enabled;
	}

	/**
	 * This implementation solve the GLPK linear problem.
	 * <p>
	 * If the problem is identified as MIP, using the function
	 * {@link LinearProblem#isMIP()}, the function glp_intopt() is used instead
	 * of glp_simplex() to solve the problem.
	 */
	@Override
	public boolean solve(LinearProblem lp) {

		// Since GLPK is not thread safe, make sure only one thread is accessing
		// the solver.
		synchronized (GLPKSolverFactory.instance()) {

			// Check the problem
			if (lp.isDisposed()) {
				throw new ILPException(ILPException.ERROR_RESOURCE_DISPOSED);
			}

			// Make the problem as clear if a solution was found
			((GLPKLinearProblem) lp).primalFeasible = null;
			((GLPKLinearProblem) lp).dualFeasible = null;
			((GLPKLinearProblem) lp).status = null;

			// True if a feasible solution is found (not necessarily optimal)
			boolean found = false;

			// Attach a terminal listener.
			attachTerminalListener();

			int returns;
			if (lp.isMIP()) {

				glp_iocp iocp = new glp_iocp();
				GLPK.glp_init_iocp(iocp);
				iocp.setPresolve(GLPKConstants.GLP_ON);

				// Set the branching technique
				if (this.brTech != null) {
					iocp.setBr_tech(brTech(this.brTech));
				}

				try {
					// Attach listener if a custom heuristic is provided
					if (this.brTech instanceof GlpkCallbackListener) {
						addCallbackListener((GlpkCallbackListener) this.brTech);
					}

					// Enable/disable feasibility pump heuristic according to
					// fpump value
					iocp.setFp_heur(this.fpump ? GLPKConstants.GLP_ON
							: GLPKConstants.GLP_OFF);

					returns = GLPK
							.glp_intopt(((GLPKLinearProblem) lp).lp, iocp);

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
				returns = GLPK.glp_simplex(((GLPKLinearProblem) lp).lp, parm);

				// Generate exception according to return code
				checkSolverReturnCode(returns);

			}

			Status status = ((GLPKLinearProblem) lp).getStatus();
			found = status.equals(Status.FEASIBLE)
					|| status.equals(Status.OPTIMAL);

			if (found) {
				((GLPKLinearProblem) lp).clear();
			}

			return found;

		}

	}

	private List<GlpkCallbackListener> listeners;

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

}
