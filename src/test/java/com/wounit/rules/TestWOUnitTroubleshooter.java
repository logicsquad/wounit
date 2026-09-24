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

import static com.wounit.rules.WOUnitTroubleshooter.Utils.extractModelName;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.wounit.rules.WOUnitTroubleshooter;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class TestWOUnitTroubleshooter {
    @Test
    public void diagnoseEveryTime() throws Exception {
	WOUnitTroubleshooter.diagnoseModelNotFound("xxx");

	String result = WOUnitTroubleshooter.diagnoseModelNotFound("xxx");

	assertThat(result, containsString(" Available models:"));
    }

    @Test
    public void doNotSuggestModelNameWhenNoGoodMatchFound() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound("AAAABBBBBCCCCCDDDDDEEEE");

	assertThat(result, not(containsString("Did you mean")));
    }

    @Test
    public void doNotSuggestWhenModelNameIsNull() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound(null);

	assertThat(result, not(containsString("Did you mean")));
    }

    @Test
    public void modelNameForFilePath() throws Exception {
	String result = extractModelName("/Users/user/Documents/workspace/project/Resources/Sample.eomodeld/index.eomodeld");

	assertThat(result, is("Sample"));
    }

    @Test
    public void modelNameForNSBundlePath() throws Exception {
	String result = extractModelName("Nonlocalized.lproj/Sample.eomodeld");

	assertThat(result, is("Sample"));
    }

    @Test
    public void modelNameIsNullWhenNoModelNameMatches() throws Exception {
	String result = extractModelName("Nonlocalized.lproj/Sample.xxx");

	assertThat(result, nullValue());
    }

    @Test
    public void listAvailableModelNames() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound("xxx");

	assertThat(result, containsString(" Available models:"));
	assertThat(result, containsString("\n  - AnotherTest"));
	assertThat(result, containsString("\n  - Test"));
	assertThat(result, containsString("\n  - erprototypes"));
    }

    @Test
    public void modelNotFoundMessage() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound("xxx");

	assertThat(result, startsWith("Cannot load model named 'xxx'."));
    }

    @Test
    public void suggestModelNameIgnoresCaseWhenFindingBestMatch() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound("ERPROTOTYPES");

	assertThat(result, containsString(" Did you mean 'erprototypes'?"));
    }

    @Test
    public void suggestModelNameWhenPossible() throws Exception {
	String result = WOUnitTroubleshooter.diagnoseModelNotFound("Teste");

	assertThat(result, containsString(" Did you mean 'Test'?"));
    }
}
