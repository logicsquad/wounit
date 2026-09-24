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
package com.wounit.annotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Spy;

import com.wounit.model.FooEntity;
import com.wounit.rules.TemporaryEditingContext;

/**
 * Checks a spied object under test with Mockito's default mock maker. The
 * subclasses cover a spy created by Mockito and one created by WOUnit.
 */
public abstract class AbstractSpyTest {
    @RegisterExtension
    public final TemporaryEditingContext editingContext = new TemporaryEditingContext("Test");

    @Spy
    @UnderTest
    private FooEntity spiedFoo;

    @Test
    public void saveSpiedObject() {
	spiedFoo.setBar("saved");

	editingContext.saveChanges();

	assertThat(editingContext.globalIDForObject(spiedFoo).isTemporary(), is(false));
    }

    @Test
    public void spyKeepsEntityClass() {
	assertThat(mockingDetails(spiedFoo).isSpy(), is(true));
	assertThat(spiedFoo.getClass().getName(), is(FooEntity.class.getName()));
    }

    @Test
    public void stubSpiedObject() {
	doReturn("stubbed").when(spiedFoo).bar();

	assertThat(spiedFoo.bar(), is("stubbed"));
    }

    @Test
    public void verifySpiedObject() {
	spiedFoo.setBar("value");

	verify(spiedFoo).setBar("value");
	assertThat(spiedFoo.bar(), is("value"));
    }
}
