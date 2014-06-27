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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.patrikdufresne.ilp.SolverOption;

/**
 * Cbc solver options.
 * 
 * @author Patrik Dufresne
 * 
 */
public class CbcSolverOption implements SolverOption {

    private static final String COMBINE_SOLUTIONS = "-combineSolutions";
    private static final String COST_STRATEGY = "costStrategy";
    private static final String CUTSONOFF = "-cutsonoff";
    private static final String FEASIBILITY_PUMP = "-feasibilityPump";
    private static final String GMI_CUTS = "GMICuts";
    private static final String HEURISTICS_ON_OFF = "-heuristicsOnOff";
    private static final String LATWOMIR_CUTS = "latwomirCuts";
    private static final String LOGLEVEL = "-logLevel";
    private static final String OFF = "off";
    private static final String ON = "on";
    private static final String PERTURBATION = "perturbation";
    private static final String PREPROCESS = "-preprocess";
    private static final String PRESOLVE = "presolve";
    private static final String PROBING_CUTS = "probingCuts";
    private static final String PROBNAME = "problem-name";
    private static final String REDUCE_AND_SPLIT_CUTS = "reduceAndSplitCuts";
    private static final String SLOGLEVEL = "-slogLevel";
    private static final String SOLVE = "-solve";
    private static final String STRATEGY = "-strategy";
    private static final String TRUST_PSEUDO_COST = "-trustPseudoCosts";

    private Boolean combineSolutions; // Base at on
    private CostStrategy costStrategy; // Base at off.
    private Boolean cutsOnOff; // Base at on
    private Boolean feasibilityPump; // Base at on
    private GMICuts gmiCuts;
    private Boolean heuristicsOnOff; // Base at on.
    private LatwomirCuts latwomirCuts;
    private Integer logLevel = 0; // Base at 1
    private Boolean perturbation;
    private Preprocess preprocess; // Base at sos
    private Presolve presolve;
    private ProbingCuts probingCuts;

    private ReduceAndSplitCuts reduceAndSplitCuts;

    private Integer sLogLevel; // Base at 1
    private Integer strategy; // Base at 1
    private Integer trustPseudoCost; // Base at 5

    /**
     * Used to generate the list of argument to be passed to CbcMain.
     * 
     * @return the argument list.
     */
    public List<String> getArgs() {
        List<String> args = new ArrayList<String>();
        args.add(PROBNAME);
        if (this.preprocess != null) {
            args.addAll(Arrays.asList(PREPROCESS, this.preprocess.toString()));
        }
        if (this.feasibilityPump != null) {
            args.addAll(Arrays.asList(FEASIBILITY_PUMP, this.feasibilityPump ? ON : OFF));
        }
        if (this.cutsOnOff != null) {
            args.addAll(Arrays.asList(CUTSONOFF, this.cutsOnOff ? ON : OFF));
        }
        if (this.heuristicsOnOff != null) {
            args.addAll(Arrays.asList(HEURISTICS_ON_OFF, this.heuristicsOnOff ? ON : OFF));
        }
        if (this.combineSolutions != null) {
            args.addAll(Arrays.asList(COMBINE_SOLUTIONS, this.combineSolutions ? ON : OFF));
        }
        if (this.strategy != null) {
            args.addAll(Arrays.asList(STRATEGY, Integer.toString(this.strategy)));
        }
        if (this.logLevel != null) {
            args.addAll(Arrays.asList(LOGLEVEL, Integer.toString(this.logLevel)));
        }
        if (this.sLogLevel != null) {
            args.addAll(Arrays.asList(SLOGLEVEL, Integer.toString(this.sLogLevel)));
        }
        if (this.trustPseudoCost != null) {
            args.addAll(Arrays.asList(TRUST_PSEUDO_COST, Integer.toString(this.trustPseudoCost)));
        }
        if (this.costStrategy != null) {
            args.addAll(Arrays.asList(COST_STRATEGY, this.costStrategy.toString()));
        }
        if (this.reduceAndSplitCuts != null) {
            args.addAll(Arrays.asList(REDUCE_AND_SPLIT_CUTS, this.reduceAndSplitCuts.toString()));
        }
        if (this.gmiCuts != null) {
            args.addAll(Arrays.asList(GMI_CUTS, this.gmiCuts.toString()));
        }
        if (this.latwomirCuts != null) {
            args.addAll(Arrays.asList(LATWOMIR_CUTS, this.latwomirCuts.toString()));
        }
        if (this.probingCuts != null) {
            args.addAll(Arrays.asList(PROBING_CUTS, this.probingCuts.toString()));
        }
        if (this.presolve != null) {
            args.addAll(Arrays.asList(PRESOLVE, this.presolve.toString()));
        }
        if (this.perturbation != null) {
            args.addAll(Arrays.asList(PERTURBATION, this.perturbation ? ON : OFF));
        }
        args.add(SOLVE);
        return args;
    }

