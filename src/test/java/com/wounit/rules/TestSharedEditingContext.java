/**
 * Copyright (C) 2026 Logic Squad
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
package com.wounit.rules;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.wounit.exceptions.WOUnitException;
import com.wounit.stubs.PerClassEditingContextStubTestCase;
import com.wounit.stubs.StaticEditingContextStubTestCase;
import com.wounit.stubs.StubTestCaseLauncher;

/**
 * Checks that an editing context that all the tests of a class would share
 * fails the class before its first test, and unloads the models it loaded.
 */
public class TestSharedEditingContext {
    private static void checkClassFailsBeforeItsFirstTest(Class<?> testClass, String editingContextName) {
	TestExecutionSummary summary = StubTestCaseLauncher.launch(testClass);

	assertThat(summary.getTestsStartedCount(), is(0L));
	assertThat(summary.getFailures().size(), is(1));

	Throwable exception = summary.getFailures().get(0).getException();

	assertThat(exception, instanceOf(WOUnitException.class));
	assertThat(exception.getMessage(), is("Each test needs an editing context of its own, but this " + editingContextName + " would be shared by all the tests in " + testClass.getName() + ". Register it in an instance field rather than a static one, and keep the default per-method test instance lifecycle."));
	assertThat(exception.getSuppressed().length, is(0));

	assertThat(EOModelGroup.defaultGroup().modelNamed("Test"), nullValue());
    }

    @Test
    public void failClassWithPerClassLifecycleBeforeItsFirstTest() {
	checkClassFailsBeforeItsFirstTest(PerClassEditingContextStubTestCase.class, TemporaryEditingContext.class.getSimpleName());
    }

    @Test
    public void failClassWithStaticEditingContextBeforeItsFirstTest() {
	checkClassFailsBeforeItsFirstTest(StaticEditingContextStubTestCase.class, MockEditingContext.class.getSimpleName());
    }

    @BeforeEach
    public void unloadTestModel() {
	// So that the stub's editing context loads the model, and has to unload it
	EOModelGroup modelGroup = EOModelGroup.defaultGroup();

	EOModel model = modelGroup.modelNamed("Test");

	if (model != null) {
	    modelGroup.removeModel(model);
	}
    }
}
