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

import java.io.File;
import java.io.IOException;

/**
 * IPersistentLinearProblem is a linear problem that can be load from file and
 * save to file.
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IPersistentLinearProblem extends LinearProblem {

    /**
     * Load the linear problem from a file
     * 
     * @param file
     *            the file to load data from
     * @throws IOException
     */
    public void load(File file) throws IOException;

    /**
     * Save the linear problem to a file
     * 
     * @param file
     *            the file
     * @throws IOException
     */
    public void save(File file) throws IOException;

}
