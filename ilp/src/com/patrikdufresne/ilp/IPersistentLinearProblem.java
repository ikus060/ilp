/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
