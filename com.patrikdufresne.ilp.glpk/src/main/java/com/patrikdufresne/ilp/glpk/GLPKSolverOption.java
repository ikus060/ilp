package com.patrikdufresne.ilp.glpk;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkCallbackListener;
import org.gnu.glpk.glp_tree;

import com.patrikdufresne.ilp.IBranchingTechniqueLast;
import com.patrikdufresne.ilp.IBranchingTechniqueLastAlwaysDown;
import com.patrikdufresne.ilp.IFeasibilityPumpHeuristic;
import com.patrikdufresne.ilp.SolverOption;

/**
 * This implementation of {@link SolverOption} may be used with GLPK solver.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GLPKSolverOption implements IFeasibilityPumpHeuristic,
		IBranchingTechniqueLast, IBranchingTechniqueLastAlwaysDown {

	/**
	 * Constant value for branching last.
	 */
	static final String BRANCHING_LAST = "last"; //$NON-NLS-1$

	/**
	 * Callback function implementing last always down branching technique.
	 */
	static final GlpkCallbackListener BRANCHING_LAST_ALWAYS_DOWN = new GlpkCallbackListener() {
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
	 * Define the branching technique used by this solver. May be constant value
	 * or a custom heuristic.
	 */
	Object brTech;

	/**
	 * True to enabled Feasibility pump heuristic.
	 */
	boolean fpump;

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
	 * This implementation enable the feasibility pump for this solver.
	 */
	@Override
	public void setFeasibilityPumpHeuristic(boolean enabled) {
		this.fpump = enabled;
	}

}