    /**
     * Return the current cost strategy to be used or null to use default.
     * 
     * @return
     */
    public CostStrategy getCostStrategy() {
        return costStrategy;
    }

    /**
     * Return the cutsOnOff value or null if undefined. Base value at on.
     * 
     * @return cutsOnOff value or null
     */
    public Boolean getCutsOnOff() {
        return this.cutsOnOff;
    }

    /**
     * Return the <code>feas(ibilityPump)</code> value or null if undefined. Base value at on.
     * 
     * @return feasibilityPump value or null
     */
    public Boolean getFeasibilityPump() {
        return this.feasibilityPump;
    }

    public GMICuts getGmiCuts() {
        return gmiCuts;
    }

    /**
     * Return the state of <code>heur(isticsOnOff)</code>.
     * 
     * @return True if the <code>heur(isticsOnOff)</code> is turn on, false to disable heuristic or null tu use default.
     */
    public Boolean getHeuristicsOnOff() {
        return heuristicsOnOff;
    }

    public LatwomirCuts getLatwomirCuts() {
        return latwomirCuts;
    }

    /**
     * Return the logLevel value or null if undefined. Base value at 1.
     * 
     * @return logLevel value or null
     */
    public Integer getLogLevel() {
        return this.logLevel;
    }

    public Boolean getPerturbation() {
        return perturbation;
    }

    /**
     * Return the preprocess value or null if undefined. Base value at sos.
     * 
     * @return preprocess value or null
     */
    public Preprocess getPreprocess() {
        return this.preprocess;
    }

    public Presolve getPresolve() {
        return presolve;
    }

    public ProbingCuts getProbingCuts() {
        return probingCuts;
    }

    public ReduceAndSplitCuts getReduceAndSplit() {
        return reduceAndSplitCuts;
    }

    /**
     * Return the sLogLevel value or null if undefined. Base value at 1.
     * 
     * @return sLogLevel value or null
     */
    public Integer getSLogLevel() {
        return this.sLogLevel;
    }

    /**
     * Return the strategy value or null if undefined. Base value at 1.
     * 
     * @return strategy value or null.
     */
    public Integer getStrategy() {
        return this.strategy;
    }

    /**
     * Return the trustPseudoCost value or null if undefined. Base value at 5.
     * 
     * @return trustPseudoCost value or null
     */
    public Integer getTrustPseudoCost() {
        return this.trustPseudoCost;
    }

    /**
     * Return the combineSolution value or null if undefined. Base value at on.
     * 
     * @return combineSolution value or null
     */
    public Boolean isCombineSolutions() {
        return this.combineSolutions;
    }

    /**
     * Sets or unset the combineSolution value.
     * 
     * @param strategy
     *            the new combineSolution value or null.
     */
    public void setCombineSolutions(Boolean combineSolutions) {
        this.combineSolutions = combineSolutions;
    }

    /**
     * Sets how to use costs as priorities. This orders the variables in order of their absolute costs - with largest
     * cost ones being branched on first. This primitive strategy can be surprsingly effective. The column order option
     * is obviously not on costs but easy to code here. Default off.
     * 
     * @param costStrategy
     *            off, pri(orities), column(Order?), 01f(irst?), 01l(ast?), singletons or nonzero
     */
    public void setCostStrategy(CostStrategy costStrategy) {
        this.costStrategy = costStrategy;
    }

    /**
     * Sets or unset the cutsOnOff value.
     * 
     * @param strategy
     *            the new cutsOnOff value or null.
     */
    public void setCutsOnOff(Boolean cutsOnOff) {
        this.cutsOnOff = cutsOnOff;
    }

    /**
     * Sets or unset the feasibilityPump value.
     * 
     * @param strategy
     *            the new feasibilityPump value or null.
     */
    public void setFeasibilityPump(Boolean feasibilityPump) {
        this.feasibilityPump = feasibilityPump;
    }

