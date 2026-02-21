# tamboui-spring

Spring Boot integration for [TamboUI](https://github.com/tamboui/tamboui) — build terminal UIs with annotations, dependency injection, and convention over configuration.

[![CI](https://img.shields.io/github/actions/workflow/status/KyleKreuter/tamboui-spring/ci.yml?branch=main&label=CI)](https://github.com/KyleKreuter/tamboui-spring/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kylekreuter/tamboui-spring-boot-starter?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kylekreuter/tamboui-spring-boot-starter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25%2B-blue)](https://openjdk.org/projects/jdk/25/)

> **Pre-release** — API subject to change.

## Quick Start

Add the starter to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.kylekreuter</groupId>
    <artifactId>tamboui-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Create a screen controller:

```java
@TamboScreen(template = "dashboard")
public class DashboardController implements ScreenController {

    @Override
    public void populate(TemplateModel model) {
        model.put("title", "Hello TamboUI!");
    }
}
```

Create a template (`src/main/resources/templates/dashboard.ttl`):

```xml
<t:panel title="${title}" class="border-rounded p-1 text-cyan">
    <t:text t:text="Welcome to TamboUI Spring Boot!" />
</t:panel>
```

## Modules

| Module | Description |
|--------|-------------|
| `tamboui-spring-boot-autoconfigure` | Auto-configuration, annotations, template engine |
| `tamboui-spring-boot-starter` | Starter POM with default dependencies |
| `tamboui-spring-demo` | Demo application |

## Prerequisites

- Java 25+
- Maven 3.8+

## Building

```bash
mvn clean install
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

MIT License — see [LICENSE](LICENSE) for details.
