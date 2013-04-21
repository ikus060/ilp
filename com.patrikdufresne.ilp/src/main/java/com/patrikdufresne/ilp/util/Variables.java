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

import com.patrikdufresne.ilp.Variable;

/**
 * Utility class for {@link Variable}.
 * 
 * @author Patrik Dufresne
 * 
 */
public class Variables {

    /**
     * Check if the variable bound is fixed.
     * 
     * @param var
     *            the variable
     * @return True if lower bound and the upper bound is fixed.
     */
    public static boolean isFixed(Variable var) {
        return var.getLowerBound() != null && var.getLowerBound().equals(var.getUpperBound());
    }

    /**
     * Private constructor for utility class.
     */
    private Variables() {

    }

}