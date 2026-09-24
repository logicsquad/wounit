/**
 * Copyright (C) 2009 hprange <hprange@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
// Modifications copyright (C) 2026 Logic Squad.

package com.wounit.exceptions;

/**
 * Basic exception thrown if a problem occur in the internal logic of WOUnit.
 * 
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 * @since 1.1
 */
public class WOUnitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public WOUnitException(String message) {
	super(message);
    }

    /**
     * Creates an exception with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause of this exception
     */
    public WOUnitException(String message, Throwable cause) {
	super(message, cause);
    }
}
