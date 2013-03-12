/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * A mechanism to log errors throughout ILP.
 * <p>
 * Clients may provide their own implementation to change how errors are logged
 * from within ILP.
 * </p>
 * 
 * @see ILPPolicy#getLog()
 * @see ILPPolicy#setLog(ILogger)
 */
public interface ILPLogger {

	public static final int DEBUG = 0x01;

	public static final int INFO = 0x02;

	public static final int WARNING = 0x03;

	public static final int ERROR = 0x04;

	/**
	 * Logs the given status.
	 * 
	 * @param status
	 *            the status to log
	 */
	public void log(int severity, String message);

	/**
	 * Return the current log level.
	 * 
	 * @return One of the DEBUG, INFO, WARNING, ERROR constants.
	 */
	public int getLevel();

}
