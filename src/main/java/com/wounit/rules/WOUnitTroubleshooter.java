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

import static com.wounit.rules.WOUnitTroubleshooter.Utils.findAvailableModels;
import static com.wounit.rules.WOUnitTroubleshooter.Utils.findSimilarModelName;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSMutableArray;

/**
 * This class provides utility methods to help diagnose problems when running
 * WOUnit tests. This class is intended for internal use.
 * 
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 * @since 1.3
 */
class WOUnitTroubleshooter {
    protected static class Utils {
	protected static String extractModelName(String path) {
	    Pattern pattern = Pattern.compile("(.*?)\\.eomodeld");
	    Matcher matcher = pattern.matcher(path);

	    if (matcher.find()) {
		String match = matcher.group(1);

		return match.substring(match.lastIndexOf("/") + 1);
	    }

	    return null;
	}

	protected static Set<String> findAvailableModels() {
	    List<String> paths = new ArrayList<String>();

	    for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
		paths.addAll(findModelsInClassPathEntry(new File(entry)));
	    }

	    try {
		URL resourcesFolder = WOUnitTroubleshooter.class.getResource("/");

		if (resourcesFolder != null) {
		    paths.addAll(findModelsRecursively(new File(resourcesFolder.toURI())));
		}
	    } catch (URISyntaxException exception) {
		// Ignore the exception and keep trying to diagnose
	    }

	    NSMutableArray<NSBundle> bundles = new NSMutableArray<NSBundle>(NSBundle.frameworkBundles());

	    bundles.add(NSBundle.mainBundle());

	    for (NSBundle bundle : bundles) {
		NSArray<String> modelPaths = bundle.resourcePathsForDirectories("eomodeld", null);

		for (String modelPath : modelPaths) {
		    paths.add(modelPath);
		}
	    }

	    Set<String> modelNames = new TreeSet<String>();

	    for (String path : paths) {
		modelNames.add(extractModelName(path));
	    }

	    return modelNames;
	}

	protected static List<String> findModelsInClassPathEntry(File entry) {
	    if (!entry.getName().endsWith(".jar")) {
		return findModelsRecursively(entry);
	    }

	    List<String> models = new ArrayList<String>();

	    try (JarFile jar = new JarFile(entry)) {
		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
		    String name = entries.nextElement().getName();

		    if (name.contains("eomodeld/index.eomodeld")) {
			models.add(name);
		    }
		}
	    } catch (IOException exception) {
		// Ignore the exception and keep trying to diagnose
	    }

	    return models;
	}

	protected static List<String> findModelsRecursively(File base) {
	    if (base.isDirectory()) {
		File[] files = base.listFiles();

		List<String> models = new ArrayList<String>();

		if (files == null) {
		    return models;
		}

		for (File file : files) {
		    models.addAll(findModelsRecursively(file));
		}

		return models;
	    }

	    if (base.getAbsolutePath().contains("eomodeld/index.eomodeld")) {
		return Arrays.asList(new String[] { base.getAbsolutePath() });
	    }

	    return Collections.emptyList();
	}

	protected static String findSimilarModelName(Set<String> names, String wrongName) {
	    String bestMatch = null;
	    int bestDistance = 5;

	    for (String name : names) {
		int distance = levenshteinDistance(name.toLowerCase(), wrongName.toLowerCase(), 5);

		if (distance <= bestDistance) {
		    bestMatch = name;
		    bestDistance = distance;
		}
	    }

	    return bestMatch;
	}

	protected static int levenshteinDistance(CharSequence x, CharSequence y, int limit) {
	    return levenshteinDistance(x, y, limit, 0);
	}

	private static int levenshteinDistance(CharSequence x, CharSequence y, int limit, int accumulator) {
	    if (accumulator > limit) {
		return accumulator;
	    }

	    int n = x.length();
	    int m = y.length();

	    if (n == 0) {
		return accumulator + m;
	    }

	    if (m == 0) {
		return accumulator + n;
	    }

	    int cost = 0;

	    if (x.charAt(n - 1) != y.charAt(m - 1)) {
		cost = 1;
	    }

	    return Math.min(levenshteinDistance(x.subSequence(0, n - 1), y, limit, accumulator + 1), Math.min(levenshteinDistance(x, y.subSequence(0, m - 1), limit, accumulator + 1), levenshteinDistance(x.subSequence(0, n - 1), y.subSequence(0, m - 1), limit, accumulator + cost)));
	}
    }

    /**
     * Diagnose eomodel not found errors.
     *
     * @param modelName
     *            the name of the model not found
     * @return a message saying the model could not be loaded, suggesting a
     *         similar model name if there is one, and listing the available
     *         models
     */
    static String diagnoseModelNotFound(String modelName) {
	Set<String> models = findAvailableModels();

	StringBuilder message = new StringBuilder(String.format("Cannot load model named '%s'.", modelName));

	if (modelName != null) {
	    String suggestedName = findSimilarModelName(models, modelName);

	    if (suggestedName != null) {
		message.append(String.format(" Did you mean '%s'?", suggestedName));
	    }
	}

	if (models.isEmpty()) {
	    message.append(" No models are available.");
	} else {
	    message.append(" Available models:");

	    for (String model : models) {
		message.append(String.format("\n  - %s", model));
	    }
	}

	return message.toString();
    }
}
