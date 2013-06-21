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
