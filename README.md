# Fess WebApp Plugin Example

[![Java CI with Maven](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A minimal, copy-from template for building a [Fess](https://fess.codelibs.org/) **WebApp plugin**.

It shows the two ways a WebApp plugin extends Fess's dependency-injection (DI)
container, side by side:

1. **Add a new component** &mdash; the recommended default. Register your own
   helper/action/service without touching Fess core.
2. **Override a core component** &mdash; replace a built-in Fess component with
   your own subclass when you genuinely need to change core behavior.

Both are tiny on purpose. The value of this repository is the *wiring* and the
*conventions*, which you can copy and replace with your own classes.

## How Fess assembles plugin DI

Fess builds its DI container from many small LastaDi XML files. A plugin
contributes by shipping XML files on the classpath whose **file names** follow
LastaDi's redefine conventions:

| File name pattern | Effect |
| --- | --- |
| `<baseDicon>++.xml` (e.g. `app++.xml`) | **Adds** new components into the container built from `<baseDicon>.xml` (additive merge). Nothing is overridden. |
| `<baseDicon>+<componentName>.xml` (e.g. `fess+systemHelper.xml`) | **Replaces** the single component named `<componentName>` that `<baseDicon>.xml` declares (per-component redefine). |

Two things are easy to get wrong:

- The **prefix must be the dicon that declares the component.** `systemHelper` is
  declared in Fess core's `fess.xml`, so the override file must be
  `fess+systemHelper.xml` &mdash; not `app+systemHelper.xml`.
- A redefine (`+`) replaces the **entire** component definition, including its
  `postConstruct` calls. Registering the same component `name` twice in one
  container is an *ambiguity error*, not an override &mdash; the `+` convention is
  exactly what lets you replace a core singleton cleanly.

## Pattern 1: Add a new component (`app++.xml` + `ExampleHelper`)

[`app++.xml`](src/main/resources/app++.xml) merges a single new component,
`exampleHelper`, into the `app` container. Because `exampleHelper` does not exist
in Fess core, nothing is overridden.

```xml
<!-- src/main/resources/app++.xml -->
<components>
    <component name="exampleHelper"
        class="org.codelibs.fess.webapp.example.helper.ExampleHelper" />
</components>
```

[`ExampleHelper`](src/main/java/org/codelibs/fess/webapp/example/helper/ExampleHelper.java)
has one small, real method, `getPluginLabel()`, which reads the running Fess
version from the core `SystemHelper` (looked up through `ComponentUtil`) and
returns a label such as `fess-webapp-example (Fess 15.8)`. It follows the
standard Fess idioms: `@PostConstruct` for initialization and `ComponentUtil` to
*reuse* core components instead of copying or overriding them.

This is the pattern you should reach for first.

## Pattern 2: Override a core component (`fess+systemHelper.xml` + `CustomSystemHelper`)

[`CustomSystemHelper`](src/main/java/org/codelibs/fess/webapp/example/helper/CustomSystemHelper.java)
extends Fess's `SystemHelper` and overrides `parseProjectProperties()` to tolerate
a parse failure and set a `fess.webapp.plugin` marker property.
[`fess+systemHelper.xml`](src/main/resources/fess+systemHelper.xml) re-registers
`systemHelper` with this subclass:

```xml
<!-- src/main/resources/fess+systemHelper.xml -->
<component name="systemHelper"
    class="org.codelibs.fess.webapp.example.helper.CustomSystemHelper">
    <postConstruct name="addDesignJspFileName">
        <arg>"index"</arg>
        <arg>"index.jsp"</arg>
    </postConstruct>
    <!-- ...the rest of Fess core's design-JSP mappings... -->
</component>
```

> **Maintenance cost (read before overriding):** because a redefine replaces the
> *whole* definition, the override must repeat **every** `postConstruct` the core
> `systemHelper` performs &mdash; the full set of design-JSP name mappings. These
> are copied verbatim from Fess core's `fess.xml`, and the referenced `*.jsp`
> files are provided by Fess itself (this plugin ships none of them). You must
> keep that list in sync with each Fess release, or design pages (e.g.
> `chat` / `busy` / `newpassword`) will stop resolving. This is exactly why
> Pattern 1 is preferred whenever you do not truly need to replace core behavior.

## The `Fess-WebAppJar` manifest

A WebApp plugin JAR must declare itself with the manifest entry
`Fess-WebAppJar: true` so Fess mounts its classes and DI XML into the web
application's classloader. This is set in [`pom.xml`](pom.xml) via the
`maven-jar-plugin`:

```xml
<plugin>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifestEntries>
                <Fess-WebAppJar>true</Fess-WebAppJar>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```

## Requirements

- Java 21 or later
- Maven 3.8 or later
- Fess 15.8 or later

## Project structure

```
src/
├── main/
│   ├── java/
│   │   └── org/codelibs/fess/webapp/example/helper/
│   │       ├── ExampleHelper.java          # Pattern 1: a brand-new component
│   │       └── CustomSystemHelper.java      # Pattern 2: a core-component override
│   └── resources/
│       ├── app++.xml                        # Pattern 1: additive merge
│       └── fess+systemHelper.xml            # Pattern 2: per-component redefine
└── test/
    ├── java/
    │   └── org/codelibs/fess/webapp/example/
    │       ├── UnitWebappTestCase.java
    │       └── helper/
    │           ├── ExampleHelperTest.java
    │           └── CustomSystemHelperTest.java
    └── resources/
        ├── test_app.xml                     # loads app++.xml for Pattern 1
        └── test_systemhelper.xml            # loads fess+systemHelper.xml for Pattern 2
```

All code lives under the single package root `org.codelibs.fess.webapp.example`.

## Building and testing

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Build the plugin JAR (lands in target/)
mvn clean package

# Format code and license headers before committing
mvn formatter:format
mvn license:format
```

Each test retrieves its component from the DI container exactly as Fess does at
runtime, which proves the wiring is correct:

- [`ExampleHelperTest`](src/test/java/org/codelibs/fess/webapp/example/helper/ExampleHelperTest.java)
  loads [`test_app.xml`](src/test/resources/test_app.xml) (which `<include>`s
  `app++.xml`) and looks up `exampleHelper`.
- [`CustomSystemHelperTest`](src/test/java/org/codelibs/fess/webapp/example/helper/CustomSystemHelperTest.java)
  loads [`test_systemhelper.xml`](src/test/resources/test_systemhelper.xml) (which
  `<include>`s `fess+systemHelper.xml`) and verifies `systemHelper` resolves to the
  overriding `CustomSystemHelper`.

## Installation

### Building from source

```bash
git clone https://github.com/codelibs/fess-webapp-example.git
cd fess-webapp-example
mvn clean package
```

### Installing into Fess

Place the built JAR in Fess's plugin directory, or install it from the admin UI.
See the [Plugin Installation Guide](https://fess.codelibs.org/15.8/admin/plugin-guide.html).

## How to extend it

1. Add your own class (a helper, action, service, etc.) under
   `org.codelibs.fess.webapp.example`.
2. To **add** it, register it in `app++.xml` with a unique component name that
   does not collide with a Fess core component. To **override** a core component,
   ship a `<baseDicon>+<componentName>.xml` file and subclass the original.
3. Use `@PostConstruct` for initialization and `ComponentUtil.getXxx()` /
   `@Resource` to reuse core components instead of copying them.
4. Add a test that retrieves your component from the DI container and asserts its
   behavior.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Related projects

- [Fess](https://github.com/codelibs/fess) - The main Fess search server
- [LastaFlute](https://github.com/lastaflute/lastaflute) - Web framework used by Fess
- [DBFlute](https://github.com/dbflute/dbflute-core) - Database access framework

---

**CodeLibs Project** - https://www.codelibs.org/
