# Fess WebApp Plugin Example

[![Java CI with Maven](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A minimal, copy-from template for building a [Fess](https://fess.codelibs.org/) **WebApp plugin**.

It shows the one thing every WebApp plugin needs to get right: how to **add** your
own components to Fess's dependency-injection (DI) container using the additive
`app++.xml` merge convention, **without overriding or copying any Fess core
component**.

## What this plugin does

- Registers a single new component, `exampleHelper`
  ([`ExampleHelper`](src/main/java/org/codelibs/fess/webapp/example/helper/ExampleHelper.java)),
  via [`app++.xml`](src/main/resources/app++.xml).
- `ExampleHelper` has one small, real method, `getPluginLabel()`, which reads the
  running Fess version from the core `SystemHelper` (looked up through
  `ComponentUtil`) and returns a label such as `fess-webapp-example (Fess 15.7.0)`.

That is deliberately tiny. The value of this repository is the *wiring*, which you
can copy and replace with your own helper, action, or service.

## How the additive merge works

Fess assembles its DI container from many small LastaDi XML files. A plugin can
contribute to that container by shipping XML files on the classpath whose names
follow LastaDi's merge conventions:

| File name pattern | Effect |
| --- | --- |
| `app++.xml` | **Adds** components to the `app` namespace (merge). Used here. |
| `fess_query+<name>.xml` | **Overrides** the core component named `<name>`. |
| `fess_api++.xml` | **Adds** components to the `fess_api` namespace (merge). |

The `++` suffix means "merge into this namespace". Because `exampleHelper` does
not exist in Fess core, nothing is overridden — the component is simply added.
This is the recommended way to extend Fess: add new components rather than
replacing core singletons.

```xml
<!-- src/main/resources/app++.xml -->
<components>
    <component name="exampleHelper"
        class="org.codelibs.fess.webapp.example.helper.ExampleHelper" />
</components>
```

## The `Fess-WebAppJar` manifest

A WebApp plugin JAR must declare itself with the manifest entry
`Fess-WebAppJar: true` so Fess loads its classes and DI XML into the web
application. This is set in [`pom.xml`](pom.xml) via the `maven-jar-plugin`:

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
- Fess 15.7 or later

## Project structure

```
src/
├── main/
│   ├── java/
│   │   └── org/codelibs/fess/webapp/example/helper/
│   │       └── ExampleHelper.java
│   └── resources/
│       └── app++.xml
└── test/
    ├── java/
    │   └── org/codelibs/fess/webapp/example/
    │       ├── UnitWebappTestCase.java
    │       └── helper/
    │           └── ExampleHelperTest.java
    └── resources/
        └── test_app.xml
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

The test ([`ExampleHelperTest`](src/test/java/org/codelibs/fess/webapp/example/helper/ExampleHelperTest.java))
loads [`test_app.xml`](src/test/resources/test_app.xml), which `<include>`s the
plugin's `app++.xml`, then retrieves `exampleHelper` from the DI container exactly
as Fess does at runtime. That proves the `app++.xml` wiring is correct.

## Installation

### Building from source

```bash
git clone https://github.com/codelibs/fess-webapp-example.git
cd fess-webapp-example
mvn clean package
```

### Installing into Fess

Place the built JAR in Fess's plugin directory, or install it from the admin UI.
See the [Plugin Installation Guide](https://fess.codelibs.org/15.7/admin/plugin-guide.html).

## How to extend it

1. Add your own class (a helper, action, service, etc.) under
   `org.codelibs.fess.webapp.example`.
2. Register it in `app++.xml` with a unique component name that does **not**
   collide with a Fess core component (unless you intend to override one).
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
