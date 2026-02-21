package io.github.kylekreuter.tamboui.spring.core;

/**
 * Interface for TamboUI screen controllers.
 * <p>
 * Implement this interface in classes annotated with
 * {@link io.github.kylekreuter.tamboui.spring.annotation.TamboScreen @TamboScreen}
 * to populate the template model each frame.
 * <p>
 * Controllers can override lifecycle hooks to react to screen transitions:
 * <ul>
 *   <li>{@link #onMount()} — called once, the first time a screen is navigated to</li>
 *   <li>{@link #onActivate()} — called every time the screen becomes active</li>
 *   <li>{@link #onDeactivate()} — called every time the screen is left</li>
 *   <li>{@link #onUnmount()} — called when the screen is permanently removed (e.g. shutdown)</li>
 * </ul>
 * All lifecycle hooks have empty default implementations so that existing controllers
 * are not required to implement them.
 */
public interface ScreenController {

    /**
     * Populate the model with data for the current frame.
     * Called by the rendering pipeline before template evaluation.
     *
     * @param model the template model to populate
     */
    void populate(TemplateModel model);

    /**
     * Called once when this screen is navigated to for the first time.
     * <p>
     * Use this hook to perform one-time initialization such as creating form state,
     * loading initial data, or setting up resources that persist across activations.
     */
    default void onMount() {
        // empty default — override to initialize state on first navigation
    }

    /**
     * Called every time this screen becomes the active screen.
     * <p>
     * This is invoked after {@link #onMount()} on the first navigation, and on
     * every subsequent navigation to this screen (e.g. returning from another screen).
     * Use this hook for refreshing data or resuming timers.
     */
    default void onActivate() {
        // empty default — override to react on each activation
    }

    /**
     * Called every time this screen is left (another screen becomes active).
     * <p>
     * Use this hook for pausing background work, warning about unsaved changes,
     * or releasing temporary resources.
     */
    default void onDeactivate() {
        // empty default — override to react when screen is deactivated
    }

    /**
     * Called when this screen is permanently removed from the router
     * (e.g. during application shutdown or explicit unregistration).
     * <p>
     * Use this hook for final cleanup of resources that were allocated in
     * {@link #onMount()}.
     */
    default void onUnmount() {
        // empty default — override for final cleanup
    }
}
