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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.webobjects.eoaccess.EOModelGroup;
import com.wounit.annotations.UnderTest;
import com.wounit.model.AugmentedEntity;
import com.wounit.model.BaseEntity;

/**
 * Checks that a model loaded before the editing context, which WOUnit doesn't
 * unload, keeps its partials from one test to the next, while the editing
 * context loads another model again for each test.
 */
public class TestPartialEntitiesOfPreloadedModel {
    @BeforeAll
    public static void loadPartialsModel() {
	EOModelGroup.defaultGroup().addModelWithPathURL(TestPartialEntitiesOfPreloadedModel.class.getResource("/PartialsTest.eomodeld"));
    }

    @AfterAll
    public static void unloadPartialsModel() {
	EOModelGroup modelGroup = EOModelGroup.defaultGroup();

	modelGroup.removeModel(modelGroup.modelNamed("PartialsTest"));
    }

    @RegisterExtension
    public final MockEditingContext editingContext = new MockEditingContext("PartialsTest", "Test");

    @UnderTest
    private BaseEntity base;

    @RepeatedTest(3)
    public void createPartialForClassInEveryTest() {
	assertThat(base.partialForClass(AugmentedEntity.class), notNullValue());
    }
}
