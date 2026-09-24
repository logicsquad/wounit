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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockingDetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;

import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * Checks a {@link TemporaryEditingContext} registered as a JUnit Jupiter
 * extension.
 */
@ExtendWith(MockitoExtension.class)
public class TestTemporaryEditingContextExtension {
    @RegisterExtension
    public final TemporaryEditingContext editingContext = new TemporaryEditingContext("Test");

    @UnderTest
    private FooEntity foo;

    @Spy
    @UnderTest
    private FooEntity spiedFoo;

    @Test
    public void createObjectsUnderTestInTheEditingContext() {
	assertThat(foo, notNullValue());
	assertThat(foo.editingContext(), is(editingContext));
    }

    @Test
    public void insertSpiedObjectsUnderTest() {
	assertThat(mockingDetails(spiedFoo).isSpy(), is(true));
	assertThat(ERXArrayUtilities.arrayBySelectingInstancesOfClass(editingContext.insertedObjects(), FooEntity.class), hasItem(spiedFoo));
    }

    @Test
    public void provideItselfAsTheNewEditingContext() {
	assertThat(ERXEC.newEditingContext(), is(editingContext));
    }
}
