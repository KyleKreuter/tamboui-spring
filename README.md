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
        model.put("title", "My App")
             .put("status", "Running");
    }

    @OnKey("q")
    public void onQuit() {
        System.exit(0);
    }
}
```

Create a template (`src/main/resources/templates/dashboard.ttl`):

```xml
<t:dock>
    <t:panel region="top" height="3" title="${title}" border-type="rounded">
        <t:row>
            <t:text t:text="${title}" class="bold text-cyan" />
            <t:spacer />
            <t:text t:text="[q] Quit" class="dim" />
        </t:row>
    </t:panel>
    <t:panel region="center" border-type="rounded">
        <t:text t:text="Status: ${status}" class="text-green" />
    </t:panel>
</t:dock>
```

That's it — `@TamboScreen` beans are auto-discovered and the TUI starts with the Spring context.

## Features

### Layouts

Build complex layouts with `<t:row>`, `<t:column>`, `<t:grid>`, `<t:dock>`, and `<t:spacer>`:

```xml
<!-- 5-region dock layout -->
<t:dock>
    <t:panel region="top" height="3" title="Header" border-type="rounded" />
    <t:panel region="left" width="20" title="Sidebar" border-type="rounded" />
    <t:panel region="center" title="Content" border-type="rounded">
        <t:column spacing="1">
            <t:text t:text="Line 1" />
            <t:text t:text="Line 2" />
        </t:column>
    </t:panel>
</t:dock>

<!-- CSS-Grid with areas -->
<t:grid grid-size="2" gutter="1">
    <t:panel title="Card 1" border-type="rounded">
        <t:text t:text="Content" />
    </t:panel>
    <t:panel title="Card 2" border-type="rounded">
        <t:text t:text="Content" />
    </t:panel>
</t:grid>
```

### Data Widgets

Display data with `<t:list>` and `<t:table>`:

```xml
<!-- List with static items -->
<t:list>
    <t:item>Home</t:item>
    <t:item>Settings</t:item>
    <t:item>About</t:item>
</t:list>

<!-- Table with column definitions -->
<t:table highlight-color="cyan">
    <t:col header="Name" width="20" />
    <t:col header="Status" width="10" />
</t:table>
```

### Template Directives

Use `<t:if>`, `<t:each>`, and `<t:include>` for dynamic templates:

```xml
<!-- Conditional rendering -->
<t:if test="${isLoggedIn}">
    <t:text t:text="Welcome, ${username}!" />
</t:if>

<!-- Iteration -->
<t:column>
    <t:each items="${users}" var="user" index="i">
        <t:text t:text="${i + 1}. ${user.name}" />
    </t:each>
</t:column>

<!-- Reusable fragments -->
<t:include template="statusbar" />
```

### Forms & Two-Way Binding

Bind mutable state objects to form elements:

```java
@TamboScreen(template = "settings")
public class SettingsController implements ScreenController {

    private final FormState formState = FormState.builder()
            .textField("username", "")
            .textField("email", "")
            .booleanField("darkMode", true)
            .build();

    @Override
    public void onMount() {
        // one-time initialization on first navigation
    }

    @Override
    public void populate(TemplateModel model) {
        model.bindState("settingsForm", formState);
    }

    @OnSubmit("settingsForm")
    public void onSave(FormState state) {
        String username = state.textValue("username");
        boolean darkMode = state.booleanValue("darkMode");
        // process form data
    }
}
```

```xml
<t:form bind="settingsForm">
    <t:input field="username" placeholder="Username" />
    <t:input field="email" placeholder="Email" />
</t:form>
```

State objects bound via `bindState()` survive frame cycles — unlike regular `put()` attributes which are cleared each frame.

### Keyboard Events

Bind methods to key presses with `@OnKey`. Handlers on `@TamboScreen` beans are screen-scoped; handlers on regular `@Component` beans fire globally:

```java
@TamboScreen(template = "editor")
public class EditorController implements ScreenController {

    @OnKey("ctrl+s")
    public void onSave() {
        // only fires when this screen is active
    }

    @OnKey("esc")
    public void onBack(KeyEvent event) {
        router.navigateTo("dashboard");
    }

    // ...
}
```

```java
@Component
public class GlobalShortcuts {

    @OnKey("ctrl+q")
    public void onQuit() {
        // fires on any screen
        System.exit(0);
    }
}
```

### Screen Navigation & Lifecycle

Navigate between screens via `NavigationRouter`. Controllers can hook into the screen lifecycle:

```java
@TamboScreen(template = "profile")
public class ProfileController implements ScreenController {

    @Autowired
    private NavigationRouter router;

    @Override
    public void onMount() { }       // first navigation only

    @Override
    public void onActivate() { }    // every activation

    @Override
    public void onDeactivate() { }  // when leaving the screen

    @Override
    public void onUnmount() { }     // on shutdown / unregistration

    @Override
    public void populate(TemplateModel model) {
        model.put("screens", router.getRegisteredScreens());
    }

    @OnKey("tab")
    public void switchScreen() {
        router.navigateTo("settings");
    }
}
```

## Template Tags

| Tag | Description | Container |
|-----|-------------|-----------|
| `<t:text>` | Text element | No |
| `<t:panel>` | Container with border and title | Yes |
| `<t:row>` | Horizontal layout | Yes |
| `<t:column>` | Vertical layout | Yes |
| `<t:grid>` | CSS-Grid layout | Yes |
| `<t:dock>` | 5-region layout (top/bottom/left/right/center) | Yes |
| `<t:spacer>` | Invisible flex filler | No |
| `<t:list>` | Scrollable, selectable list | Yes |
| `<t:table>` | Table with column definitions | Yes |
| `<t:col>` | Column definition inside `<t:table>` | No |
| `<t:item>` | Item inside `<t:list>` | No |
| `<t:form>` | Form container with state binding | Yes |
| `<t:input>` | Text input field | No |
| `<t:if>` | Conditional rendering (directive) | Yes |
| `<t:each>` | Iteration over collections (directive) | Yes |
| `<t:include>` | Include another template (directive) | No |

## Configuration

```properties
# application.properties
tamboui.backend=jline3                              # terminal backend
tamboui.template-prefix=templates/                  # template location
tamboui.template-suffix=.ttl                        # template file extension
tamboui.default-screen=dashboard                    # initial screen (optional)
tamboui.utility-css=META-INF/tamboui-spring/utility.tcss  # utility stylesheet
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
./mvnw clean install
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

MIT License — see [LICENSE](LICENSE) for details.
