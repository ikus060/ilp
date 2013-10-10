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
package com.patrikdufresne.ilp.util;

import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.Status;
import com.patrikdufresne.ilp.Variable;

/**
 * Utility class for LinearProblem.
 * 
 * @author Patrik Dufresne
 * 
 */
public class LinearProblems {

    /**
     * Generate a string representation of the problem to be human readable.
     * 
     * @param lp
     * @return
     */
    public static String toString(LinearProblem lp) {
        StringBuilder buf = new StringBuilder();

        // Print the solution
        Status status = lp.getStatus();
        boolean solutionAvailable = status.equals(Status.OPTIMAL) || status.equals(Status.FEASIBLE);
        // Print objective
        if (solutionAvailable) {
            buf.append(lp.getObjectiveLinear());
            buf.append("\r\n");
            buf.append(lp.getObjectiveName());
            buf.append("="); //$NON-NLS-1$
            buf.append(lp.getObjectiveValue());
            buf.append("\r\n"); //$NON-NLS-1$
        } else {

        }
        // Print variables
        for (Variable var : lp.getVariables()) {
            buf.append(var.getName());
            if (solutionAvailable) {
                buf.append("="); //$NON-NLS-1$
                buf.append(var.getValue());
            } else {
                buf.append(":{"); //$NON-NLS-1$
                buf.append(var.getLowerBound());
                buf.append(".."); //$NON-NLS-1$
                buf.append(var.getUpperBound());
                buf.append("}"); //$NON-NLS-1$
            }
            buf.append("\r\n"); //$NON-NLS-1$
        }
        // Print constraints.
        for (Constraint constraint : lp.getConstraints()) {
            buf.append(constraint.getLinear());
            buf.append("Constraint["); //$NON-NLS-1$
            buf.append(constraint.getName());
            buf.append("]"); //$NON-NLS-1$
            buf.append(":{"); //$NON-NLS-1$
            buf.append(constraint.getLowerBound());
            buf.append(".."); //$NON-NLS-1$
            buf.append(constraint.getUpperBound());
            buf.append("}"); //$NON-NLS-1$
            buf.append("\r\n"); //$NON-NLS-1$
        }
        return buf.toString();
    }

}
