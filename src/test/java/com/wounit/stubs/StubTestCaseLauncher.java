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
package com.wounit.stubs;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Runs a test class, usually a stub test case, through the JUnit Platform
 * launcher, in a launcher session of its own. The stub test cases that have
 * tests are disabled, so that nothing else runs them, and this launcher
 * deactivates the condition that skips them.
 */
public class StubTestCaseLauncher {
    /**
     * Run the tests of a test class.
     *
     * @param testClass
     *            the test class to run
     * @return the summary of the run
     */
    public static TestExecutionSummary launch(Class<?> testClass) {
	SummaryGeneratingListener listener = new SummaryGeneratingListener();

	LauncherFactory.create().execute(request().selectors(selectClass(testClass)).configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition").build(), listener);

	return listener.getSummary();
    }

    private StubTestCaseLauncher() {
    }
}
