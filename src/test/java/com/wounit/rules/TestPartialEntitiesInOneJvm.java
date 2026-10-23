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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.wounit.stubs.StubTestCaseLauncher;

/**
 * Checks that {@link TestPartialEntities} and
 * {@link TestPartialEntitiesDisabled}, which load the same model with partials
 * on and off, pass one after the other in one JVM, in either order.
 */
public class TestPartialEntitiesInOneJvm {
    private static void checkTestClassPasses(Class<?> testClass) {
	TestExecutionSummary summary = StubTestCaseLauncher.launch(testClass);

	assertThat(testClass.getSimpleName(), summary.getFailures().stream().map(failure -> failure.getTestIdentifier().getDisplayName() + ": " + failure.getException()).toList(), empty());
	assertThat(testClass.getSimpleName(), summary.getTestsSucceededCount(), greaterThan(0L));
    }

    @Test
    public void runTheClassWithPartialsDisabledAfterTheOther() {
	checkTestClassPasses(TestPartialEntities.class);
	checkTestClassPasses(TestPartialEntitiesDisabled.class);
    }

    @Test
    public void runTheClassWithPartialsEnabledAfterTheOther() {
	checkTestClassPasses(TestPartialEntitiesDisabled.class);
	checkTestClassPasses(TestPartialEntities.class);
    }
}
