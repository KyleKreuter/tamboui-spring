package io.github.kylekreuter.tamboui.spring.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.element.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * Spring lifecycle bean that manages the TamboUI application.
 * <p>
 * Starts the TUI rendering loop when the Spring context is ready
 * and shuts it down when the context closes.
 * <p>
 * The rendering loop runs on a dedicated daemon thread because
 * {@link ToolkitRunner#run(Supplier)} blocks until the runner is
 * stopped. This allows the Spring context to continue operating
 * while the TUI is active.
 */
public class TamboSpringApp implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(TamboSpringApp.class);

    /**
     * Maximum time in seconds to wait for the rendering thread to
     * terminate during {@link #stop()}.
     */
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ToolkitRunnerFactory runnerFactory;
    private final Supplier<Element> rootElementSupplier;
    private final List<Consumer<ToolkitRunner>> runnerReadyCallbacks = new ArrayList<>();

    private volatile boolean running = false;
    private final AtomicReference<ToolkitRunner> runnerRef = new AtomicReference<>();
    private final AtomicReference<Thread> renderThreadRef = new AtomicReference<>();

    /**
     * Creates a new TamboSpringApp with a runner factory and root element supplier.
     *
     * @param runnerFactory      factory that creates {@link ToolkitRunner} instances
     * @param rootElementSupplier supplier called each frame to build the UI element tree;
     *                            if {@code null}, a default placeholder is used
     */
    public TamboSpringApp(ToolkitRunnerFactory runnerFactory,
                          Supplier<Element> rootElementSupplier) {
        this.runnerFactory = runnerFactory;
        this.rootElementSupplier = rootElementSupplier != null
                ? rootElementSupplier
                : () -> Toolkit.text("TamboUI Spring Boot — no screen configured");
    }

    /**
     * Creates a new TamboSpringApp with a runner factory and a default placeholder UI.
     *
     * @param runnerFactory factory that creates {@link ToolkitRunner} instances
     */
    public TamboSpringApp(ToolkitRunnerFactory runnerFactory) {
        this(runnerFactory, null);
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        log.info("Starting TamboUI rendering loop");
        running = true;

        CountDownLatch startedLatch = new CountDownLatch(1);

        Thread renderThread = new Thread(() -> {
            try (ToolkitRunner runner = runnerFactory.create()) {
                runnerRef.set(runner);
                notifyRunnerReady(runner);
                startedLatch.countDown();
                runner.run(rootElementSupplier);
            } catch (Exception e) {
                log.error("TamboUI rendering loop failed", e);
            } finally {
                runnerRef.set(null);
                running = false;
                startedLatch.countDown(); // Ensure latch is released even on failure
                log.info("TamboUI rendering loop stopped");
            }
        }, "tamboui-render");
        renderThread.setDaemon(true);
        renderThread.start();
        renderThreadRef.set(renderThread);

        try {
            boolean started = startedLatch.await(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!started) {
                log.warn("TamboUI rendering loop did not start within {} seconds", SHUTDOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for TamboUI to start");
        }
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }

        log.info("Stopping TamboUI rendering loop");

        ToolkitRunner runner = runnerRef.get();
        if (runner != null) {
            runner.quit();
        }

        Thread renderThread = renderThreadRef.get();
        if (renderThread != null) {
            try {
                renderThread.join(TimeUnit.SECONDS.toMillis(SHUTDOWN_TIMEOUT_SECONDS));
                if (renderThread.isAlive()) {
                    log.warn("TamboUI render thread did not terminate within {} seconds, interrupting",
                            SHUTDOWN_TIMEOUT_SECONDS);
                    renderThread.interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for TamboUI render thread to stop");
            }
        }

        renderThreadRef.set(null);
        running = false;
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

    /**
     * Invokes all registered runner-ready callbacks and clears the list.
     */
    private void notifyRunnerReady(ToolkitRunner runner) {
        for (Consumer<ToolkitRunner> callback : runnerReadyCallbacks) {
            try {
                callback.accept(runner);
            } catch (Exception e) {
                log.error("Error in runner-ready callback", e);
            }
        }
        runnerReadyCallbacks.clear();
    }

    /**
     * Returns the active {@link ToolkitRunner}, or {@code null} if the app is not running.
     *
     * @return the active runner, or null
     */
    public ToolkitRunner getRunner() {
        return runnerRef.get();
    }

    /**
     * Registers a callback that will be invoked once the {@link ToolkitRunner}
     * has been created and is ready to accept event handlers.
     * <p>
     * If the runner is already available, the callback is invoked immediately.
     *
     * @param callback the callback receiving the ready runner
     */
    public void onRunnerReady(Consumer<ToolkitRunner> callback) {
        ToolkitRunner current = runnerRef.get();
        if (current != null) {
            callback.accept(current);
        } else {
            runnerReadyCallbacks.add(callback);
        }
    }
}
