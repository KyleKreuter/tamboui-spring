# Contributing to tamboui-spring

Thanks for taking the time to contribute! This guide explains how to set up your environment and follow project conventions.

## Prerequisites

- `git` command
- Java 25+
- Maven 3.8+ (use `./mvnw`)
- Spring Boot 4.0.2

## Setup

```bash
git clone --recurse-submodules https://github.com/KyleKreuter/tamboui-spring.git
cd tamboui-spring
mvn clean install
```

## IDEs

IntelliJ IDEA is known to work with the tamboui-spring project.

## Build and test

- Build: `mvn clean install`
- Test: `mvn test`

## Code style and conventions

- Use JUnit 5 for tests.
- Add Javadoc for all public APIs.
- Code and comments in English.

## Submitting changes

- Keep changes focused and minimal.
- Ensure tests pass.
- Write clear commit messages that explain *why*, not just *what*.
- Push your branch to your fork and open a PR against `main`.
- Describe what the PR does and why, and link related issues if applicable.

## Reporting Issues

- Use GitHub Issues for bug reports and feature requests.
- Include steps to reproduce for bugs.
- Provide context on what you expected vs. what happened.

## Coding Agents/LLM usage

This project is built with heavy use of AI tools and I fully encourage contributors to do the same. Use whatever tools make you productive — ChatGPT, GitHub Copilot, Claude, Cursor, or any other AI assistant.

The only rule: **understand what you submit.** If you can explain your code and defend your design decisions, I don't care how it was written.

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).
