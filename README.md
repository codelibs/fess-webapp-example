# Fess WebApp Plugin Example

[![Java CI with Maven](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/fess-webapp-example/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.fess/fess-webapp-example)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A demonstration WebApp plugin for [Fess](https://fess.codelibs.org/), showing how to create custom JSP design templates and extend the search engine's web interface functionality.

## Overview

This plugin demonstrates how to extend Fess's web application layer by providing custom JSP templates for various UI components. It serves as a practical example for developers who want to create their own custom web interfaces for Fess search applications.

### Key Features

- **Custom JSP Templates**: Provides custom design templates for search pages, navigation, and error handling
- **System Helper Extension**: Extends Fess's `SystemHelper` class with enhanced error handling and logging
- **Component Registration**: Demonstrates dependency injection configuration using LastaDi framework
- **Comprehensive UI Coverage**: Includes templates for search interface, user management, and error pages

## Supported UI Components

The plugin registers custom JSP templates for the following components:

### Search Interface
- `index.jsp` - Main search page
- `search.jsp` - Search interface
- `searchResults.jsp` - Search results display
- `searchNoResult.jsp` - No results found page
- `searchOptions.jsp` - Search options
- `advance.jsp` - Advanced search
- `help.jsp` - Help page

### Navigation & Layout
- `header.jsp` - Page header
- `footer.jsp` - Page footer

### Error Handling
- `error/error.jsp` - General error page
- `error/notFound.jsp` - 404 Not Found
- `error/system.jsp` - System error
- `error/redirect.jsp` - Redirect error
- `error/badRequest.jsp` - 400 Bad Request

### User Interface
- `login/index.jsp` - Login page
- `profile/index.jsp` - User profile page

### Cache Display
- `cache.hbs` - Cache display template (Handlebars)

## Requirements

- Java 21 or later
- Maven 3.6 or later
- Fess 15.0 or later

## Installation

### From Maven Repository

The plugin is available on Maven Central:

```xml
<dependency>
    <groupId>org.codelibs.fess</groupId>
    <artifactId>fess-webapp-example</artifactId>
    <version>15.0.0</version>
</dependency>
```

### Manual Installation

1. Download the plugin JAR from [Maven Repository](https://repo1.maven.org/maven2/org/codelibs/fess/fess-webapp-example/)
2. Follow the [Plugin Installation Guide](https://fess.codelibs.org/admin/plugin-guide.html) in the Fess documentation

### Building from Source

```bash
git clone https://github.com/codelibs/fess-webapp-example.git
cd fess-webapp-example
mvn clean package
```

The compiled JAR will be available in the `target/` directory.

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/codelibs/fess/plugin/webapp/helper/
│   │       └── CustomSystemHelper.java
│   └── resources/
│       └── fess+systemHelper.xml
└── test/
    ├── java/
    │   └── org/codelibs/fess/plugin/webapp/helper/
    │       └── CustomSystemHelperTest.java
    └── resources/
        └── test_app.xml
```

### Core Components

#### CustomSystemHelper

The main plugin class that extends Fess's `SystemHelper`:

- **Location**: `src/main/java/org/codelibs/fess/plugin/webapp/helper/CustomSystemHelper.java`
- **Function**: Overrides `parseProjectProperties()` with enhanced error handling
- **System Property**: Sets `fess.webapp.plugin=true` during initialization

#### Configuration

- **DI Configuration**: `src/main/resources/fess+systemHelper.xml`
- **Component Registration**: Maps UI component names to JSP template files
- **Test Configuration**: `src/test/resources/test_app.xml`

### Building and Testing

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Create package
mvn clean package

# Format code
mvn formatter:format

# Check license headers
mvn license:check

# Generate documentation
mvn javadoc:javadoc
```

### Creating Custom Templates

1. Extend the `CustomSystemHelper` class or create your own helper
2. Register your JSP templates in the DI configuration file
3. Ensure your plugin JAR includes the manifest entry: `Fess-WebAppJar=true`

## Configuration

The plugin uses LastaDi dependency injection framework. Template mappings are configured in `fess+systemHelper.xml`:

```xml
<component name="systemHelper" class="org.codelibs.fess.plugin.webapp.helper.CustomSystemHelper">
    <postConstruct name="addDesignJspFileName">
        <arg>"index"</arg>
        <arg>"index.jsp"</arg>
    </postConstruct>
    <!-- Additional template mappings... -->
</component>
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

### Development Guidelines

- Follow the existing code style and conventions
- Add appropriate test cases for new functionality
- Ensure all tests pass before submitting
- Update documentation as needed

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

- **Documentation**: [Fess Documentation](https://fess.codelibs.org/)
- **Plugin Guide**: [Plugin Installation Guide](https://fess.codelibs.org/admin/plugin-guide.html)
- **Issues**: [GitHub Issues](https://github.com/codelibs/fess-webapp-example/issues)
- **Discussions**: [GitHub Discussions](https://github.com/codelibs/fess-webapp-example/discussions)

## Related Projects

- [Fess](https://github.com/codelibs/fess) - The main Fess search server
- [LastaFlute](https://github.com/lastaflute/lastaflute) - Web framework used by Fess
- [DBFlute](https://github.com/dbflute/dbflute-core) - Database access framework

---

**CodeLibs Project** - https://www.codelibs.org/