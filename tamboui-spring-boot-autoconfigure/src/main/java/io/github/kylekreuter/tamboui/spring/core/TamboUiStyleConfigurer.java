package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.css.StyleEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * Configures TamboUI styling by loading the utility CSS into the {@link StyleEngine}
 * after all singleton beans have been initialized.
 * <p>
 * Implements {@link SmartInitializingSingleton} to ensure all beans (including the
 * {@link UtilityCssLoader} and {@link TamboSpringApp}) are fully initialized before
 * registering the CSS loader callback.
 * <p>
 * The actual CSS loading happens when the {@link dev.tamboui.toolkit.app.ToolkitRunner}
 * becomes ready (via {@link TamboSpringApp#onRunnerReady}), ensuring the
 * {@link StyleEngine} is available from the runner at load time.
 *
 * @see UtilityCssLoader
 * @see TamboSpringApp
 * @see StyleEngine
 */
public class TamboUiStyleConfigurer implements SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(TamboUiStyleConfigurer.class);

    private final UtilityCssLoader utilityCssLoader;
    private final TamboSpringApp tamboSpringApp;

    /**
     * Creates a new TamboUiStyleConfigurer.
     *
     * @param utilityCssLoader the loader providing the utility CSS content
     * @param tamboSpringApp   the TamboUI Spring application for runner-ready callbacks
     */
    public TamboUiStyleConfigurer(UtilityCssLoader utilityCssLoader, TamboSpringApp tamboSpringApp) {
        this.utilityCssLoader = utilityCssLoader;
        this.tamboSpringApp = tamboSpringApp;
    }

    /**
     * Called after all singleton beans have been initialized.
     * Registers a runner-ready callback to load the utility CSS into the
     * runner's {@link StyleEngine}.
     */
    @Override
    public void afterSingletonsInstantiated() {
        log.debug("Registering utility CSS loader callback with TamboSpringApp");
        tamboSpringApp.onRunnerReady(runner -> {
            StyleEngine styleEngine = runner.styleEngine();
            if (styleEngine != null) {
                utilityCssLoader.loadInto(styleEngine);
            } else {
                log.warn("ToolkitRunner has no StyleEngine available, utility CSS will not be loaded");
            }
        });
    }
}
