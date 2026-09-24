<!--
Copyright (C) 2026 Logic Squad

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
-->

Changelog
=========

2.0 (2026-09-24)
----------------

WOUnit 2.0 is rebuilt for JUnit Jupiter and Java 21. The README's
[migration notes](https://github.com/logicsquad/wounit#migrating-from-wounit-15)
explain how to move from 1.5.

* WOUnit is published to Maven Central as `net.logicsquad:wounit`,
  instead of `com.wounit:wounit` on WOCommunity's repository. Its Java
  packages haven't changed.
  ([#7](https://github.com/logicsquad/wounit/issues/7))
* WOUnit requires Java 21. ([#1](https://github.com/logicsquad/wounit/issues/1))
* `MockEditingContext` and `TemporaryEditingContext` are JUnit Jupiter
  extensions: register them with `@RegisterExtension` instead of `@Rule`.
  JUnit 4 is no longer supported.
  ([#3](https://github.com/logicsquad/wounit/issues/3))
* WOUnit uses your project's Wonder and WebObjects instead of bringing
  its own, and is built and tested against Wonder 7.4. If you use
  `TemporaryEditingContext`, add `wonder.eoadaptors:JavaMemoryAdaptor` to
  your test dependencies.
  ([#11](https://github.com/logicsquad/wounit/issues/11))
* WOUnit depends on `org.hamcrest:hamcrest` 3.0 instead of
  `hamcrest-core` 1.3. Wonder's JUnit 4 dependency brings
  `hamcrest-core` 1.3 back, so exclude it, as the README shows.
  ([#2](https://github.com/logicsquad/wounit/issues/2))
* WOUnit's `@Spy` support works with Mockito 5, whether or not the test
  uses `MockitoExtension`. An enterprise object mocked with `mock()`
  needs Mockito's subclass mock maker to be inserted into an editing
  context. ([#6](https://github.com/logicsquad/wounit/issues/6))
* WOUnit logs its warnings through SLF4J instead of printing them to
  `System.out`. It uses your project's `slf4j-api`, and works with
  SLF4J 1.7 and 2.x. ([#8](https://github.com/logicsquad/wounit/issues/8))
* A missing EOModel fails with an `IllegalArgumentException` again on
  Java 9 and later, instead of a `ClassCastException`. Its message now
  suggests a similarly named model and lists the available models,
  which WOUnit used to print to the console instead.
  ([#5](https://github.com/logicsquad/wounit/issues/5))
