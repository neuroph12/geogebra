/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math;

/**
 * Error thrown when a numerical computation can not be performed because the
 * numerical result failed to converge to a finite value.
 *
 * @version $Revision: 1.1 $ $Date: 2009-07-06 21:31:46 $
 */
public class ConvergenceException extends MathException {
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 4380655778005469702L;

    /**
     * Default constructor.
     */
    public ConvergenceException() {
        super("Convergence failed", new Object[0]);
    }
    
    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public ConvergenceException(String pattern, Object[] arguments) {
        super(pattern, arguments);
    }

    /**
     * Create an exception with a given root cause.
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public ConvergenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @param cause  the exception or error that caused this exception to be thrown
     * @since 1.2
     */
    public ConvergenceException(String pattern, Object[] arguments, Throwable cause) {
        super(pattern, arguments, cause);
    }
    
    /**
     * Constructs a new <code>ConvergenceException</code> with specified
     * detail message and nested <code>Throwable</code> root cause.
     *
     * @param msg  the error message.
     * @param rootCause  the exception or error that caused this exception
     * to be thrown.
     * @deprecated as of 1.2, replaced by 
     * {@link #ConvergenceException(String, Object[], Throwable)}
     */
    public ConvergenceException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
    
    /**
     * Constructs a new <code>ConvergenceException</code> with specified
     * detail message.
     *
     * @param msg  the error message.
     * @deprecated as of 1.2, replaced by 
     * {@link #ConvergenceException(String, Object[])}
     */
    public ConvergenceException(String msg) {
        super(msg);
    }

}
