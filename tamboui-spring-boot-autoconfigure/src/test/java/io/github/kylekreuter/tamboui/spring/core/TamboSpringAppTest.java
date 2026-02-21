package io.github.kylekreuter.tamboui.spring.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.element.Element;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TamboSpringApp}.
 * <p>
 * Uses Mockito to mock the {@link ToolkitRunner} (final class, requires
 * mockito-inline which is the default mock maker in modern Mockito versions).
 */
@ExtendWith(MockitoExtension.class)
class TamboSpringAppTest {

    @Mock
    private ToolkitRunner mockRunner;

    @Mock
    private ToolkitRunnerFactory mockFactory;

    private TamboSpringApp app;

    @BeforeEach
    void setUp() throws Exception {
        when(mockFactory.create()).thenReturn(mockRunner);
    }

    @Test
    @DisplayName("start() should set isRunning to true and start rendering thread")
    void startShouldSetRunningAndStartRenderThread() throws Exception {
        // Simulate a blocking run() that terminates when quit() is called
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        assertThat(app.isRunning()).isFalse();

        app.start();

        assertThat(app.isRunning()).isTrue();
        verify(mockFactory).create();
        verify(mockRunner, timeout(2000)).run(any(Supplier.class));

        // Clean up
        runLatch.countDown();
        app.stop();
    }

    @Test
    @DisplayName("stop() should call quit() on the runner and set isRunning to false")
    void stopShouldCallQuitAndSetNotRunning() throws Exception {
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        app.start();

        assertThat(app.isRunning()).isTrue();

        // Release the blocking run
        runLatch.countDown();
        app.stop();

        verify(mockRunner).quit();
        // Wait briefly for the daemon thread to finish
        Thread.sleep(200);
        assertThat(app.isRunning()).isFalse();
    }

    @Test
    @DisplayName("stop() should be a no-op when not running")
    void stopWhenNotRunningShouldBeNoop() {
        app = new TamboSpringApp(mockFactory);
        app.stop();

        assertThat(app.isRunning()).isFalse();
        verify(mockRunner, never()).quit();
    }

    @Test
    @DisplayName("start() should be idempotent when already running")
    void startWhenAlreadyRunningShouldBeIdempotent() throws Exception {
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        app.start();
        app.start(); // Second call should be a no-op

        // Factory should only be called once
        verify(mockFactory).create();

        runLatch.countDown();
        app.stop();
    }

    @Test
    @DisplayName("getPhase() should return Integer.MAX_VALUE - 1")
    void getPhaseShouldReturnMaxValueMinusOne() {
        app = new TamboSpringApp(mockFactory);
        assertThat(app.getPhase()).isEqualTo(Integer.MAX_VALUE - 1);
    }

    @Test
    @DisplayName("getRunner() should return the active runner while running")
    void getRunnerShouldReturnActiveRunnerWhileRunning() throws Exception {
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        assertThat(app.getRunner()).isNull();

        app.start();

        assertThat(app.getRunner()).isSameAs(mockRunner);

        runLatch.countDown();
        app.stop();
    }

    @Test
    @DisplayName("getRunner() should return null after stop")
    void getRunnerShouldReturnNullAfterStop() throws Exception {
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        app.start();
        runLatch.countDown();
        app.stop();

        // Wait for thread cleanup
        Thread.sleep(200);
        assertThat(app.getRunner()).isNull();
    }

    @Test
    @DisplayName("close() should be called on runner when rendering loop ends")
    void closeShouldBeCalledOnRunnerWhenLoopEnds() throws Exception {
        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory);
        app.start();

        runLatch.countDown();
        app.stop();
        Thread.sleep(200);

        verify(mockRunner).close();
    }

    @Test
    @DisplayName("start() should handle runner creation failure gracefully")
    void startShouldHandleCreationFailure() throws Exception {
        when(mockFactory.create()).thenThrow(new RuntimeException("Terminal init failed"));

        app = new TamboSpringApp(mockFactory);
        app.start();

        // Wait for the render thread to fail and clean up
        Thread.sleep(500);

        assertThat(app.isRunning()).isFalse();
        assertThat(app.getRunner()).isNull();
    }

    @Test
    @DisplayName("custom root element supplier should be passed to runner.run()")
    void customRootElementSupplierShouldBePassedToRun() throws Exception {
        AtomicBoolean supplierCalled = new AtomicBoolean(false);
        Supplier<Element> customSupplier = () -> {
            supplierCalled.set(true);
            return null;
        };

        CountDownLatch runLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            // Call the supplier to verify it's the custom one
            @SuppressWarnings("unchecked")
            Supplier<Element> supplier = invocation.getArgument(0);
            supplier.get();
            runLatch.await(5, TimeUnit.SECONDS);
            return null;
        }).when(mockRunner).run(any(Supplier.class));

        app = new TamboSpringApp(mockFactory, customSupplier);
        app.start();

        // Give the render thread time to call run with the supplier
        Thread.sleep(300);
        assertThat(supplierCalled.get()).isTrue();

        runLatch.countDown();
        app.stop();
    }
}
