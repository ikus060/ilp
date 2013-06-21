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
package com.patrikdufresne.ilp.glpk;

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

    static {
        GLPKLibrary.load();
    }

    /**
     * Private listener to send message trough Policy logger.
     */
    private static GlpkTerminalListener terminalListener;

    /**
     * Convert the status code into {@link Status} object.
     * 
     * @param status
     *            the status code return by
     *            {@link GLPK#glp_get_status(glp_prob)}.
     * @return a Status object
     */
    private static Status status(int status) {
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
     * Attach the listener. Notice, the terminal listener doesn't required to be
     * release, because the listener is shared across all thread and GLPKSolver.
     */
    private static void attachTerminalListener() {

        // According to glpk-java, glp_term_hook should be called to register
        // the callback function for every thread access.
        GLPK.glp_term_hook(null, null);

        // Check if the listener already exists.
        if (terminalListener != null) return;

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
            throw new ILPException("The search was prematurely terminated due " + "to the solver failure.");
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
            throw new ILPException("The search was prematurely terminated, " + "because the simplex iteration limit has been exceeded.");
        } else if (code == GLPKConstants.GLP_ETMLIM) {
            throw new ILPException("The search was prematurely terminated, " + "because the time limit has been exceeded.");
        } else if (code == GLPKConstants.GLP_ENODFS) {
            throw new ILPException("The LP problem instance has no dual " + "feasible solution (only if the LP presolver is used).");
        } else if (code == GLPKConstants.GLP_ENOPFS) {
            throw new ILPException("Unable to start the search, because LP " + "relaxation of the MIP problem instance has no primal " + "feasible solution.");
        } else {
            throw new ILPException("unknown error occur during the " + "optimization process.");
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
            throw new IllegalArgumentException("lp should be a GLPKLinearProblem");
        }
        if (!(option instanceof GLPKSolverOption)) {
            throw new IllegalArgumentException("option should be a GLPKSolverOption");
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
            glpklp.status = null;

            // Attach a terminal listener.
            attachTerminalListener();

            int returns;
            if (glpklp.isMIP()) {

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
                    iocp.setFp_heur(glpkopt.fpump ? GLPKConstants.GLP_ON : GLPKConstants.GLP_OFF);

                    // Copy the problem, and solve it. Otherwise, their is a
                    // data corruption.
                    glp_prob copy = GLPK.glp_create_prob();
                    GLPK.glp_copy_prob(copy, glpklp.lp, GLPKConstants.GLP_ON);
                    GLPK.glp_delete_prob(glpklp.lp);
                    glpklp.lp = copy;
                    returns = GLPK.glp_intopt(glpklp.lp, iocp);
                    if (returns == GLPKConstants.GLP_ENOPFS) {
                        glpklp.status = Status.INFEASIBLE;
                    } else {
                        // Generate exception according to return code
                        checkSolverReturnCode(returns);
                        // Get the MIP status
                        glpklp.status = status(GLPK.glp_mip_status(glpklp.lp));
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

                // Get the solver status.
                glpklp.status = status(GLPK.glp_get_status(glpklp.lp));

            }

            return glpklp.status.equals(Status.FEASIBLE) || glpklp.status.equals(Status.OPTIMAL);

        }

    }

}
