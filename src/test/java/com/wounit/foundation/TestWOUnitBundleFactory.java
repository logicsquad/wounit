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
package com.wounit.foundation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.development.NSLegacyBundle;
import com.wounit.foundation.WOUnitBundleFactory;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
@ExtendWith(MockitoExtension.class)
public class TestWOUnitBundleFactory {
    private WOUnitBundleFactory factory;

    @Mock
    private NSBundle mockBundle;

    @Mock
    private NSLegacyBundle.Factory mockLegacyBundleFactory;

    @Test
    public void delegateCreationToLegacyFactoryWhenPathContainsFramework() throws Exception {
	NSBundle result = factory.bundleForPath("prefix.frameworksuffix", true, false);

	assertThat(result, is(mockBundle));

	verify(mockLegacyBundleFactory).bundleForPath("prefix.frameworksuffix", true, false);
    }

    @Test
    public void delegateCreationToLegacyFactoryWhenPathContainsWoa() throws Exception {
	NSBundle result = factory.bundleForPath("prefix.woasuffix", true, false);

	assertThat(result, is(mockBundle));

	verify(mockLegacyBundleFactory).bundleForPath("prefix.woasuffix", true, false);
    }

    @Test
    public void returnNullBundleIfPathDoesNotContainFrameworkNorWoa() throws Exception {
	NSBundle result = factory.bundleForPath("prefixXXXsuffix", true, false);

	assertThat(result, nullValue());
    }

    @BeforeEach
    public void setup() {
	lenient().when(mockLegacyBundleFactory.bundleForPath(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(mockBundle);

	factory = new WOUnitBundleFactory(mockLegacyBundleFactory);
    }
}
