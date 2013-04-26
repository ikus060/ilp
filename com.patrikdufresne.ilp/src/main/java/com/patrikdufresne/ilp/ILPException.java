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
 * This runtime exception is thrown whenever a recoverable error occurs internally in SWT. The message text and error
 * code provide a further description of the problem. The exception has a <code>throwable</code> field which holds the
 * underlying exception that caused the problem (if this information is available (i.e. it may be null)).
 * <p>
 * SWTExceptions are thrown when something fails internally, but SWT is left in a known stable state (eg. a widget call
 * was made from a non-u/i thread, or there is failure while reading an Image because the source file was corrupt).
 * </p>
 * 
 * @see SWTError
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 */
public class ILPException extends RuntimeException {

    /**
     * ILP error constant indicating that no error number was specified (value is 1).
     */
    public static final int ERROR_UNSPECIFIED = 1;

    /**
     * ILP error constant indicating that no more handles are available (value is 2).
     */
    public static final int ERROR_NO_HANDLES = 2;

    /**
     * ILP error constant indicating that an attempt was made to invoke an object which had already been disposed (value
     * is 3).
     */
    public static final int ERROR_RESOURCE_DISPOSED = 3;

    /**
     * ILP error constant throws when trying to create a variable or a constraint with a name that already exists.
     */
    public static final int ERROR_DUPLICATE_NAME = 4;

    public int code;

    /**
     * Constructs a new instance of this class with its stack trace filled in. The error code is set to an unspecified
     * value.
     */
    public ILPException() {
        this(ILPException.ERROR_UNSPECIFIED);
    }

    /**
     * Constructs a new instance of this class with its stack trace and message filled in. The error code is set to an
     * unspecified value. Specifying <code>null</code> as the message is equivalent to specifying an empty string.
     * 
     * @param message
     *            the detail message for the exception
     */
    public ILPException(String message) {
        this(ILPException.ERROR_UNSPECIFIED, message);
    }

    /**
     * Constructs a new instance of this class with its stack trace and error code filled in.
     * 
     * @param code
     *            the ILP error code
     */
    public ILPException(int code) {
        this(code, ILPException.findErrorText(code));
    }

    /**
     * Constructs a new instance of this class with its stack trace, error code and message filled in. Specifying
     * <code>null</code> as the message is equivalent to specifying an empty string.
     * 
     * @param code
     *            the ILP error code
     * @param message
     *            the detail message for the exception
     */
    public ILPException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Answers a concise, human readable description of the error code.
     * 
     * @param code
     *            the SWT error code.
     * @return a description of the error code.
     * 
     * @see SWT
     */
    private static String findErrorText(int code) {
        switch (code) {
        case ERROR_UNSPECIFIED:
            return "Unspecified error"; //$NON-NLS-1$
        case ERROR_NO_HANDLES:
            return "No more handles"; //$NON-NLS-1$
        case ERROR_RESOURCE_DISPOSED:
            return "Resource is disposed"; //$NON-NLS-1$
        }
        return "Unknown error"; //$NON-NLS-1$
    }

}
