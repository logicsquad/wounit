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

import static er.extensions.foundation.ERXArrayUtilities.arrayBySelectingInstancesOfClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.wounit.annotations.Dummy;
import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;

/**
 * Checks that a {@link MockEditingContext} fills the fields of
 * <code>&#064;Nested</code> test classes and of the classes that enclose
 * them, each from the nearest editing context.
 */
public class TestMockEditingContextNested {
    @RegisterExtension
    public final MockEditingContext editingContext = new MockEditingContext("Test");

    @UnderTest
    private FooEntity outerFoo;

    @Dummy
    private FooEntity outerDummy;

    @Test
    public void fillTheTestInstanceFields() {
	assertThat(outerFoo.editingContext(), is(editingContext));
	assertThat(outerDummy.editingContext(), is(editingContext));
    }

    @Nested
    public class NestedTest {
	@UnderTest
	private FooEntity nestedFoo;

	@Dummy
	private FooEntity nestedDummy;

	@Test
	public void fillTheEnclosingInstanceFields() {
	    assertThat(outerFoo.editingContext(), is(editingContext));
	    assertThat(outerDummy.editingContext(), is(editingContext));
	}

	@Test
	public void fillTheNestedInstanceFields() {
	    assertThat(nestedFoo.editingContext(), is(editingContext));
	    assertThat(nestedDummy.editingContext(), is(editingContext));
	}

	@Nested
	public class DeeplyNestedTest {
	    @UnderTest
	    private FooEntity deeplyNestedFoo;

	    @Test
	    public void fillTheFieldsOfEveryEnclosingInstance() {
		assertThat(outerFoo.editingContext(), is(editingContext));
		assertThat(nestedFoo.editingContext(), is(editingContext));
		assertThat(deeplyNestedFoo.editingContext(), is(editingContext));
	    }
	}
    }

    @Nested
    public class NestedTestWithItsOwnEditingContext {
	@RegisterExtension
	public final MockEditingContext nestedEditingContext = new MockEditingContext("Test");

	@UnderTest
	private FooEntity nestedFoo;

	@Dummy
	private FooEntity nestedDummy;

	@Test
	public void fillEachFieldOnce() {
	    assertThat(arrayBySelectingInstancesOfClass(editingContext.insertedObjects(), FooEntity.class), contains(outerFoo));
	    assertThat(arrayBySelectingInstancesOfClass(nestedEditingContext.insertedObjects(), FooEntity.class), contains(nestedFoo));
	}

	@Test
	public void fillEachInstanceFromItsOwnEditingContext() {
	    assertThat(outerFoo.editingContext(), is(editingContext));
	    assertThat(outerDummy.editingContext(), is(editingContext));
	    assertThat(nestedFoo.editingContext(), is(nestedEditingContext));
	    assertThat(nestedDummy.editingContext(), is(nestedEditingContext));
	}
    }
}
