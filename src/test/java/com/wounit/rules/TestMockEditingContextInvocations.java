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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.wounit.annotations.Dummy;
import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;

/**
 * Checks that a {@link MockEditingContext} sets up each invocation of a
 * parameterized or repeated test afresh. Each invocation remembers its
 * editing context and objects, so that the next one can check it has new
 * ones.
 */
public class TestMockEditingContextInvocations {
    private static MockEditingContext previousEditingContext;

    private static FooEntity previousFoo;

    private static FooEntity previousDummy;

    @RegisterExtension
    public final MockEditingContext editingContext = new MockEditingContext("Test");

    @UnderTest
    private FooEntity foo;

    @Dummy
    private FooEntity dummy;

    private void checkInvocationIsSetUpAfresh(String value) {
	assertThat(foo, notNullValue());
	assertThat(dummy, notNullValue());
	assertThat(foo.editingContext(), is(editingContext));
	assertThat(dummy.editingContext(), is(editingContext));
	assertThat(editingContext.insertedObjects(), hasItem(foo));

	assertThat(editingContext, not(sameInstance(previousEditingContext)));
	assertThat(foo, not(sameInstance(previousFoo)));
	assertThat(dummy, not(sameInstance(previousDummy)));

	// Nothing that an earlier invocation wrote is visible
	assertThat(foo.bar(), nullValue());
	assertThat(dummy.bar(), nullValue());
	assertThat(FooEntity.fetchAllFooEntities(editingContext), containsInAnyOrder(foo, dummy));

	foo.setBar(value);
	dummy.setBar(value);

	previousEditingContext = editingContext;
	previousFoo = foo;
	previousDummy = dummy;
    }

    @ParameterizedTest
    @ValueSource(strings = { "first", "second", "third" })
    public void setUpEachParameterizedInvocationAfresh(String value) {
	checkInvocationIsSetUpAfresh(value);
    }

    @RepeatedTest(3)
    public void setUpEachRepetitionAfresh(RepetitionInfo repetitionInfo) {
	checkInvocationIsSetUpAfresh("repetition " + repetitionInfo.getCurrentRepetition());
    }
}
