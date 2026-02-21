package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.toolkit.app.ToolkitRunner;

/**
 * Factory for creating {@link ToolkitRunner} instances.
 * <p>
 * Extracted as a functional interface to allow testability
 * (the real {@link ToolkitRunner} is a {@code final} class created
 * via static factory methods that require a live terminal).
 * <p>
 * The default implementation delegates to {@link ToolkitRunner#create()}.
 * In tests, provide a mock or stub implementation.
 *
 * @see ToolkitRunner
 * @see TamboSpringApp
 */
@FunctionalInterface
public interface ToolkitRunnerFactory {

    /**
     * Creates a new {@link ToolkitRunner}.
     *
     * @return a configured ToolkitRunner ready to run
     * @throws Exception if terminal initialization fails
     */
    ToolkitRunner create() throws Exception;
}
