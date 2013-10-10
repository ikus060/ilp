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

    private static final String COMBINESOLUTIONS = "-combineSolutions";
    private static final String CUTSONOFF = "-cutsonoff";
    private static final String FEASIBILITYPUMP = "-feasibilityPump";
    private static final String LOGLEVEL = "-logLevel";
    private static final String OFF = "off";
    private static final String ON = "on";
    private static final String PREPROCESS = "-preprocess";
    private static final String PROBNAME = "problem-name";
    private static final String SLOGLEVEL = "-slogLevel";
    private static final String SOLVE = "-solve";
    private static final String STRATEGY = "-strategy";
    private static final String TRUSTPSEUDOCOST = "-trustPseudoCosts";

    private Boolean combineSolutions; // Base at on
    private Boolean cutsOnOff; // Base at on
    private Boolean feasibilityPump = false; // Base at on
    private Integer logLevel = 0; // Base at 1
    private Boolean preprocess = false; // Base at sos
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
            args.addAll(Arrays.asList(PREPROCESS, this.preprocess ? ON : OFF));
        }
        if (this.feasibilityPump != null) {
            args.addAll(Arrays.asList(FEASIBILITYPUMP, this.feasibilityPump ? ON : OFF));
        }
        if (this.cutsOnOff != null) {
            args.addAll(Arrays.asList(CUTSONOFF, this.cutsOnOff ? ON : OFF));
        }
        if (this.combineSolutions != null) {
            args.addAll(Arrays.asList(COMBINESOLUTIONS, this.combineSolutions ? ON : OFF));
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
            args.addAll(Arrays.asList(TRUSTPSEUDOCOST, Integer.toString(this.trustPseudoCost)));
        }
        args.add(SOLVE);
        return args;
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
     * Return the feasibilityPump value or null if undefined. Base value at on.
     * 
     * @return feasibilityPump value or null
     */
    public Boolean getFeasibilityPump() {
        return this.feasibilityPump;
    }

    /**
     * Return the logLevel value or null if undefined. Base value at 1.
     * 
     * @return logLevel value or null
     */
    public Integer getLogLevel() {
        return this.logLevel;
    }

    /**
     * Return the preprocess value or null if undefined. Base value at sos.
     * 
     * @return preprocess value or null
     */
    public Boolean getPreprocess() {
        return this.preprocess;
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
     * Sets or unset the logLevel value.
     * 
     * @param strategy
     *            the new logLevel value or null.
     */
    public void setLogLevel(Integer logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Sets or unset the preprocess value.
     * 
     * @param strategy
     *            the new preprocess value or null.
     */
    public void setPreprocess(Boolean preprocess) {
        this.preprocess = preprocess;
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
