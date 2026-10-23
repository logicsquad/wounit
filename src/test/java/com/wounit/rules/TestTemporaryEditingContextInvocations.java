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
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;

/**
 * Checks that a {@link TemporaryEditingContext} sets up each invocation of a
 * parameterized or repeated test afresh. Each invocation saves its object
 * and remembers its editing context and object, so that the next one can
 * check it has new ones, and that the saved data is gone.
 */
public class TestTemporaryEditingContextInvocations {
    private static TemporaryEditingContext previousEditingContext;

    private static FooEntity previousFoo;

    @RegisterExtension
    public final TemporaryEditingContext editingContext = new TemporaryEditingContext("Test");

    @UnderTest
    private FooEntity foo;

    private void checkInvocationIsSetUpAfresh(String value) {
	assertThat(foo, notNullValue());
	assertThat(foo.editingContext(), is(editingContext));
	assertThat(editingContext.insertedObjects(), hasItem(foo));

	assertThat(editingContext, not(sameInstance(previousEditingContext)));
	assertThat(foo, not(sameInstance(previousFoo)));

	// What an earlier invocation saved has been thrown away
	assertThat(FooEntity.fetchAllFooEntities(editingContext), is(empty()));

	foo.setBar(value);
	// FooEntity's restricting qualifier only fetches type 1
	foo.setType(1);

	editingContext.saveChanges();

	assertThat(FooEntity.fetchAllFooEntities(editingContext), contains(foo));

	previousEditingContext = editingContext;
	previousFoo = foo;
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
