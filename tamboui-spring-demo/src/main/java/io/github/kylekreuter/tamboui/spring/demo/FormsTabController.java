package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.form.BooleanFieldState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.select.SelectState;
import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;

/**
 * Forms tab (index 2) — Input, Toggle, and Select widgets.
 */
@TamboScreen(value = "forms", template = "forms")
public class FormsTabController extends AbstractTabController {

    @BindState("searchInput")
    private final TextInputState searchInput = new TextInputState();

    @BindState("darkModeToggle")
    private final BooleanFieldState darkModeToggle = new BooleanFieldState(true);

    @BindState("themeSelect")
    private final SelectState themeSelect = new SelectState("Light", "Dark", "System");

    public FormsTabController(TamboSpringApp tamboSpringApp,
                               NavigationRouter navigationRouter,
                               TabNavigationState tabNavigationState) {
        super(2, tamboSpringApp, navigationRouter, tabNavigationState);
    }

    @Override
    protected int getTabIndex() {
        return 2;
    }
}
