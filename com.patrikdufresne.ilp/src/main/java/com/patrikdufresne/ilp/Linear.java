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
package com.patrikdufresne.ilp;

/**
 * The interface is a linear expression consisting of variables and their
 * coefficients.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface Linear extends Iterable<Term> {

    /**
     * Add a new term to the linear expression.
     * 
     * @param term
     *            the term to be added
     */
    void add(Term term);

    /**
     * Remove a term from the linear expression.
     * 
     * @param term
     *            the term to remove
     */
    void remove(Term term);

    /**
     * Remove all terms from this linear expression.
     */
    void clear();

    /**
     * Returns the number of term in the linear expression.
     * 
     * @return the number of term
     */
    int size();

}
