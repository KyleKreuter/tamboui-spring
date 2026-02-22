package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.form.BooleanFieldState;
import dev.tamboui.widgets.input.TextAreaState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.tabs.TabsState;
import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

/**
 * Showcase screen demonstrating interactive widgets: Tabs, TextArea, and FormField.
 * Press {@code d} to open the dialog screen.
 */
@TamboScreen(value = "showcase", template = "showcase")
public class WidgetShowcaseController implements ScreenController {

    private final TamboSpringApp tamboSpringApp;
    private final NavigationRouter navigationRouter;

    @BindState("navigationTabs")
    private final TabsState navigationTabs = new TabsState(0);

    @BindState("editorState")
    private final TextAreaState editorState = new TextAreaState(
        "// Welcome to the TamboUI Widget Showcase\n"
            + "// This text area demonstrates the TextArea widget.\n"
            + "// Try editing this content!\n");

    @BindState("searchInput")
    private final TextInputState searchInput = new TextInputState();

    @BindState("darkModeToggle")
    private final BooleanFieldState darkModeToggle = new BooleanFieldState(true);

    public WidgetShowcaseController(TamboSpringApp tamboSpringApp,
                                    NavigationRouter navigationRouter) {
        this.tamboSpringApp = tamboSpringApp;
        this.navigationRouter = navigationRouter;
    }

    @Override
    public void populate(TemplateModel model) {
        model.put("title", "Widget Showcase")
            .put("footerHint", "TAB Focus  d Dialog  Ctrl+P/N Tabs  Ctrl+Q Quit");
    }

    @OnKey("ctrl+p")
    void previousTab() {
        navigationTabs.selectPrevious(3);
    }

    @OnKey("ctrl+n")
    void nextTab() {
        navigationTabs.selectNext(3);
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
