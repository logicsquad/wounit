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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOObjectStore;
import com.wounit.annotations.UnderTest;
import com.wounit.exceptions.WOUnitException;
import com.wounit.foundation.WOUnitBundleFactory;
import com.wounit.utils.WOUnitEditingContextFactory;

import er.extensions.ERXExtensions;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEntityClassDescription;
import er.extensions.foundation.ERXArrayUtilities;
import er.extensions.foundation.ERXProperties;
import er.extensions.partials.ERXPartialInitializer;

/**
 * <code>AbstractEditingContextRule</code> is a subclass of <code>ERXEC</code>
 * that implements JUnit Jupiter's {@link BeforeEachCallback} and
 * {@link AfterEachCallback} interfaces. This class provides the required
 * infrastructure to properly initialize/dispose the <code>ERXEC</code>
 * before/after the test execution.
 * <p>
 * Each test needs an editing context of its own, so the editing context must
 * be registered in an instance field of a test class with the default
 * per-method test instance lifecycle. JUnit calls the
 * {@link BeforeAllCallback} and {@link AfterAllCallback} methods only on an
 * editing context that all the tests of a class would share, which fails the
 * class before its first test.
 * 
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 * @since 1.0
 */
public abstract class AbstractEditingContextRule extends ERXEC implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    // Lazy initialization of singleton instance of ERXExtensions
    private static class SINGLETONS {
        static ERXExtensions exrExtensions = new ERXExtensions();
    }

    /**
     * This facade creates enterprise objects and inserts them into the
     * specified editing context.
     */
    static class UnderTestFacade extends EditingContextFacade {
        public UnderTestFacade(EOEditingContext editingContext) {
            super(editingContext);
        }

        @Override
        public EOEnterpriseObject create(Class<? extends EOEnterpriseObject> type) {
            EOEntity entity = EOUtilities.entityForClass(editingContext, type);

            return EOUtilities.createAndInsertInstance(editingContext, entity.name());
        }

        @Override
        public void insert(EOEnterpriseObject object) {
            editingContext.insertObject(object);
        }
    }

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEditingContextRule.class);

    /**
     * Collection of models to unload after the test execution.
     */
    private final Collection<String> modelToUnload = new ArrayList<String>();

    /**
     * A processor for fields with special annotations.
     */
    protected AnnotationProcessor processor;

    /**
     * Constructor only for test purposes.
     */
    AbstractEditingContextRule(EOObjectStore objectStore, String... modelNames) {
        super(objectStore);

        if (!ERXProperties.hasKey("er.extensions.partials.enabled")) {
            ERXProperties.setStringForKey("true", "er.extensions.partials.enabled");
        }

        ERXProperties.setStringForKey("true", "NSProjectBundleEnabled");
        ERXProperties.setStringForKey("(" + WOUnitBundleFactory.class.getName() + ")", "NSBundleFactories");

        // Simulate what ERExtensions does
        EOModelGroup.setClassDelegate(SINGLETONS.exrExtensions);
        ERXEntityClassDescription.registerDescription();
        ERXArrayUtilities.initialize();

        for (String modelName : modelNames) {
            loadModel(modelName);
        }

        boolean isPartialsEnabled = ERXProperties.booleanForKey("er.extensions.partials.enabled");

        if (isPartialsEnabled) {
            ERXPartialInitializer.initializer().initializePartialEntities(EOModelGroup.defaultGroup());
        }
    }

    /**
     * Create a new instance of this editing context loading the models
     * specified by parameter.
     *
     * @param modelNames
     *            the name of the models to be loaded before the test execution.
     */
    public AbstractEditingContextRule(String... modelNames) {
        this(defaultParentObjectStore(), modelNames);
    }

    /**
     * Reset all changes made into this editing context and unload all models
     * loaded by this class at the beginning of the test execution.
     */
    protected void after() {
        unlock();

        try {
            disposeImpl();
        } catch (Exception exception) {
            LOG.warn("An exception has been thrown while disposing the {} after the test execution.", getClass().getSimpleName(), exception);
        }

        EOModelGroup modelGroup = EOModelGroup.defaultGroup();

        for (String modelName : modelToUnload) {
            EOModel model = modelGroup.modelNamed(modelName);

            modelGroup.removeModel(model);
        }

        ERXEC.setFactory(null);
    }

    @Override
    public void dispose() {
        // We're not invoking the super implementation on purpose.
        // This method is called to prepare the editing context for destruction, making it unusable as a result.
        // Thus, one can expect unpredictable outcomes if the code under test calls this method,
        // including the inability to clean up properly after the test execution.
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().matches("(com.wounit|org.mockito|java.lang).*")) {
                LOG.warn("Ignoring call to EOEditingContext.dispose method by the code under test at {}", element);

                break;
            }
        }
    }

    /**
     * An alternative method used by this editing context to invoke the real {@code EOEditingContext#dispose}
     * implementation after running tests.
     */
    protected void disposeImpl() {
        super.dispose();
    }

    /**
     * Fail the test class before its first test, because all its tests would
     * share this editing context. JUnit calls this method only when the
     * editing context is registered in a <code>static</code> field, or the
     * test class has a per-class test instance lifecycle.
     *
     * @throws WOUnitException
     *             always, saying how to register the editing context instead
     * @since 2.1
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        throw new WOUnitException(String.format("Each test needs an editing context of its own, but this %s would be shared by all the tests in %s. Register it in an instance field rather than a static one, and keep the default per-method test instance lifecycle.", getClass().getSimpleName(), context.getRequiredTestClass().getName()));
    }

    /**
     * Create the annotation processor for the test instances and set up this
     * editing context before each JUnit Jupiter test.
     * <p>
     * In a <code>&#064;Nested</code> test, this editing context fills the
     * fields of the instance that registers it and of the instances nested in
     * that one, up to one that registers an editing context of its own.
     *
     * @see #before()
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        processor = new AnnotationProcessor(instancesToFill(context.getRequiredTestInstances().getAllInstances()));

        before();
    }

    /**
     * Select the test instances whose fields this editing context fills: the
     * one that registers it, and those nested in that one, up to one that
     * registers an editing context of its own. If no instance registers this
     * editing context, it fills them all.
     *
     * @param instances
     *            the instances of a test, outermost first
     * @return the instances to fill, outermost first
     */
    private List<Object> instancesToFill(List<Object> instances) {
        for (int first = 0; first < instances.size(); first++) {
            if (editingContextsRegisteredBy(instances.get(first)).stream().noneMatch(editingContext -> editingContext == this)) {
                continue;
            }

            int end = first + 1;

            while (end < instances.size() && editingContextsRegisteredBy(instances.get(end)).isEmpty()) {
                end++;
            }

            return instances.subList(first, end);
        }

        return instances;
    }

    /**
     * Find the editing contexts that a test instance registers in its
     * <code>&#064;RegisterExtension</code> instance fields, inherited ones
     * included. Fields that can't be made accessible are left out.
     *
     * @param instance
     *            a test instance
     * @return the editing contexts it registers
     */
    private static List<AbstractEditingContextRule> editingContextsRegisteredBy(Object instance) {
        List<AbstractEditingContextRule> editingContexts = new ArrayList<AbstractEditingContextRule>();

        for (Class<?> type = instance.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !field.isAnnotationPresent(RegisterExtension.class) || !field.trySetAccessible()) {
                    continue;
                }

                try {
                    if (field.get(instance) instanceof AbstractEditingContextRule editingContext) {
                        editingContexts.add(editingContext);
                    }
                } catch (IllegalAccessException exception) {
                    // Can't happen once the field is accessible
                    throw new IllegalStateException(exception);
                }
            }
        }

        return editingContexts;
    }

    /**
     * Reset this editing context after each JUnit Jupiter test.
     *
     * @see #after()
     */
    @Override
    public void afterEach(ExtensionContext context) {
        after();
    }

    /**
     * Release what the constructor set up, such as the models it loaded, after
     * {@link #beforeAll(ExtensionContext)} has failed the test class.
     *
     * @since 2.1
     */
    @Override
    public void afterAll(ExtensionContext context) {
        // after() releases the lock that before() takes
        lock();

        after();
    }

    /**
     * Set up this editing context in order to execute the test case.
     * <p>
     * Create and insert objects for fields annotated with @UnderTest before the
     * test execution.
     */
    protected void before() {
        ERXEC.setFactory(new WOUnitEditingContextFactory(this));

        lock();

        processor.process(UnderTest.class, new UnderTestFacade(this));
    }

    /**
     * Load the model with the specified name into the default model group.
     *
     * @param modelName
     *            name of the model to be loaded
     * @throws IllegalArgumentException
     *             if no model can be found with the specified name
     * @see EOModelGroup#defaultGroup
     */
    protected void loadModel(String modelName) {
        EOModelGroup modelGroup = EOModelGroup.defaultGroup();

        EOModel model = modelGroup.modelNamed(modelName);

        if (model != null) {
            return;
        }

        URL url = getClass().getResource("/Resources/" + modelName + ".eomodeld");

        if (url == null) {
            url = getClass().getResource("/" + modelName + ".eomodeld");
        }

        if (url == null) {
            throw new IllegalArgumentException(WOUnitTroubleshooter.diagnoseModelNotFound(modelName));
        }

        modelGroup.addModelWithPathURL(url);

        modelToUnload.add(modelName);
    }

}
