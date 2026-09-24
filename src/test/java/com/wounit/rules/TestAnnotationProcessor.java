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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wounit.annotations.Dummy;
import com.wounit.exceptions.WOUnitException;
import com.wounit.model.FooEntity;
import com.wounit.stubs.ChildStubTestCase;
import com.wounit.stubs.DummyArrayStubTestCase;
import com.wounit.stubs.NotInitializedSpiedObjectStubTestCase;
import com.wounit.stubs.RawArrayDeclarationForDummyStubTestCase;
import com.wounit.stubs.StubTestCase;
import com.wounit.stubs.WrongGenericTypeForDummyStubTestCase;
import com.wounit.stubs.WrongGenericTypeForSpiedObjectStubTestCase;
import com.wounit.stubs.WrongTypeForDummyStubTestCase;
import com.wounit.stubs.WrongTypeForSpiedObjectStubTestCase;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
@ExtendWith(MockitoExtension.class)
public class TestAnnotationProcessor {
    @Mock
    private EditingContextFacade mockFacade;

    @Mock
    private FooEntity mockFoo;

    @RegisterExtension
    public final MockEditingContext editingContext = new MockEditingContext("Test");

    @Test
    public void createArrayOfDummiesBasedOnTheGenericType() throws Exception {
        DummyArrayStubTestCase mockTarget = new DummyArrayStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.arrayOfOneDummy().get(0), instanceOf(FooEntity.class));
    }

    @Test
    public void createArrayOfDummiesIfAnnotationAndSizePresent() throws Exception {
        DummyArrayStubTestCase mockTarget = new DummyArrayStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.arrayOfTwoDummies().size(), is(2));
    }

    @Test
    public void createArrayOfOneDummyIfAnnotationPresentButNoSize() throws Exception {
        DummyArrayStubTestCase mockTarget = new DummyArrayStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.arrayOfOneDummy(), notNullValue());
        assertThat(mockTarget.arrayOfOneDummy().size(), is(1));
    }

    @Test
    public void createObjectForInheritedFieldIfAnnotationPresent() throws Exception {
        ChildStubTestCase mockTarget = new ChildStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.foo(), is(mockFoo));
    }

    @Test
    public void createObjectIfAnnotationPresent() throws Exception {
        StubTestCase mockTarget = new StubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.foo(), is(mockFoo));
    }

    @Test
    public void createSpiedObjectIfFieldHasNotBeenInitializedByMockito() throws Exception {
        NotInitializedSpiedObjectStubTestCase mockTarget = new NotInitializedSpiedObjectStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.value(), notNullValue());

        boolean isSpy = Mockito.mockingDetails(mockTarget.value()).isSpy();

        assertTrue(isSpy);
    }

    @Test
    public void doNotCreateObjectIfAnnotationIsAbsent() throws Exception {
        StubTestCase mockTarget = new StubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        processor.process(Dummy.class, mockFacade);

        assertThat(mockTarget.objectUnderTest(), nullValue());
    }

    @Test
    public void exceptionIfAnnotatedGenericTypeIsIncompatible() throws Exception {
        WrongGenericTypeForDummyStubTestCase mockTarget = new WrongGenericTypeForDummyStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        WOUnitException exception = assertThrows(WOUnitException.class, () -> processor.process(Dummy.class, mockFacade));

        assertThat(exception.getMessage(), is("Cannot create object of type java.lang.String.\n Only fields and arrays of type com.webobjects.eocontrol.EOEnterpriseObject can be annotated with @Dummy."));
    }

    @Test
    public void exceptionIfAnnotatedTypeIsIncompatible() throws Exception {
        WrongTypeForDummyStubTestCase mockTarget = new WrongTypeForDummyStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        WOUnitException exception = assertThrows(WOUnitException.class, () -> processor.process(Dummy.class, mockFacade));

        assertThat(exception.getMessage(), is("Cannot create object of type java.lang.String.\n Only fields and arrays of type com.webobjects.eocontrol.EOEnterpriseObject can be annotated with @Dummy."));
    }

    @Test
    public void exceptionIfRawArrayAnnotated() throws Exception {
        RawArrayDeclarationForDummyStubTestCase mockTarget = new RawArrayDeclarationForDummyStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        WOUnitException exception = assertThrows(WOUnitException.class, () -> processor.process(Dummy.class, mockFacade));

        assertThat(exception.getMessage(), is("Cannot create object for a raw type com.webobjects.foundation.NSArray. Please, provide a generic type."));
    }

    @Test
    public void exceptionIfSpiedGenericTypeIsIncompatible() throws Exception {
        WrongGenericTypeForSpiedObjectStubTestCase mockTarget = new WrongGenericTypeForSpiedObjectStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        WOUnitException exception = assertThrows(WOUnitException.class, () -> processor.process(Dummy.class, mockFacade));

        assertThat(exception.getMessage(), is("Cannot create object of type java.lang.String.\n Only fields and arrays of type com.webobjects.eocontrol.EOEnterpriseObject can be annotated with @Dummy."));
    }

    @Test
    public void exceptionIfSpyingIncompatibleType() throws Exception {
        WrongTypeForSpiedObjectStubTestCase mockTarget = new WrongTypeForSpiedObjectStubTestCase();

        AnnotationProcessor processor = new AnnotationProcessor(mockTarget);

        WOUnitException exception = assertThrows(WOUnitException.class, () -> processor.process(Dummy.class, mockFacade));

        assertThat(exception.getMessage(), is("Cannot spy object of type java.lang.String.\n Only fields and arrays of type com.webobjects.eocontrol.EOEnterpriseObject can be annotated with @Spy + @Dummy."));
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        lenient().when(mockFacade.create(Mockito.any(Class.class))).thenReturn(mockFoo);
    }
}
