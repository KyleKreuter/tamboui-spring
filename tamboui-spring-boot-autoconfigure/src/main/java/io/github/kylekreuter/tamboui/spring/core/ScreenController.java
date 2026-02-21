package io.github.kylekreuter.tamboui.spring.core;

/**
 * Interface for TamboUI screen controllers.
 * <p>
 * Implement this interface in classes annotated with
 * {@link io.github.kylekreuter.tamboui.spring.annotation.TamboScreen @TamboScreen}
 * to populate the template model each frame.
 */
public interface ScreenController {

    /**
     * Populate the model with data for the current frame.
     * Called by the rendering pipeline before template evaluation.
     *
     * @param model the template model to populate
     */
    void populate(TemplateModel model);
}
