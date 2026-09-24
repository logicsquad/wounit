[![Build Status](https://github.com/logicsquad/wounit/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/logicsquad/wounit/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/net.logicsquad/wounit)](https://central.sonatype.com/artifact/net.logicsquad/wounit)
[![Javadoc](https://javadoc.io/badge2/net.logicsquad/wounit/javadoc.svg)](https://javadoc.io/doc/net.logicsquad/wounit)
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
> WOUnit 2.0 is rebuilt for [JUnit 6](https://junit.org/) and Java 21,
> and is published as `net.logicsquad:wounit`. It drops support for
> JUnit 4, so projects that use JUnit 4 should stay on WOUnit 1.5.

WOUnit helps you test WebObjects and Project Wonder applications with
JUnit Jupiter. It gives each test a fresh editing context, with your
EOModels loaded and no database behind it, so you can write fast unit
and integration tests for your Enterprise Objects, or write the tests
first.

Features
--------

* **No database needed**: everything happens in memory, so unit and
  integration tests run fast.
* **Built on Wonder**: the editing contexts are `ERXEC`s, so your
  enterprise objects go through the augmented transaction process that
  `ERXEnterpriseObject` defines.
* **Easy to use**: there's no base class to extend. Register an editing
  context as a JUnit Jupiter extension, and annotate the fields you want
  WOUnit to fill.
* **Simple, but not simpler**: one field loads your EOModels, and sets
  up and cleans up the editing context around every test.

Requirements
------------

* Java 21 or later
* [JUnit](https://junit.org/) 6
* [Project Wonder](https://github.com/wocommunity/wonder) 7.4
* WebObjects 5.4.3
* [Mockito](https://site.mockito.org/) 5, if you spy on enterprise objects

Adding WOUnit to your project
-----------------------------

WOUnit is published to Maven Central. Add it to your test dependencies:

```xml
<dependency>
    <groupId>net.logicsquad</groupId>
    <artifactId>wounit</artifactId>
    <version>2.0</version>
    <scope>test</scope>
</dependency>
```

WOUnit uses your project's own Wonder and WebObjects, from the
[WOCommunity repository](https://maven.wocommunity.org/) your project
already uses. It brings JUnit Jupiter's API and Hamcrest with it, but
your tests also need JUnit Jupiter's engine: see JUnit's
[instructions for Maven](https://docs.junit.org/current/running-tests/build-support.html#maven).

`TemporaryEditingContext` also needs Wonder's memory adaptor:

```xml
<dependency>
    <groupId>wonder.eoadaptors</groupId>
    <artifactId>JavaMemoryAdaptor</artifactId>
    <version>7.4</version>
    <scope>test</scope>
</dependency>
```

Wonder's `ERExtensions` brings JUnit 4, and with it Hamcrest 1.3's
`hamcrest-core`. Next to WOUnit's Hamcrest 3.0, the older classes
usually win. Exclude `hamcrest-core` to use Hamcrest 3.0 alone:

```xml
<dependency>
    <groupId>wonder.core</groupId>
    <artifactId>ERExtensions</artifactId>
    <version>7.4</version>
    <exclusions>
        <exclusion>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

To spy on enterprise objects, add `org.mockito:mockito-core` 5, and
`org.mockito:mockito-junit-jupiter` if you want Mockito's JUnit
extension. On Java 21 and later, Mockito recommends loading its agent
explicitly rather than letting it attach itself: see
[Mockito's instructions](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3).

Getting started
---------------

### A first test

```java
import static com.wounit.matchers.EOAssert.cannotBeSaved;
import static com.wounit.matchers.EOAssert.confirm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.wounit.annotations.UnderTest;
import com.wounit.rules.MockEditingContext;

class BookTest {
    @RegisterExtension
    MockEditingContext editingContext = new MockEditingContext("Library");

    @UnderTest
    Book book;

    @Test
    void cannotBeSavedWithoutATitle() {
        book.setTitle(null);

        confirm(book, cannotBeSaved());
    }
}
```

Here's what happens:

* `@RegisterExtension` registers the editing context with JUnit. JUnit
  creates a new test instance for each test by default, so each test
  gets a fresh editing context.
* The constructor loads the `Library` EOModel from the classpath, as
  `Resources/Library.eomodeld` or `Library.eomodeld`. Pass as many model
  names as the test needs. If WOUnit can't find a model, its exception
  lists the models it can see.
* Before each test, WOUnit fills the `@UnderTest` field with a new
  `Book`, inserted into the editing context.
* After each test, WOUnit disposes of the editing context and unloads
  the models it loaded.
* `confirm()` and the matchers come from `EOAssert`, described below.

### Unit tests with MockEditingContext

`MockEditingContext` keeps everything in memory. As well as the objects
you create and insert as usual, it can create *saved* objects. They
look as though they came from the database, and the editing context
ignores their changes, so they're never validated or saved. That lets
you test one object in isolation, with the objects around it as simple
stand-ins:

```java
@Test
void canBeSavedWithATitleAndAnAuthor() {
    Author author = editingContext.createSavedObject(Author.class);

    book.setTitle("Untitled");
    book.setAuthor(author);

    confirm(book, canBeSaved());
}
```

Here `author` needs none of the attributes a real `Author` would. To
register an object you've built yourself as saved, such as a spy, use
`insertSavedObject()`.

Fetches work too: `MockEditingContext` answers them from the objects it
holds, applying the fetch specification's qualifier, sort orderings and
fetch limit. Saving works in memory, and gives new objects permanent
global IDs.

### Integration tests with TemporaryEditingContext

`TemporaryEditingContext` runs your models on Wonder's memory adaptor.
Objects are validated, saved and fetched as they would be with a
database, and the data is thrown away after each test. Use it to test
how a group of objects behaves together. It needs the
`JavaMemoryAdaptor` dependency shown above.

```java
class LibraryTest {
    @RegisterExtension
    TemporaryEditingContext editingContext = new TemporaryEditingContext("Library");

    @Test
    void savesABookWithItsAuthor() {
        Author author = Author.createAuthor(editingContext, "Anonymous");
        Book.createBook(editingContext, "Untitled", author);

        confirm(editingContext, saveChanges());
    }
}
```

### Choosing an editing context

The difference is what sits behind the editing context:

* `MockEditingContext` has nothing behind it. Saving validates your
  changes and marks the objects as saved, and a fetch searches the
  objects the editing context already holds. Nothing is written
  anywhere.
* `TemporaryEditingContext` has EOF's whole database layer behind it,
  with Wonder's memory adaptor in place of a database. Saving writes
  rows, and a fetch reads them back.

That matters whenever a test fetches, or relies on what was saved:

|                                                         | `MockEditingContext` | `TemporaryEditingContext`      |
|---------------------------------------------------------|----------------------|--------------------------------|
| A fetch finds objects that haven't been saved           | Yes                  | No, as with a database         |
| A fetch applies the entity's restricting qualifier      | No                   | Yes                            |
| Another editing context can read saved data             | No                   | Yes, relationships included    |
| Saved stand-ins, with `@Dummy` or `createSavedObject()` | Yes                  | No: every object must be valid |
| Extra dependency                                        | None                 | `JavaMemoryAdaptor`            |

So:

* Use `MockEditingContext` to test an object's own behaviour, such as
  its validation rules, derived values and business logic, with simple
  stand-ins for the objects around it.
* Use `TemporaryEditingContext` when the test is about what EOF saves
  and fetches: a qualifier or fetch specification, code that saves and
  then fetches, or several related objects saved together.

For example, suppose `Book` can find books by title:

```java
public static NSArray<Book> booksTitled(EOEditingContext editingContext, String title) {
    return fetchBooks(editingContext, TITLE.eq(title), null);
}
```

With `TemporaryEditingContext`, the fetch finds the book only once it
has been saved, as it would in the application:

```java
class BookFetchTest {
    @RegisterExtension
    TemporaryEditingContext editingContext = new TemporaryEditingContext("Library");

    @Test
    void findsSavedBooksByTitle() {
        Author author = Author.createAuthor(editingContext, "Anonymous");
        Book book = Book.createBook(editingContext, "Untitled", author);

        assertThat(Book.booksTitled(editingContext, "Untitled"), is(empty()));

        editingContext.saveChanges();

        assertThat(Book.booksTitled(editingContext, "Untitled"), contains(book));
    }
}
```

With `MockEditingContext`, the first assertion would fail, because the
fetch finds the unsaved book. So if the code under test forgot to save,
a `MockEditingContext` test wouldn't notice.

### Filling fields with @UnderTest and @Dummy

Two annotations save you from creating the same objects at the start of
every test:

* `@UnderTest` fills a field with a new object, inserted into the
  editing context. It's meant for the object under test, and works with
  both editing contexts.
* `@Dummy` fills a field with a saved object, as `createSavedObject()`
  would. It works only with `MockEditingContext`.

Here's the `MockEditingContext` test from above, with WOUnit creating
both objects:

```java
class BookTest {
    @RegisterExtension
    MockEditingContext editingContext = new MockEditingContext("Library");

    @UnderTest
    Book book;

    @Dummy
    Author author;

    @Test
    void canBeSavedWithATitleAndAnAuthor() {
        book.setTitle("Untitled");
        book.setAuthor(author);

        confirm(book, canBeSaved());
    }
}
```

To fill an `NSArray` field with several objects, give either annotation
a `size`, as in `@Dummy(size = 3) NSArray<Author> authors`.

### Assertions with EOAssert

`EOAssert` provides Hamcrest matchers for enterprise objects and
editing contexts, and `confirm()` to apply them. Import them
statically:

```java
import static com.wounit.matchers.EOAssert.*;
```

```java
// Validation, without saving
confirm(book, canBeSaved());
confirm(book, cannotBeSaved());
confirm(book, cannotBeSavedBecause("The title property cannot be null"));
confirm(book, canBeDeleted());
confirm(book, cannotBeDeleted());
confirm(book, cannotBeDeletedBecause("A book on loan cannot be deleted"));

// State
confirm(book, hasBeenSaved());
confirm(book, hasNotBeenSaved());
confirm(book, hasBeenDeleted());
confirm(book, hasNotBeenDeleted());

// Saving the editing context
confirm(editingContext, saveChanges());
confirm(editingContext, doNotSaveChanges());
confirm(editingContext, doNotSaveChangesBecause("The title property cannot be null"));
```

The `...Because()` matchers also check the validation message. To check
a value by key, use `hasValueForKey()` with Hamcrest's `assertThat()`:

```java
assertThat(book, hasValueForKey(Book.TITLE, is("Untitled")));
assertThat(books, hasItem(hasValueForKey("title", is("Untitled"))));
```

### Spying with Mockito

Add Mockito's `@Spy` to an `@UnderTest` or `@Dummy` field, and WOUnit
fills it with a spy: still a real enterprise object in the editing
context, but one whose methods you can stub and verify. That helps with
code that would otherwise reach the database, such as a method that
runs an aggregate query.

```java
@ExtendWith(MockitoExtension.class)
class AuthorTest {
    @RegisterExtension
    MockEditingContext editingContext = new MockEditingContext("Library");

    @Spy
    @UnderTest
    Author author;

    @Test
    void isProlificWithTenBooksInPrint() {
        // countOfBooksInPrint() runs an aggregate query
        doReturn(10).when(author).countOfBooksInPrint();

        assertThat(author.isProlific(), is(true));
    }
}
```

WOUnit creates the spy itself if Mockito hasn't, so `MockitoExtension`
is optional.

Mockito 5 makes mocks with its inline mock maker, and an enterprise
object mocked that way can't be inserted into an editing context. If
you need to insert a mock, make it with the subclass mock maker:

```java
Book book = mock(Book.class, withSettings().mockMaker(MockMakers.SUBCLASS));
```

### Code that creates its own editing context

Before each test, both editing contexts install a
`WOUnitEditingContextFactory`, so that `ERXEC.newEditingContext()`
returns the test's editing context. After the test, Wonder's default
factory is back. Code under test that creates its own editing context
works with the same objects as your test.

The [Javadoc](https://javadoc.io/doc/net.logicsquad/wounit) covers the
whole API.

Migrating from WOUnit 1.5
-------------------------

WOUnit 2.0 keeps 1.5's features and its `com.wounit` packages. Most of
the work is moving your tests to JUnit Jupiter:

1. **Move to Java 21 and JUnit Jupiter.** WOUnit 2.0 doesn't support
   JUnit 4. JUnit's
   [migration guide](https://docs.junit.org/current/migrating-from-junit4.html)
   covers `@Before` → `@BeforeEach` and the rest.
2. **Change the dependency** from `com.wounit:wounit:1.5` to
   `net.logicsquad:wounit:2.0`, as shown above. The packages haven't
   changed, so your imports stay as they are.
3. **Register editing contexts with `@RegisterExtension`** instead of
   `@Rule`:

   ```java
   // WOUnit 1.5
   @Rule
   public MockEditingContext editingContext = new MockEditingContext("Library");

   // WOUnit 2.0
   @RegisterExtension
   MockEditingContext editingContext = new MockEditingContext("Library");
   ```

4. **Add `JavaMemoryAdaptor`** if you use `TemporaryEditingContext`.
   WOUnit no longer brings Wonder or WebObjects with it, and is built and
   tested against Wonder 7.4.
5. **Update Mockito to 5**, if you use `@Spy`. Replace
   `@RunWith(MockitoJUnitRunner.class)` with
   `@ExtendWith(MockitoExtension.class)`, or drop it. An enterprise
   object mocked with `mock()` now needs the subclass mock maker to be
   inserted into an editing context, as shown above.
6. **Use Hamcrest 3.0.** WOUnit depends on `org.hamcrest:hamcrest` 3.0
   instead of `hamcrest-core` 1.3. Exclude `hamcrest-core` from Wonder,
   as shown above, and use `org.hamcrest.MatcherAssert.assertThat()`
   instead of JUnit 4's `Assert.assertThat()`.

The [changelog](CHANGELOG.md) lists every change in 2.0.

History
-------

WOUnit comes from a long line of tools for testing WebObjects:

* **WOFJUnit**, by Shin Ogino.
* **[WOUnitTest](https://wounittest.sourceforge.net/)**, which Shin Ogino
  and Christian Pekeler built from WOFJUnit, with early contributions
  from Josh Flowers and Bill Bumgarner.
* **WOUnitTest 2**, Christian Pekeler's leaner WOUnitTest for JUnit 4,
  released in 2006.
* **WOUnit**, which Henrique Prange created in 2009 as an evolution of
  WOUnitTest 2, and developed at
  [hprange/wounit](https://github.com/hprange/wounit) up to 1.5 in 2022.

We're grateful to everyone who built them.
