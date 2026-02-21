package io.github.kylekreuter.tamboui.spring.core;

import java.util.function.Consumer;

import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.toolkit.app.ToolkitRunner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link TamboUiStyleConfigurer}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TamboUiStyleConfigurerTest {

    @Mock
    private UtilityCssLoader mockCssLoader;

    @Mock
    private TamboSpringApp mockTamboSpringApp;

    @Mock
    private ToolkitRunner mockToolkitRunner;

    @Mock
    private StyleEngine mockStyleEngine;

    @Captor
    private ArgumentCaptor<Consumer<ToolkitRunner>> callbackCaptor;

    private TamboUiStyleConfigurer configurer;

    @BeforeEach
    void setUp() {
        configurer = new TamboUiStyleConfigurer(mockCssLoader, mockTamboSpringApp);
    }

    @Test
    @DisplayName("afterSingletonsInstantiated should register a runner-ready callback")
    void shouldRegisterRunnerReadyCallback() {
        configurer.afterSingletonsInstantiated();

        verify(mockTamboSpringApp).onRunnerReady(callbackCaptor.capture());
    }

    @Test
    @DisplayName("runner-ready callback should create StyleEngine, load CSS, and set on runner")
    void callbackShouldCreateStyleEngineAndLoadCss() {
        try (MockedStatic<StyleEngine> mockedStyleEngine = mockStatic(StyleEngine.class)) {
            mockedStyleEngine.when(StyleEngine::create).thenReturn(mockStyleEngine);

            configurer.afterSingletonsInstantiated();

            verify(mockTamboSpringApp).onRunnerReady(callbackCaptor.capture());

            // Simulate runner becoming ready
            Consumer<ToolkitRunner> callback = callbackCaptor.getValue();
            callback.accept(mockToolkitRunner);

            // Verify StyleEngine was created, CSS was loaded, and engine was set on runner
            mockedStyleEngine.verify(StyleEngine::create);
            verify(mockCssLoader).loadInto(mockStyleEngine);
            verify(mockToolkitRunner).styleEngine(mockStyleEngine);
        }
    }

    @Test
    @DisplayName("should not interact with CSS loader before callback is invoked")
    void shouldNotInteractWithCssLoaderBeforeCallback() {
        configurer.afterSingletonsInstantiated();

        // CSS loader should not be called until the runner is ready
        verifyNoInteractions(mockCssLoader);
    }
}
