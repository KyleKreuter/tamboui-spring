package io.github.kylekreuter.tamboui.spring.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.toolkit.event.EventRouter;
import dev.tamboui.toolkit.event.GlobalEventHandler;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.KeyModifiers;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OnKeyRegistrar}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OnKeyRegistrarTest {

    @Mock
    private TamboSpringApp tamboSpringApp;

    @Mock
    private ToolkitRunner toolkitRunner;

    @Mock
    private EventRouter eventRouter;

    @Mock
    private NavigationRouter navigationRouter;

    private OnKeyRegistrar registrar;

    @BeforeEach
    void setUp() {
        when(toolkitRunner.eventRouter()).thenReturn(eventRouter);
        registrar = new OnKeyRegistrar(tamboSpringApp, navigationRouter);
    }

    @Test
    @DisplayName("should discover @OnKey-annotated methods on beans")
    void shouldDiscoverAnnotatedMethods() {
        SampleBean bean = new SampleBean();

        registrar.postProcessAfterInitialization(bean, "sampleBean");

        assertThat(registrar.hasBindings()).isTrue();
        assertThat(registrar.getBindings()).hasSize(1);
    }

    @Test
    @DisplayName("should discover multiple @OnKey methods on a single bean")
    void shouldDiscoverMultipleAnnotatedMethods() {
        MultiKeyBean bean = new MultiKeyBean();

        registrar.postProcessAfterInitialization(bean, "multiKeyBean");

        assertThat(registrar.getBindings()).hasSize(2);
    }

    @Test
    @DisplayName("should not discover beans without @OnKey methods")
    void shouldIgnoreBeansWithoutAnnotation() {
        PlainBean bean = new PlainBean();

        registrar.postProcessAfterInitialization(bean, "plainBean");

        assertThat(registrar.hasBindings()).isFalse();
    }

    @Test
    @DisplayName("should throw on invalid @OnKey value")
    void shouldThrowOnInvalidKeyValue() {
        InvalidKeyBean bean = new InvalidKeyBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "invalidKeyBean"))
                .isInstanceOf(org.springframework.beans.BeansException.class)
                .hasMessageContaining("Invalid @OnKey value");
    }

    @Test
    @DisplayName("should throw on method with too many parameters")
    void shouldThrowOnMethodWithTooManyParams() {
        TooManyParamsBean bean = new TooManyParamsBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "tooManyParams"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("zero parameters or a single KeyEvent parameter");
    }

    @Test
    @DisplayName("should throw on method with wrong parameter type")
    void shouldThrowOnWrongParamType() {
        WrongParamTypeBean bean = new WrongParamTypeBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "wrongParam"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be of type KeyEvent");
    }

    @Test
    @DisplayName("should register handlers on EventRouter when runner is ready")
    void shouldRegisterHandlersOnEventRouter() {
        SampleBean bean = new SampleBean();
        registrar.postProcessAfterInitialization(bean, "sampleBean");

        registrar.registerHandlers(eventRouter);

        verify(eventRouter).addGlobalHandler(org.mockito.ArgumentMatchers.any(GlobalEventHandler.class));
    }

    @Test
    @DisplayName("should invoke no-arg method when matching key event is received")
    void shouldInvokeNoArgMethodOnMatch() {
        SampleBean bean = new SampleBean();
        registrar.postProcessAfterInitialization(bean, "sampleBean");

        // Capture the registered handler
        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        verify(eventRouter).addGlobalHandler(captor.capture());

        GlobalEventHandler handler = captor.getValue();

        // Simulate a 'q' key event
        KeyEvent event = KeyEvent.ofChar('q');
        EventResult result = handler.handle(event);

        assertThat(result).isEqualTo(EventResult.HANDLED);
        assertThat(bean.qPressed).isTrue();
    }

    @Test
    @DisplayName("should not invoke method when non-matching key event is received")
    void shouldNotInvokeOnNonMatch() {
        SampleBean bean = new SampleBean();
        registrar.postProcessAfterInitialization(bean, "sampleBean");

        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        verify(eventRouter).addGlobalHandler(captor.capture());

        GlobalEventHandler handler = captor.getValue();

        // Simulate a different key event
        KeyEvent event = KeyEvent.ofChar('x');
        EventResult result = handler.handle(event);

        assertThat(result).isEqualTo(EventResult.UNHANDLED);
        assertThat(bean.qPressed).isFalse();
    }

    @Test
    @DisplayName("should invoke method with KeyEvent parameter when matching")
    void shouldInvokeMethodWithKeyEventParam() {
        KeyEventParamBean bean = new KeyEventParamBean();
        registrar.postProcessAfterInitialization(bean, "keyEventParamBean");

        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        verify(eventRouter).addGlobalHandler(captor.capture());

        GlobalEventHandler handler = captor.getValue();

        KeyEvent event = KeyEvent.ofKey(KeyCode.ESCAPE);
        EventResult result = handler.handle(event);

        assertThat(result).isEqualTo(EventResult.HANDLED);
        assertThat(bean.receivedEvent).isSameAs(event);
    }

    @Test
    @DisplayName("should return UNHANDLED for non-KeyEvent events")
    void shouldReturnUnhandledForNonKeyEvents() {
        SampleBean bean = new SampleBean();
        registrar.postProcessAfterInitialization(bean, "sampleBean");

        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        verify(eventRouter).addGlobalHandler(captor.capture());

        GlobalEventHandler handler = captor.getValue();

        // Simulate a non-key event (e.g., a mock Event)
        dev.tamboui.tui.event.Event nonKeyEvent = mock(dev.tamboui.tui.event.Event.class);
        EventResult result = handler.handle(nonKeyEvent);

        assertThat(result).isEqualTo(EventResult.UNHANDLED);
    }

    @Test
    @DisplayName("getPhase() should return Integer.MAX_VALUE - 2")
    void getPhaseShouldReturnCorrectValue() {
        assertThat(registrar.getPhase()).isEqualTo(Integer.MAX_VALUE - 2);
    }

    @Test
    @DisplayName("postProcessAfterInitialization should return the same bean instance")
    void shouldReturnSameBeanInstance() {
        SampleBean bean = new SampleBean();

        Object result = registrar.postProcessAfterInitialization(bean, "sampleBean");

        assertThat(result).isSameAs(bean);
    }

    @Test
    @DisplayName("isRunning() should return false initially")
    void isRunningFalseInitially() {
        assertThat(registrar.isRunning()).isFalse();
    }

    @Test
    @DisplayName("start() should do nothing when no bindings exist")
    void startWithNoBindingsShouldDoNothing() {
        registrar.start();

        assertThat(registrar.isRunning()).isFalse();
    }

    @Test
    @DisplayName("stop() should set running to false")
    void stopShouldSetRunningToFalse() {
        registrar.stop();

        assertThat(registrar.isRunning()).isFalse();
    }

    @Test
    @DisplayName("should discover bindings from multiple beans")
    void shouldDiscoverBindingsFromMultipleBeans() {
        SampleBean bean1 = new SampleBean();
        CtrlCBean bean2 = new CtrlCBean();

        registrar.postProcessAfterInitialization(bean1, "sampleBean");
        registrar.postProcessAfterInitialization(bean2, "ctrlCBean");

        assertThat(registrar.getBindings()).hasSize(2);
    }

    @Test
    @DisplayName("should handle multiple bindings dispatching to correct methods")
    void shouldDispatchToCorrectMethodsWithMultipleBindings() {
        SampleBean qBean = new SampleBean();
        CtrlCBean ctrlCBean = new CtrlCBean();

        registrar.postProcessAfterInitialization(qBean, "qBean");
        registrar.postProcessAfterInitialization(ctrlCBean, "ctrlCBean");

        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        // Two bindings -> two addGlobalHandler calls
        verify(eventRouter, org.mockito.Mockito.times(2)).addGlobalHandler(captor.capture());

        java.util.List<GlobalEventHandler> handlers = captor.getAllValues();

        // Simulate 'q' key event -- first handler should match, second should not
        KeyEvent qEvent = KeyEvent.ofChar('q');
        EventResult result1 = handlers.get(0).handle(qEvent);
        EventResult result2 = handlers.get(1).handle(qEvent);

        assertThat(result1).isEqualTo(EventResult.HANDLED);
        assertThat(result2).isEqualTo(EventResult.UNHANDLED);
        assertThat(qBean.qPressed).isTrue();
        assertThat(ctrlCBean.handled).isFalse();

        // Simulate Ctrl+C event -- first handler should not match, second should
        KeyEvent ctrlCEvent = KeyEvent.ofChar('c', KeyModifiers.CTRL);
        EventResult result3 = handlers.get(0).handle(ctrlCEvent);
        EventResult result4 = handlers.get(1).handle(ctrlCEvent);

        assertThat(result3).isEqualTo(EventResult.UNHANDLED);
        assertThat(result4).isEqualTo(EventResult.HANDLED);
        assertThat(ctrlCBean.handled).isTrue();
    }

    @Test
    @DisplayName("hasBindings() should return false initially")
    void hasBindingsFalseInitially() {
        assertThat(registrar.hasBindings()).isFalse();
    }

    @Test
    @DisplayName("getBindings() should return empty list initially")
    void getBindingsEmptyInitially() {
        assertThat(registrar.getBindings()).isEmpty();
    }

    @Test
    @DisplayName("should handle modifier key bindings correctly")
    void shouldHandleModifierBindings() {
        CtrlCBean bean = new CtrlCBean();
        registrar.postProcessAfterInitialization(bean, "ctrlCBean");

        ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
        registrar.registerHandlers(eventRouter);
        verify(eventRouter).addGlobalHandler(captor.capture());

        GlobalEventHandler handler = captor.getValue();

        // Ctrl+C should match
        KeyEvent ctrlC = KeyEvent.ofChar('c', KeyModifiers.CTRL);
        assertThat(handler.handle(ctrlC)).isEqualTo(EventResult.HANDLED);
        assertThat(bean.handled).isTrue();

        // Plain 'c' should not match
        bean.handled = false;
        KeyEvent plainC = KeyEvent.ofChar('c');
        assertThat(handler.handle(plainC)).isEqualTo(EventResult.UNHANDLED);
        assertThat(bean.handled).isFalse();
    }

    // ==================== Screen-Scoping Tests ====================

    @Nested
    @DisplayName("Screen-Scoping")
    class ScreenScopingTests {

        @Test
        @DisplayName("should set screenName on binding for @TamboScreen beans")
        void shouldSetScreenNameForTamboScreenBeans() {
            DashboardScreenBean bean = new DashboardScreenBean();

            registrar.postProcessAfterInitialization(bean, "dashboardScreenBean");

            assertThat(registrar.getBindings()).hasSize(1);
            assertThat(registrar.getBindings().get(0).screenName()).isEqualTo("dashboardScreenBean");
        }

        @Test
        @DisplayName("should use @TamboScreen.value() as screenName when provided")
        void shouldUseTamboScreenValueAsScreenName() {
            NamedScreenBean bean = new NamedScreenBean();

            registrar.postProcessAfterInitialization(bean, "namedScreenBean");

            assertThat(registrar.getBindings()).hasSize(1);
            assertThat(registrar.getBindings().get(0).screenName()).isEqualTo("myDashboard");
        }

        @Test
        @DisplayName("should have null screenName for beans without @TamboScreen")
        void shouldHaveNullScreenNameForRegularBeans() {
            SampleBean bean = new SampleBean();

            registrar.postProcessAfterInitialization(bean, "sampleBean");

            assertThat(registrar.getBindings()).hasSize(1);
            assertThat(registrar.getBindings().get(0).screenName()).isNull();
        }

        @Test
        @DisplayName("should fire screen-scoped handler when screen is active")
        void shouldFireWhenScreenIsActive() {
            DashboardScreenBean bean = new DashboardScreenBean();
            registrar.postProcessAfterInitialization(bean, "dashboard");
            when(navigationRouter.getActiveScreen()).thenReturn("dashboard");

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter).addGlobalHandler(captor.capture());

            GlobalEventHandler handler = captor.getValue();
            KeyEvent event = KeyEvent.ofChar('q');
            EventResult result = handler.handle(event);

            assertThat(result).isEqualTo(EventResult.HANDLED);
            assertThat(bean.handled).isTrue();
        }

        @Test
        @DisplayName("should NOT fire screen-scoped handler when different screen is active")
        void shouldNotFireWhenDifferentScreenActive() {
            DashboardScreenBean bean = new DashboardScreenBean();
            registrar.postProcessAfterInitialization(bean, "dashboard");
            when(navigationRouter.getActiveScreen()).thenReturn("settings");

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter).addGlobalHandler(captor.capture());

            GlobalEventHandler handler = captor.getValue();
            KeyEvent event = KeyEvent.ofChar('q');
            EventResult result = handler.handle(event);

            assertThat(result).isEqualTo(EventResult.UNHANDLED);
            assertThat(bean.handled).isFalse();
        }

        @Test
        @DisplayName("should NOT fire screen-scoped handler when no screen is active")
        void shouldNotFireWhenNoScreenActive() {
            DashboardScreenBean bean = new DashboardScreenBean();
            registrar.postProcessAfterInitialization(bean, "dashboard");
            when(navigationRouter.getActiveScreen()).thenReturn(null);

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter).addGlobalHandler(captor.capture());

            GlobalEventHandler handler = captor.getValue();
            KeyEvent event = KeyEvent.ofChar('q');
            EventResult result = handler.handle(event);

            assertThat(result).isEqualTo(EventResult.UNHANDLED);
            assertThat(bean.handled).isFalse();
        }

        @Test
        @DisplayName("global handlers should fire regardless of active screen")
        void globalHandlersShouldAlwaysFire() {
            SampleBean bean = new SampleBean();
            registrar.postProcessAfterInitialization(bean, "sampleBean");
            when(navigationRouter.getActiveScreen()).thenReturn("anyScreen");

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter).addGlobalHandler(captor.capture());

            GlobalEventHandler handler = captor.getValue();
            KeyEvent event = KeyEvent.ofChar('q');
            EventResult result = handler.handle(event);

            assertThat(result).isEqualTo(EventResult.HANDLED);
            assertThat(bean.qPressed).isTrue();
        }

        @Test
        @DisplayName("global handlers should fire when no screen is active")
        void globalHandlersShouldFireWithoutActiveScreen() {
            SampleBean bean = new SampleBean();
            registrar.postProcessAfterInitialization(bean, "sampleBean");
            when(navigationRouter.getActiveScreen()).thenReturn(null);

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter).addGlobalHandler(captor.capture());

            GlobalEventHandler handler = captor.getValue();
            KeyEvent event = KeyEvent.ofChar('q');
            EventResult result = handler.handle(event);

            assertThat(result).isEqualTo(EventResult.HANDLED);
            assertThat(bean.qPressed).isTrue();
        }

        @Test
        @DisplayName("mixed: screen-scoped and global handlers coexist correctly")
        void mixedScreenScopedAndGlobalHandlers() {
            DashboardScreenBean dashBean = new DashboardScreenBean();
            CtrlCBean globalBean = new CtrlCBean();

            registrar.postProcessAfterInitialization(dashBean, "dashboard");
            registrar.postProcessAfterInitialization(globalBean, "ctrlCBean");
            when(navigationRouter.getActiveScreen()).thenReturn("settings"); // NOT dashboard

            ArgumentCaptor<GlobalEventHandler> captor = ArgumentCaptor.forClass(GlobalEventHandler.class);
            registrar.registerHandlers(eventRouter);
            verify(eventRouter, org.mockito.Mockito.times(2)).addGlobalHandler(captor.capture());

            java.util.List<GlobalEventHandler> handlers = captor.getAllValues();

            // 'q' should NOT fire on dashboard bean (wrong screen)
            KeyEvent qEvent = KeyEvent.ofChar('q');
            assertThat(handlers.get(0).handle(qEvent)).isEqualTo(EventResult.UNHANDLED);
            assertThat(dashBean.handled).isFalse();

            // Ctrl+C should fire on global bean (no screen scoping)
            KeyEvent ctrlCEvent = KeyEvent.ofChar('c', KeyModifiers.CTRL);
            assertThat(handlers.get(1).handle(ctrlCEvent)).isEqualTo(EventResult.HANDLED);
            assertThat(globalBean.handled).isTrue();
        }
    }

    // --- Test beans ---

    static class SampleBean {
        boolean qPressed = false;

        @OnKey("q")
        void onQuit() {
            qPressed = true;
        }
    }

    static class MultiKeyBean {
        @OnKey("q")
        void onQuit() {}

        @OnKey("ctrl+c")
        void onCtrlC() {}
    }

    static class PlainBean {
        void someMethod() {}
    }

    static class InvalidKeyBean {
        @OnKey("ctrl")
        void onInvalid() {}
    }

    static class TooManyParamsBean {
        @OnKey("q")
        void onKey(KeyEvent event, String extra) {}
    }

    static class WrongParamTypeBean {
        @OnKey("q")
        void onKey(String notAnEvent) {}
    }

    static class KeyEventParamBean {
        KeyEvent receivedEvent = null;

        @OnKey("esc")
        void onEscape(KeyEvent event) {
            receivedEvent = event;
        }
    }

    static class CtrlCBean {
        boolean handled = false;

        @OnKey("ctrl+c")
        void onCtrlC() {
            handled = true;
        }
    }

    @TamboScreen(template = "dashboard")
    static class DashboardScreenBean implements ScreenController {
        boolean handled = false;

        @OnKey("q")
        void onQuit() {
            handled = true;
        }

        @Override
        public void populate(TemplateModel model) {}
    }

    @TamboScreen(value = "myDashboard", template = "dashboard")
    static class NamedScreenBean implements ScreenController {
        boolean handled = false;

        @OnKey("q")
        void onQuit() {
            handled = true;
        }

        @Override
        public void populate(TemplateModel model) {}
    }
}
