/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.ilp;

/**
 * This error is thrown whenever an unrecoverable error occurs internally. The
 * message text provide a further description of the problem. The exception has
 * a <code>throwable</code> field which holds the underlying throwable that
 * caused the problem (if this information is available (i.e. it may be null)).
 * 
 * @see IPLException
 */
public class ILPError extends Error {

	public ILPError(String message, Throwable cause) {
		super(message, cause);
	}

}