    /**
     * Sets whether to use alternative Gomory cuts. This switches on an alternative Gomory cut generator (either at root
     * or in entire tree). This version is by Giacomo Nannicini and may be more robust. See branchAndCut for information
     * on options. Default off.
     * 
     * @param gmiCuts
     */
    public void setGmiCuts(GMICuts gmiCuts) {
        this.gmiCuts = gmiCuts;
    }

    /**
     * Switches most heuristics on or off. This can be used to switch on or off all heuristics. Then you can do
     * individual ones off or on. CbcTreeLocal is not included as it dramatically alters search.
     * 
     * @param heuristicsOnOff
     *            True to enable heuristics, false to disable heuristics, null to use default.
     */
    public void setHeuristicsOnOff(Boolean heuristicsOnOff) {
        this.heuristicsOnOff = heuristicsOnOff;
    }

    /**
     * Sets whether to use Lagrangean TwoMir cuts. This is a lagrangean relaxation for TwoMir cuts. See lagomoryCuts for
     * description of options. Default off.
     * 
     * @param latwomirCuts
     */
    public void setLatwomirCuts(LatwomirCuts latwomirCuts) {
        this.latwomirCuts = latwomirCuts;
    }

    /**
     * Sets or unset the logLevel value.
     * 
     * @param strategy
     *            the new logLevel value or null.
     */
    public void setLogLevel(Integer logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Sets whether to perturb problem. Perturbation helps to stop cycling, but Clp uses other measures for this.
     * However large problems and especially ones with unit elements and unit rhs or costs benefit from perturbation.
     * Normally Clp tries to be intelligent, but you can switch this off. The Clp library has this off by default.
     * Default on.
     * 
     * @param perturbation
     */
    public void setPerturbation(Boolean perturbation) {
        this.perturbation = perturbation;
    }

    /**
     * Sets whether to use integer preprocessing. This tries to reduce size of model in a similar way to presolve and it
     * also tries to strengthen the model - this can be very useful and is worth trying.
     * <ul>
     * <li><code>equal</code> will turn <= cliques into ==.</li>
     * <li><code>sos</code> will create sos sets if all 0-1 in sets (well one extra is allowed) and no overlaps.</li>
     * <li><code>trysos</code> is same but allows any number extra.</li>
     * <li><code>equalall</code> will turn all valid inequalities into equalities with integer slacks.</li>
     * <li><code>strategy</code> is as on but uses CbcStrategy.
     * </ul>
     * Default <code>sos</code>
     * 
     * @param strategy
     *            the new preprocess value or null.
     */
    public void setPreprocess(Preprocess preprocess) {
        this.preprocess = preprocess;
    }

    /**
     * Sets whether to presolve problem Presolve analyzes the model to find such things as redundant equations,
     * equations which fix some variables, equations which can be transformed into bounds etc etc. For the initial solve
     * of any problem this is worth doing unless you know that it will have no effect. <code>on</code> will normally do
     * 5 passes while using <code>more</code> will do 10. If the problem is very large you may need to write the
     * original to file using 'file'. Default on.
     * 
     * @param presolve
     */
    public void setPresolve(Presolve presolve) {
        this.presolve = presolve;
    }

    /**
     * Sets whether to use Probing cuts. This switches on probing cuts (either at root or in entire tree). See
     * branchAndCut for information on options. but strong options do more probing. Default on.
     * 
     * @param probingCuts
     */
    public void setProbingCuts(ProbingCuts probingCuts) {
        this.probingCuts = probingCuts;
    }

    /**
     * Sets whether to use Reduce-and-Split cuts. This switches on reduce and split cuts (either at root or in entire
     * tree). May be slow See branchAndCut for information on options. Default off.
     * 
     * @param reduceAndSplitCuts
     */
    public void setReduceAndSplit(ReduceAndSplitCuts reduceAndSplitCuts) {
        this.reduceAndSplitCuts = reduceAndSplitCuts;
    }

    /**
     * Sets or unset the sLogLevel value.
     * 
     * @param strategy
     *            the new sLogLevel value or null.
     */
    public void setSLogLevel(Integer sLogLevel) {
        this.sLogLevel = sLogLevel;
    }

    /**
     * Sets or unset the strategy value.
     * 
     * @param strategy
     *            the new strategy value or null.
     */
    public void setStrategy(Integer strategy) {
        this.strategy = strategy;
    }

    /**
     * Sets or unset the trustPseudocost value.
     * 
     * @param trustPseudocost
     *            the new trustPseudocost value or null.
     */
    public void setTrustPseudoCost(Integer trustPseudoCost) {
        this.trustPseudoCost = trustPseudoCost;
    }
}
