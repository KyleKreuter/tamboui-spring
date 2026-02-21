# tamboui-spring

Spring Boot integration for [TamboUI](https://github.com/tamboui/tamboui) — build terminal UIs with annotations, dependency injection, and convention over configuration.

<!-- ![Build Status](https://img.shields.io/github/actions/workflow/status/kylekreuter/tamboui-spring/ci.yml?branch=main) -->
<!-- ![Maven Central](https://img.shields.io/maven-central/v/io.github.kylekreuter/tamboui-spring-boot-starter) -->

> **Pre-release** — API subject to change.

## Build

```bash
mvn clean install
```

## Usage

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

## License

TBD
