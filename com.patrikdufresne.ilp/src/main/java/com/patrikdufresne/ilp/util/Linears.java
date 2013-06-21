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

import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.Term;

/**
 * Utility class to manipulate linears.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Linears {

    /**
     * Private constructor to avoid creating instances of utility class.
     */
    private Linears() {

    }

    /**
     * Compute the value of a linear using the variable value from a snapshot.
     * 
     * @param linear
     *            the linear to be computed
     * @param snapshot
     *            the value snapshot
     * @return the computed value.
     * @throws NullPointerException
     *             if the snapshot doesn't contains a variable from the linear.
     */
    public static Double compute(Linear linear, ValueSnapshot snapshot) {
        double value = 0;
        for (Term term : linear) {
            value += term.getCoefficient().doubleValue() * snapshot.get(term.getVariable()).doubleValue();
        }
        return Double.valueOf(value);
    }

}
