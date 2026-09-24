/**
 * Copyright (C) 2009 hprange <hprange@gmail.com>
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
// Modifications copyright (C) 2026 Logic Squad.
package com.wounit.rules;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.wounit.annotations.UnderTest;
import com.wounit.model.AugmentedEntity;
import com.wounit.model.BaseEntity;

import er.extensions.foundation.ERXProperties;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class TestPartialEntitiesDisabled {
    @BeforeAll
    public static void disablePartials() {
        ERXProperties.setStringForKey("false", "er.extensions.partials.enabled");
    }

    @AfterAll
    public static void removePartialsConfiguration() {
        ERXProperties.removeKey("er.extensions.partials.enabled");
    }

    @UnderTest
    protected BaseEntity base;

    @RegisterExtension
    public MockEditingContext editingContext = new MockEditingContext("PartialsTest");

    @Test
    public void disablePartialsAccordingToProperties() throws Exception {
        try {
            base.partialForClass(AugmentedEntity.class);

            fail("Must throw an exception when trying to use partials");
        } catch (Exception exception) {
            assertThat(exception.getMessage(), containsString("no partial"));
        }
    }
}
