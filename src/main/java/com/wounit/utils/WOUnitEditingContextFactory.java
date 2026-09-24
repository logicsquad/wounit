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
package com.wounit.utils;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStore;
import com.wounit.rules.MockEditingContext;
import com.wounit.rules.TemporaryEditingContext;

import er.extensions.eof.ERXEC;

/**
 * The <code>WOUnitEditingContextFactory</code> is a helper class created to
 * make testing easier. In some scenarios, programmers may not have access to
 * the creation of editing contexts. This can happen when the code under test
 * uses one of the <code>ERXEC.newEditingContext</code> factory methods. This
 * class extends the <code>ERXEC.DefaultFactory</code> class. It returns the
 * provided editing context for every call to
 * <code>ERXEC.newEditingContext</code> factory methods.
 * <p>
 * The {@link MockEditingContext} and the {@link TemporaryEditingContext}
 * install a <code>WOUnitEditingContextFactory</code> for themselves before each
 * test, and put back Wonder's default factory after it. Any code under test
 * that calls one of the <code>ERXEC.newEditingContext</code> factory methods
 * receives the test's editing context as a result.
 * <p>
 * To hand out another editing context instead, install a factory for it in a
 * <code>&#064;BeforeEach</code> method, which JUnit runs after the extension
 * has set up:
 * 
 * <pre>
 * &#064;BeforeEach
 * public void setup() {
 *     ERXEC.setFactory(new WOUnitEditingContextFactory(anotherEditingContext));
 * }
 * </pre>
 * 
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 * @since 1.3
 * @see ERXEC.Factory
 */
public class WOUnitEditingContextFactory extends ERXEC.DefaultFactory {
    private final EOEditingContext editingContext;

    /**
     * Create an editing context factory that always returns the provided
     * editing context as a result.
     * 
     * @param editingContext
     *            An editing context
     */
    public WOUnitEditingContextFactory(EOEditingContext editingContext) {
	super();

	this.editingContext = editingContext;
    }

    @Override
    protected EOEditingContext _createEditingContext(EOObjectStore parent) {
	return editingContext;
    }
}
