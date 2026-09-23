[![Build Status](https://github.com/logicsquad/wounit/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/logicsquad/wounit/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

WOUnit
======

> **This is the [Logic Squad](https://logicsquad.net) fork of WOUnit.**
> WOUnit was created by Henrique Prange, who developed and maintained it
> at [hprange/wounit](https://github.com/hprange/wounit) up to its 1.5
> release in 2022. The original project no longer appears to be under
> active development, so we're carrying it on here — with our thanks to
> Henrique for more than a decade of work.
>
> WOUnit 2.0 is being rebuilt for [JUnit 6](https://junit.org/) and
> Java 21, and will be published as `net.logicsquad:wounit`. It drops
> support for JUnit 4, so projects that use JUnit 4 should stay on
> WOUnit 1.5.
>
> 2.0 has not been released yet. Until it is, the rest of this README
> describes WOUnit 1.5. Work on 2.0 is tracked in
> [GitHub Issues](https://github.com/logicsquad/wounit/issues).

The WOUnit framework contains a set of utilities for testing WebObjects
applications using JUnit 4.7 or later capabilities. This library can be
useful if you write unit/integration tests for Enterprise Objects or
employ the TDD technique on your projects.

**Version**: 1.5

Requirements
------------

* [JUnit](http://www.junit.org/) 4.7 or later
* [Project Wonder](http://wiki.objectstyle.org/confluence/display/WONDER/Home) 6 or later
* WebObjects 5
* Java 8

Features
--------

* **No Database Access Needed**: all the logic is handled in memory for fast unit testing
and integration testing.
* **Wonderful**: developed on top of Wonder classes, make possible the use of the augmented
transaction process specified by the ERXEnterpriseObject interface.
* **Easy to use**: no extension required for test classes. The WOUnit library makes use of
generics, annotations and the rule approach provided by JUnit 4.7.
* **Simple but not simpler**: only one line of code and you are ready to start writing tests.
The rules are responsible for loading eomodels, initializing and cleaning up before/after
test executions.

Installation
------------

Maven users have to add the dependency declaration:

	<dependency>
		<groupId>com.wounit</groupId>
		<artifactId>wounit</artifactId>
		<version>1.5</version>
	</dependency>

Non Maven users have to:

1. Download the wounit.jar.
2. Add the wounit library to the build path.

Usage
-----

	import static com.wounit.matchers.EOAssert.*;
	import com.wounit.rules.MockEditingContext;
	import com.wounit.annotations.Dummy;
    import com.wounit.annotations.UnderTest;

	public class MyEntityTest {
		@Rule
		public MockEditingContext ec = new MockEditingContext("MyModel");

		@Dummy
		private Bar dummyBar;

		@UnderTest
		private Foo foo;

		@Test
		public void cantSaveFooWithOnlyOneBar() {
			foo.addToBarRelationship(dummyBar);

			confirm(foo, cannotBeSavedBecause("Foo must have at least 2 bars related to it"));
		}
	}

OR

	import static com.wounit.matchers.EOAssert.*;
	import com.wounit.rules.TemporaryEditingContext;
	import com.wounit.annotations.UnderTest;

	public class MyEntityTest {
		@Rule
		public TemporaryEditingContext ec = new TemporaryEditingContext("MyModel");

		@UnderTest
		private Foo foo;

		@Test
		public void cannotSaveFooIfBarIsNull() {
			foo.setBar(null);

			confirm(foo, cannotBeSavedBecause("The bar property cannot be null"));
		}
	}

Building From Source
--------------------

### Building with Maven

WOUnit requires WebObjects and Wonder libraries that are not available in the Maven Central repository. Check steps 2 and 3 in the [Quick Start guide](http://wiki.wocommunity.org/display/WOL/Quick+Start) for more information about how to setup the WOCommunity repository and how to install WebObjects in the local Maven repository.

WOUnit can be built running the Maven command:

	mvn clean install

**Note**: WOUnit can only be successfully built if the WOCommunity repository is correctly configured.

Alternatively, use the `.maven_settings.xml` file in the project root, which configures the WOCommunity repository:

	mvn -s .maven_settings.xml clean install

### Importing into Eclipse

Maven users should install the [m2e](http://eclipse.org/m2e/) plug-in for Eclipse and use the _Import Maven Project_ option.

Acknowledge
-----------

This project is an evolution of the original [WOUnitTest 2](http://wounittest.sourceforge.net/)
framework and is heavily inspired by it.

About
-----

* **Site**: http://hprange.github.com/wounit
* **E-mail**: hprange at gmail.com
* **Twitter**: @hprange