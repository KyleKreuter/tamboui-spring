package io.github.kylekreuter.tamboui.spring.demo;

import org.springframework.stereotype.Component;

/**
 * Shared state bean that tracks the last active tab screen name.
 * Used by {@link DialogShowcaseController} to navigate back to the correct tab.
 */
@Component
public class TabNavigationState {

    private String activeTabScreen = "home";

    public String getActiveTabScreen() {
        return activeTabScreen;
    }

    public void setActiveTabScreen(String activeTabScreen) {
        this.activeTabScreen = activeTabScreen;
    }
}
