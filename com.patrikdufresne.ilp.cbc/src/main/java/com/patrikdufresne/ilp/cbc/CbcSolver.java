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
package com.patrikdufresne.ilp.cbc;

import java.util.Arrays;
import java.util.List;

import com.patrikdufresne.cbc4j.CBCLibrary;
import com.patrikdufresne.cbc4j.SWIGTYPE_p_CbcModel;
import com.patrikdufresne.cbc4j.cbc4j;
import com.patrikdufresne.ilp.ILPLogger;
import com.patrikdufresne.ilp.ILPPolicy;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.Solver;
import com.patrikdufresne.ilp.SolverOption;
import com.patrikdufresne.ilp.Status;

/**
 * Cbc solver
 * 
 * @author Patrik Dufresne
 * 
 */
public class CbcSolver implements Solver {

    static {
        CBCLibrary.load();
    }

    /**
     * <pre>
     * 0 - none
     * 1 - minimal
     * 2 - normal low
     * 3 - normal high
     * 4 - verbose
     * </pre>
     * 
     * @return
     */
    private static int getCbcLogLevel() {
        switch (ILPPolicy.getLog().getLevel()) {
        case ILPLogger.TRACE:
            return 4;
        case ILPLogger.DEBUG:
            return 3;
        case ILPLogger.INFO:
            return 2;
        case ILPLogger.WARNING:
            return 0;
        case ILPLogger.ERROR:
        default:
            return 0;
        }
    }

    public CbcSolver() {
        // Create a new reference to cbc solver.
    }

    /**
     * <pre>
     * clp status
     *   -1 - unknown e.g. before solve or if postSolve says not optimal
     *   0 - optimal
     *   1 - primal infeasible
     *   2 - dual infeasible
     *   3 - stopped on iterations or time
     *   4 - stopped due to errors
     *   5 - stopped by event handler (virtual int ClpEventHandler::event()) 
     * cbc status
     *   -1 before branchAndBound
     *   0 finished - check isProvenOptimal or isProvenInfeasible to see if solution found
     *   (or check value of best solution)
     *   1 stopped - on maxnodes, maxsols, maxtime
     *   2 difficulties so run was abandoned
     *   (5 event user programmed event occurred) 
     * clp secondary status of problem - may get extended
     *   0 - none
     *   1 - primal infeasible because dual limit reached OR probably primal
     *   infeasible but can't prove it (main status 4)
     *   2 - scaled problem optimal - unscaled problem has primal infeasibilities
     *   3 - scaled problem optimal - unscaled problem has dual infeasibilities
     *   4 - scaled problem optimal - unscaled problem has primal and dual infeasibilities
     *   5 - giving up in primal with flagged variables
     *   6 - failed due to empty problem check
     *   7 - postSolve says not optimal
     *   8 - failed due to bad element check
     *   9 - status was 3 and stopped on time
     *   100 up - translation of enum from ClpEventHandler
     *      
     * cbc secondary status of problem
     *   -1 unset (status_ will also be -1)
     *   0 search completed with solution
     *   1 linear relaxation not feasible (or worse than cutoff)
     *   2 stopped on gap
     *   3 stopped on nodes
     *   4 stopped on time
     *   5 stopped on user event
     *   6 stopped on solutions
     *   7 linear relaxation unbounded
     *   8 stopped on iterations limit
     * </pre>
     * 
     * @param status
     * @param secondaryStatus
     * @return
     */
    Status checkStatus(SWIGTYPE_p_CbcModel cbcModel) {
        int status = cbc4j.status(cbcModel);
        if (status == 0) {
            if (cbc4j.isProvenOptimal(cbcModel)) {
                return Status.OPTIMAL;
            } else if (cbc4j.isProvenInfeasible(cbcModel)) {
                return Status.INFEASIBLE;
            }
            throw new IllegalStateException("cbc solver finish without a known status");
        }
        return Status.UNKNOWN;
    }

    @Override
    public LinearProblem createLinearProblem() {
        return new CbcLinearProblem();
    }

    @Override
    public SolverOption createSolverOption() {
        return new CbcSolverOption();
    }

    @Override
    public void dispose() {
        // Nothing to do.
    }

    @Override
    public boolean solve(LinearProblem lp, SolverOption option) {
        // Check arguments value.
        if (lp == null || !(lp instanceof CbcLinearProblem)) {
            throw new IllegalArgumentException("invalid linear problem");
        }
        if (!(option instanceof CbcSolverOption)) {
            throw new IllegalArgumentException("invalid solver option");
        }
        long start = 0;
        boolean traceEnabled = ILPPolicy.getLog().getLevel() == ILPLogger.TRACE;
        CbcLinearProblem cbclp = (CbcLinearProblem) lp;

        // Start the time counter;
        if (traceEnabled) {
            start = System.currentTimeMillis();
        }

        // Release the previous solution.
        cbclp.status = Status.UNKNOWN;
        cbclp.bestSolution = null;
        cbclp.objValue = null;
        
        // Flush java output.
        System.out.flush();

        // Build solver arguments.
        ((CbcSolverOption) option).setLogLevel(getCbcLogLevel());
        ((CbcSolverOption) option).setSLogLevel(getCbcLogLevel());
        List<String> list = ((CbcSolverOption) option).getArgs();
        String args[] = list.toArray(new String[list.size()]);
        // Print the arguments. Should be removed.
        ILPPolicy.getLog().log(ILPLogger.TRACE, "arguments: " + list);

        /* Call CBC solver. */
        // Make of copy of the original Lp to avoid side effect when solving the problem.
        SWIGTYPE_p_CbcModel cbcModel = cbc4j.newCbcModel(cbclp.lp);
        try {
            cbc4j.callCbc0(cbcModel);
            cbc4j.callCbc1(args.length, args, cbcModel);

            // Print the total time took to run CBC.
            if (traceEnabled) {
                ILPPolicy.getLog().log(ILPLogger.TRACE, "cbc solver took " + (System.currentTimeMillis() - start) + " ms");
            }

            // Check the status, retrieve the best solution, get the objective value.
            cbclp.status = checkStatus(cbcModel);
            cbclp.bestSolution = cbc4j.bestSolution(cbcModel);
            cbclp.objValue = cbc4j.getObjValue(cbcModel);

        } finally {
            // Release the cbcModel
            cbc4j.deleteCbcModel(cbcModel);
        }

        return Status.FEASIBLE.equals(cbclp.status) || Status.OPTIMAL.equals(cbclp.status);
    }
}
