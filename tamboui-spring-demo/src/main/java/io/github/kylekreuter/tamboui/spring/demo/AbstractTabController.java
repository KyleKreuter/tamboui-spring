package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.tabs.TabsState;
import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

/**
 * Abstract base class for tab-based screens. Provides shared navigation logic
 * (Ctrl+P/N for tab switching, d for dialog, Ctrl+Q to quit) and a common
 * footer hint.
 * <p>
 * Each concrete subclass represents one tab and is annotated with
 * {@link io.github.kylekreuter.tamboui.spring.annotation.TamboScreen @TamboScreen}.
 */
public abstract class AbstractTabController implements ScreenController {

    protected static final String[] TAB_SCREENS = {"home", "editor", "forms", "data"};
    private static final int TAB_COUNT = TAB_SCREENS.length;

    protected final TamboSpringApp tamboSpringApp;
    protected final NavigationRouter navigationRouter;
    private final TabNavigationState tabNavigationState;

    @BindState("navigationTabs")
    protected final TabsState navigationTabs;

    protected AbstractTabController(int tabIndex, TamboSpringApp tamboSpringApp,
                                     NavigationRouter navigationRouter,
                                     TabNavigationState tabNavigationState) {
        this.navigationTabs = new TabsState(tabIndex);
        this.tamboSpringApp = tamboSpringApp;
        this.navigationRouter = navigationRouter;
        this.tabNavigationState = tabNavigationState;
    }

    protected abstract int getTabIndex();

    @Override
    public void onActivate() {
        tabNavigationState.setActiveTabScreen(TAB_SCREENS[getTabIndex()]);
    }

    @Override
    public void populate(TemplateModel model) {
        model.put("footerHint", "TAB Focus  d Dialog  Ctrl+P/N Tabs  Ctrl+Q Quit");
    }

    @OnKey("ctrl+p")
    void previousTab() {
        int prev = (getTabIndex() - 1 + TAB_COUNT) % TAB_COUNT;
        navigationRouter.navigateTo(TAB_SCREENS[prev]);
    }

    @OnKey("ctrl+n")
    void nextTab() {
        int next = (getTabIndex() + 1) % TAB_COUNT;
        navigationRouter.navigateTo(TAB_SCREENS[next]);
    }

    @OnKey("d")
    void openDialog() {
        navigationRouter.navigateTo("dialogShowcase");
    }

    @OnKey("ctrl+q")
    void quit() {
        tamboSpringApp.getRunner().quit();
    }
}
