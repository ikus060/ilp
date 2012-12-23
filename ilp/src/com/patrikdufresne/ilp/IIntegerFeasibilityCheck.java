package com.patrikdufresne.ilp;

/**
 * When enabled, allow the solver to run a feasibility check using a different
 * algorithm then simplex or branch and bound. A common algorithm is to use
 * miniSAT.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IIntegerFeasibilityCheck extends SolverOption {

	/**
	 * Enable or disable the feasibility check.
	 * 
	 * @param enabled
	 *            True to enable the feasibility check
	 */
	void setIntegerFeasibilitCheck(boolean enabled);

	/**
	 * Return the feasibility check enable state.
	 * 
	 * @return True if the feasibility check is enable.
	 */
	boolean getIntegerFeasibilityCheck();

}
