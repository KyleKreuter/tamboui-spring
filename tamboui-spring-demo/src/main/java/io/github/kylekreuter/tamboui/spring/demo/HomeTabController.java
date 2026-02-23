package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;

/**
 * Home tab (index 0) — welcome text and keyboard shortcut reference.
 */
@TamboScreen(value = "home", template = "home")
public class HomeTabController extends AbstractTabController {

    public HomeTabController(TamboSpringApp tamboSpringApp,
                              NavigationRouter navigationRouter,
                              TabNavigationState tabNavigationState) {
        super(0, tamboSpringApp, navigationRouter, tabNavigationState);
    }

    @Override
    protected int getTabIndex() {
        return 0;
    }
}
