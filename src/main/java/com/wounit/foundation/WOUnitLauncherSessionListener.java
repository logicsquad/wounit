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

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * The <code>WOUnitLauncherSessionListener</code> makes the project under test
 * a bundle, by setting the <code>NSProjectBundleEnabled</code> and
 * <code>NSBundleFactories</code> properties, as soon as the JUnit Platform
 * launcher opens a session. <code>NSBundle</code> reads them only once, when
 * it initialises, and a test class can initialise it before WOUnit creates an
 * editing context, for example by constructing a component. Set before any
 * test class is loaded, the properties are in place whatever order the test
 * classes run in.
 * <p>
 * The JUnit Platform finds this listener through
 * <code>META-INF/services</code>, so projects need neither register it nor set
 * the two properties themselves. The editing contexts still set them as well,
 * in case the launcher doesn't call this listener.
 * <p>
 * This class is not intended to be used outside of the context of testing.
 *
 * @since 2.1
 */
public class WOUnitLauncherSessionListener implements LauncherSessionListener {
    /**
     * Creates the listener. The JUnit Platform creates it through
     * <code>java.util.ServiceLoader</code>, which needs a public constructor.
     */
    public WOUnitLauncherSessionListener() {
    }

    /**
     * Set the <code>NSProjectBundleEnabled</code> and
     * <code>NSBundleFactories</code> properties. {@link WOUnitBundleFactory} is
     * named rather than loaded, so that no WebObjects class loads before the
     * tests do.
     */
    @Override
    public void launcherSessionOpened(LauncherSession session) {
	System.setProperty("NSProjectBundleEnabled", "true");
	System.setProperty("NSBundleFactories", "(com.wounit.foundation.WOUnitBundleFactory)");
    }
}
