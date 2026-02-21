package io.github.kylekreuter.tamboui.spring.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;

/**
 * Routes navigation between TamboUI screens.
 * <p>
 * Manages the active screen, resolves {@link ScreenController} instances by their
 * {@link TamboScreen @TamboScreen} name, and notifies registered listeners when
 * a screen transition occurs. This enables the rendering pipeline to switch to
 * the new screen's template and controller.
 *
 * <pre>{@code
 * // Inject and navigate
 * @Autowired NavigationRouter router;
 * router.navigateTo("settings");
 * }</pre>
 */
public class NavigationRouter {

    private volatile String activeScreen;

    private final Map<String, ScreenController> screenControllers = new ConcurrentHashMap<>();
    private final Map<String, String> screenTemplates = new ConcurrentHashMap<>();
    private final List<ScreenChangeListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Listener that is notified when the active screen changes.
     */
    @FunctionalInterface
    public interface ScreenChangeListener {

        /**
         * Called when the active screen changes.
         *
         * @param previousScreen the name of the previous screen, or {@code null} if this is the initial navigation
         * @param newScreen      the name of the new active screen
         * @param controller     the resolved {@link ScreenController} for the new screen
         * @param templateName   the template name associated with the new screen
         */
        void onScreenChange(String previousScreen, String newScreen,
                            ScreenController controller, String templateName);
    }

    /**
     * Register a {@link ScreenController} with the given screen name and template.
     * <p>
     * Typically called during auto-configuration to register all beans
     * annotated with {@link TamboScreen @TamboScreen}.
     *
     * @param screenName   the logical screen name (from {@code @TamboScreen} value or bean name)
     * @param controller   the screen controller instance
     * @param templateName the template name from {@code @TamboScreen.template()}
     * @throws IllegalArgumentException if any argument is {@code null} or empty
     */
    public void registerScreen(String screenName, ScreenController controller, String templateName) {
        Objects.requireNonNull(screenName, "screenName must not be null");
        Objects.requireNonNull(controller, "controller must not be null");
        Objects.requireNonNull(templateName, "templateName must not be null");
        if (screenName.isBlank()) {
            throw new IllegalArgumentException("screenName must not be blank");
        }
        if (templateName.isBlank()) {
            throw new IllegalArgumentException("templateName must not be blank");
        }
        screenControllers.put(screenName, controller);
        screenTemplates.put(screenName, templateName);
    }

    /**
     * Navigate to the screen with the given name.
     * <p>
     * Sets the active screen, resolves the associated {@link ScreenController},
     * and notifies all registered {@link ScreenChangeListener}s. The rendering
     * pipeline (typically {@link TamboSpringApp}) reacts to this event by
     * switching to the new screen's template.
     *
     * @param screenName the target screen name
     * @throws IllegalArgumentException if {@code screenName} is {@code null} or blank
     * @throws IllegalStateException    if no screen is registered under the given name
     */
    public void navigateTo(String screenName) {
        Objects.requireNonNull(screenName, "screenName must not be null");
        if (screenName.isBlank()) {
            throw new IllegalArgumentException("screenName must not be blank");
        }

        ScreenController controller = screenControllers.get(screenName);
        if (controller == null) {
            throw new IllegalStateException(
                    "No screen registered with name '" + screenName + "'. "
                    + "Available screens: " + screenControllers.keySet());
        }

        String templateName = screenTemplates.get(screenName);
        String previousScreen = this.activeScreen;
        this.activeScreen = screenName;

        for (ScreenChangeListener listener : listeners) {
            listener.onScreenChange(previousScreen, screenName, controller, templateName);
        }
    }

    /**
     * Return the name of the currently active screen.
     *
     * @return the active screen name, or {@code null} if no navigation has occurred
     */
    public String getActiveScreen() {
        return activeScreen;
    }

    /**
     * Return the {@link ScreenController} for the currently active screen.
     *
     * @return the active controller, or {@code null} if no screen is active
     */
    public ScreenController getActiveController() {
        String current = activeScreen;
        return current != null ? screenControllers.get(current) : null;
    }

    /**
     * Return the template name for the currently active screen.
     *
     * @return the active template name, or {@code null} if no screen is active
     */
    public String getActiveTemplateName() {
        String current = activeScreen;
        return current != null ? screenTemplates.get(current) : null;
    }

    /**
     * Return the {@link ScreenController} registered under the given screen name.
     *
     * @param screenName the screen name
     * @return the controller, or {@code null} if not found
     */
    public ScreenController getController(String screenName) {
        return screenControllers.get(screenName);
    }

    /**
     * Check whether a screen with the given name is registered.
     *
     * @param screenName the screen name to check
     * @return {@code true} if a screen is registered with this name
     */
    public boolean hasScreen(String screenName) {
        return screenControllers.containsKey(screenName);
    }

    /**
     * Return an unmodifiable view of all registered screen names.
     *
     * @return the set of registered screen names
     */
    public java.util.Set<String> getRegisteredScreens() {
        return java.util.Collections.unmodifiableSet(screenControllers.keySet());
    }

    /**
     * Add a listener that is notified when the active screen changes.
     *
     * @param listener the listener to add
     */
    public void addScreenChangeListener(ScreenChangeListener listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.add(listener);
    }

    /**
     * Remove a previously registered screen change listener.
     *
     * @param listener the listener to remove
     */
    public void removeScreenChangeListener(ScreenChangeListener listener) {
        listeners.remove(listener);
    }
}
