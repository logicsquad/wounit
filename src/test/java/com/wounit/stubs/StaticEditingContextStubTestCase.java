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
import org.junit.jupiter.api.extension.RegisterExtension;

import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;
import com.wounit.rules.MockEditingContext;

/**
 * A test class that registers its editing context in a static field, which
 * WOUnit has to fail before the first test.
 */
@Disabled("Run by TestSharedEditingContext")
public class StaticEditingContextStubTestCase {
    @RegisterExtension
    public static final MockEditingContext editingContext = new MockEditingContext("Test");

    @UnderTest
    private FooEntity foo;

    @Test
    public void firstTest() {
	foo.setBar("first");
    }

    @Test
    public void secondTest() {
	foo.setBar("second");
    }
}
