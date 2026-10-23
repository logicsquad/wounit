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
package com.wounit.foundation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.wounit.stubs.BundlePropertiesStubTestCase;
import com.wounit.stubs.StubTestCaseLauncher;

/**
 * Checks that the {@link WOUnitLauncherSessionListener} sets the bundle
 * properties, and that the JUnit Platform calls it before it initialises a
 * test class.
 */
public class TestWOUnitLauncherSessionListener {
    private static final String BUNDLE_FACTORIES = "(" + WOUnitBundleFactory.class.getName() + ")";

    private String bundleFactoriesToRestore;

    private String projectBundleEnabledToRestore;

    @BeforeEach
    public void clearBundleProperties() {
	bundleFactoriesToRestore = System.clearProperty("NSBundleFactories");
	projectBundleEnabledToRestore = System.clearProperty("NSProjectBundleEnabled");
    }

    @AfterEach
    public void restoreBundleProperties() {
	restoreProperty("NSBundleFactories", bundleFactoriesToRestore);
	restoreProperty("NSProjectBundleEnabled", projectBundleEnabledToRestore);
    }

    private void restoreProperty(String key, String value) {
	if (value == null) {
	    System.clearProperty(key);
	} else {
	    System.setProperty(key, value);
	}
    }

    @Test
    public void setBundlePropertiesBeforeTheTestClassesAreInitialised() {
	TestExecutionSummary summary = StubTestCaseLauncher.launch(BundlePropertiesStubTestCase.class);

	assertThat(summary.getTestsSucceededCount(), is(1L));
	assertThat(BundlePropertiesStubTestCase.bundleFactoriesWhenInitialised(), is(BUNDLE_FACTORIES));
	assertThat(BundlePropertiesStubTestCase.projectBundleEnabledWhenInitialised(), is("true"));
    }

    @Test
    public void setBundlePropertiesWhenTheSessionOpens() {
	new WOUnitLauncherSessionListener().launcherSessionOpened(mock(LauncherSession.class));

	assertThat(System.getProperty("NSBundleFactories"), is(BUNDLE_FACTORIES));
	assertThat(System.getProperty("NSProjectBundleEnabled"), is("true"));
    }
}
