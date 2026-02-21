package io.github.kylekreuter.tamboui.spring.core;

/**
 * Routes navigation between TamboUI screens.
 * <p>
 * Manages the active screen and handles transitions between
 * {@link io.github.kylekreuter.tamboui.spring.annotation.TamboScreen @TamboScreen} controllers.
 */
public class NavigationRouter {

    private String activeScreen;

    /**
     * Navigate to the screen with the given name.
     *
     * @param screenName the target screen name
     */
    public void navigateTo(String screenName) {
        this.activeScreen = screenName;
        // TODO: Trigger screen transition via TamboUI
    }

    /**
     * Return the name of the currently active screen.
     */
    public String getActiveScreen() {
        return activeScreen;
    }
}
