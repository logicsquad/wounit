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

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.eof.ERXEntityClassDescription;

/**
 * This class makes Wonder forget the class descriptions of a model that has
 * been unloaded, so that the model gets new ones when it's loaded again.
 * <p>
 * When a model is loaded, Wonder's <code>ERXEntityClassDescription.Factory</code>
 * registers a class description for each of its entities, but only the first
 * time it sees the model's name. It also keeps the class descriptions it
 * creates, by model and entity name, for as long as the JVM runs. So a model
 * loaded again would get the class descriptions of its first load, which
 * describe entities that are no longer in the model group. The factory keeps
 * this state in private fields, and has only a global <code>reset()</code>,
 * which would also discard the class descriptions of models that are still
 * loaded, partials included.
 *
 * @since 2.1
 */
class ModelClassDescriptions {
    private static final Logger LOG = LoggerFactory.getLogger(ModelClassDescriptions.class);

    @SuppressWarnings("unchecked")
    private static <T> T factoryField(ERXEntityClassDescription.Factory factory, String name) throws ReflectiveOperationException {
	Field field = ERXEntityClassDescription.Factory.class.getDeclaredField(name);

	field.setAccessible(true);

	return (T) field.get(factory);
    }

    /**
     * Make Wonder's class description factory forget a model that isn't
     * loaded, if it has seen the model before. A model that's still loaded
     * must not be passed, since its entities would then get new class
     * descriptions the next time EOF asks for them.
     * <p>
     * If the factory doesn't have the fields this method expects, it logs a
     * warning and leaves the factory as it is.
     *
     * @param modelName
     *            the name of a model that isn't loaded
     */
    static void forget(String modelName) {
	ERXEntityClassDescription.Factory factory = ERXEntityClassDescription.factory();

	if (factory == null) {
	    return;
	}

	try {
	    NSMutableArray<String> registeredModelNames = factoryField(factory, "_registeredModelNames");
	    NSMutableDictionary<String, ERXEntityClassDescription> classDescriptionForEntity = factoryField(factory, "_classDescriptionForEntity");
	    NSMutableDictionary<String, NSMutableArray<EOEntity>> entitiesForClass = factoryField(factory, "_entitiesForClass");

	    registeredModelNames.removeObject(modelName);

	    // The keys are "<model name> <entity name>", and entity names have no spaces
	    String prefix = modelName + " ";

	    for (String key : classDescriptionForEntity.allKeys()) {
		if (key.startsWith(prefix) && key.indexOf(' ', prefix.length()) < 0) {
		    classDescriptionForEntity.removeObjectForKey(key);
		}
	    }

	    for (NSMutableArray<EOEntity> entities : entitiesForClass.allValues()) {
		for (EOEntity entity : entities.immutableClone()) {
		    EOModel model = entity.model();

		    if (model != null && modelName.equals(model.name())) {
			entities.removeIdenticalObject(entity);
		    }
		}
	    }
	} catch (ReflectiveOperationException | RuntimeException exception) {
	    LOG.warn("Cannot make Wonder forget the class descriptions of the model named {}. Its enterprise objects may describe the entities of an earlier load.", modelName, exception);
	}
    }

    private ModelClassDescriptions() {
	// Cannot be instantiated
    }
}
