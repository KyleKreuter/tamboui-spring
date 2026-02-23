package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;

/**
 * Data tab (index 3) — Table and List widgets.
 */
@TamboScreen(value = "data", template = "data")
public class DataTabController extends AbstractTabController {

    public DataTabController(TamboSpringApp tamboSpringApp,
                              NavigationRouter navigationRouter,
                              TabNavigationState tabNavigationState) {
        super(3, tamboSpringApp, navigationRouter, tabNavigationState);
    }

    @Override
    protected int getTabIndex() {
        return 3;
    }
}
