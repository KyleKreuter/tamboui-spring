package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.input.TextAreaState;
import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;

/**
 * Editor tab (index 1) — TextArea with line numbers.
 */
@TamboScreen(value = "editor", template = "editor")
public class EditorTabController extends AbstractTabController {

    @BindState("editorState")
    private final TextAreaState editorState = new TextAreaState(
        "// Welcome to the TamboUI Widget Showcase\n"
            + "// This text area demonstrates the TextArea widget.\n"
            + "// Try editing this content!\n"
            + "\n"
            + "public class HelloWorld {\n"
            + "    public static void main(String[] args) {\n"
            + "        System.out.println(\"Hello, TamboUI!\");\n"
            + "    }\n"
            + "}\n");

    public EditorTabController(TamboSpringApp tamboSpringApp,
                                NavigationRouter navigationRouter,
                                TabNavigationState tabNavigationState) {
        super(1, tamboSpringApp, navigationRouter, tabNavigationState);
    }

    @Override
    protected int getTabIndex() {
        return 1;
    }
}
