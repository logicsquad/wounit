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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A test class that records the bundle properties when it's initialised, as
 * <code>NSBundle</code> would read them if the class initialised it.
 */
@Disabled("Run by TestWOUnitLauncherSessionListener")
public class BundlePropertiesStubTestCase {
    private static final String BUNDLE_FACTORIES = System.getProperty("NSBundleFactories");

    private static final String PROJECT_BUNDLE_ENABLED = System.getProperty("NSProjectBundleEnabled");

    public static String bundleFactoriesWhenInitialised() {
	return BUNDLE_FACTORIES;
    }

    public static String projectBundleEnabledWhenInitialised() {
	return PROJECT_BUNDLE_ENABLED;
    }

    @Test
    public void initialiseTheClass() {
    }
}
