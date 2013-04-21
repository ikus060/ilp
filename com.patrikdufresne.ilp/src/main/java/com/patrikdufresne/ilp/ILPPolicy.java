/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * The policy class handles settings for behavior, debug flags and logging
 * within ILP.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ILPPolicy {

	private static ILPLogger log;

	/**
	 * Private constructor for utility class.
	 */
	private ILPPolicy() {
		// Nothing to do
	}

	/**
	 * Returns the logger used by ILP to log messages.
	 * <p>
	 * The default logger prints the status to <code>System.err</code>.
	 * </p>
	 * 
	 * @return the logger
	 */
	public static ILPLogger getLog() {
		if (ILPPolicy.log == null) {
			ILPPolicy.log = getDummyLog();
		}
		return ILPPolicy.log;
	}

	private static ILPLogger getDummyLog() {
		return new ILPLogger() {
			@Override
			public void log(int severity, String message) {
				System.out.println(message);
			}

			@Override
			public int getLevel() {
				return ILPLogger.ERROR;
			}
		};
	}

	/**
	 * Sets the logger used by ILP to log messages.
	 * 
	 * @param logger
	 *            the logger to use, or <code>null</code> to use the default
	 *            logger
	 */

	public static void setLog(ILPLogger logger) {
		ILPPolicy.log = logger;
	}

	/**
	 * Same as calling getLog().log(severity, message).
	 * 
	 * @param debug
	 * @param message
	 */
	public static void log(int severity, String message) {
		if (ILPPolicy.log == null) {
			getLog().log(severity, message);
		} else {
			ILPPolicy.log.log(severity, message);
		}
	}

}
