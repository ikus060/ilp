package com.patrikdufresne.ilp;

/**
 * This interface force the solver to use a specific root algorithm to solve the
 * linear problem.
 * <p>
 * Solver implementing this interface will use the SAT/miniSAT algorithm to find
 * a feasible solution for a 0-1 problem. If the problem is not compatible, an
 * exception will be thrown.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface ICheckIntegerFeasibility extends SolverOption {

	/**
	 * Enable or disable the integer feasibility check.
	 * 
	 * @param enabled
	 *            True to enable the feasibility check
	 */
	void setCheckIntegerFeasibility(boolean enabled);

	/**
	 * Check if this integer feasibility check is enabled.
	 * 
	 * @return True if the integer feasibility check is enabled.
	 */
	boolean getCheckIntegerFeasibility();

}
