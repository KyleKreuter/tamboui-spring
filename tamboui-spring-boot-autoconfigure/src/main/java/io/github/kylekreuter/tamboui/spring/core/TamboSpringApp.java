package io.github.kylekreuter.tamboui.spring.core;

import org.springframework.context.SmartLifecycle;

/**
 * Spring lifecycle bean that manages the TamboUI application.
 * <p>
 * Starts the TUI rendering loop when the Spring context is ready
 * and shuts it down when the context closes.
 */
public class TamboSpringApp implements SmartLifecycle {

    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
        // TODO: Initialize TamboUI ToolkitRunner and start rendering loop
    }

    @Override
    public void stop() {
        running = false;
        // TODO: Shut down TamboUI ToolkitRunner
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        // Start late, stop early — TUI should be the last thing to start
        return Integer.MAX_VALUE - 1;
    }
}
