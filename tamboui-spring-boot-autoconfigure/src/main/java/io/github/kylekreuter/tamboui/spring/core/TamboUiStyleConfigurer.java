package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.css.engine.StyleEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * Configures TamboUI styling by creating a {@link StyleEngine} with the utility CSS
 * and registering it with the {@link dev.tamboui.toolkit.app.ToolkitRunner}.
 * <p>
 * Implements {@link SmartInitializingSingleton} to ensure all beans (including the
 * {@link UtilityCssLoader} and {@link TamboSpringApp}) are fully initialized before
 * registering the CSS loader callback.
 * <p>
 * The actual StyleEngine setup happens when the {@link dev.tamboui.toolkit.app.ToolkitRunner}
 * becomes ready (via {@link TamboSpringApp#onRunnerReady}), which is the correct
 * lifecycle point to configure the runner's style engine.
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
     * Registers a runner-ready callback that creates a {@link StyleEngine},
     * loads the utility CSS into it, and sets it on the ToolkitRunner.
     */
    @Override
    public void afterSingletonsInstantiated() {
        log.debug("Registering utility CSS loader callback with TamboSpringApp");
        tamboSpringApp.onRunnerReady(runner -> {
            StyleEngine styleEngine = StyleEngine.create();
            utilityCssLoader.loadInto(styleEngine);
            runner.styleEngine(styleEngine);
            log.info("StyleEngine configured and registered with ToolkitRunner");
        });
    }
}
