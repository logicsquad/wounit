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

2.0 (unreleased)
----------------

* WOUnit requires Java 21. ([#1](https://github.com/logicsquad/wounit/issues/1))
* A missing EOModel fails with an `IllegalArgumentException` again on
  Java 9 and later, instead of a `ClassCastException`. Its message now
  suggests a similarly named model and lists the available models,
  which WOUnit used to print to the console instead.
  ([#5](https://github.com/logicsquad/wounit/issues/5))
* WOUnit depends on `org.hamcrest:hamcrest` 3.0 instead of
  `hamcrest-core` 1.3. ([#2](https://github.com/logicsquad/wounit/issues/2))
* `MockEditingContext` and `TemporaryEditingContext` are JUnit Jupiter
  extensions: register them with `@RegisterExtension`.
  ([#3](https://github.com/logicsquad/wounit/issues/3))
