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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOEntityClassDescription;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.foundation.NSValidation;
import com.wounit.annotations.UnderTest;
import com.wounit.model.FooEntity;

/**
 * Checks that the enterprise objects of a model that the editing context loads
 * again for each test describe the entities of the current load. The tests run
 * in order: the first remembers the entity of its load, and changes it.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractReloadedModelTest<T extends AbstractEditingContextRule> {
    private static EOEntity entityOfTheFirstTest;

    private static EOEntity fooEntity() {
	return EOModelGroup.defaultGroup().entityNamed(FooEntity.ENTITY_NAME);
    }

    @RegisterExtension
    public final T editingContext = createEditingContext("Test");

    @UnderTest
    private FooEntity foo;

    protected abstract T createEditingContext(String... modelNames);

    private EOEntity entityOf(FooEntity object) {
	return ((EOEntityClassDescription) object.classDescription()).entity();
    }

    @Test
    @Order(1)
    public void describeTheEntityOfTheCurrentLoad() {
	entityOfTheFirstTest = fooEntity();

	assertThat(entityOf(foo), sameInstance(fooEntity()));

	fooEntity().attributeNamed(FooEntity.BAR_KEY).setAllowsNull(false);

	assertThrows(NSValidation.ValidationException.class, () -> foo.validateForSave());
    }

    @Test
    @Order(2)
    public void describeTheEntityOfANewLoad() {
	// The editing context unloaded the model after the first test, and has loaded it again
	assertThat(fooEntity(), not(sameInstance(entityOfTheFirstTest)));

	assertThat(entityOf(foo), sameInstance(fooEntity()));

	// The first test's change to the model went with its load
	foo.validateForSave();
    }

    @Test
    @Order(3)
    public void validateAgainstTheEntityOfANewLoad() {
	assertThat(entityOf(foo), sameInstance(fooEntity()));

	fooEntity().attributeNamed(FooEntity.BAR_KEY).setAllowsNull(false);

	assertThrows(NSValidation.ValidationException.class, () -> foo.validateForSave());
    }
}
