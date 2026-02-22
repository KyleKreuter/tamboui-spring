package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.form.BooleanFieldState;
import dev.tamboui.widgets.input.TextAreaState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.tabs.TabsState;
import dev.tamboui.widgets.tree.TreeNode;

import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

import java.util.List;

/**
 * Showcase screen demonstrating all 8 new widget types supported by the
 * {@link io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter}.
 * <p>
 * This screen provides a visual reference for Tabs, TextArea, Dialog, Tree,
 * FormField, Columns, Stack, and Flow widgets. Each widget is configured with
 * representative attributes and state bindings.
 */
@TamboScreen(template = "showcase")
public class WidgetShowcaseController implements ScreenController {

    private final TamboSpringApp tamboSpringApp;

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

    public WidgetShowcaseController(TamboSpringApp tamboSpringApp) {
        this.tamboSpringApp = tamboSpringApp;
    }

    @Override
    public void populate(TemplateModel model) {
        model.put("title", "Widget Showcase")
             .put("subtitle", "All 8 new widget types")
             .put("footerHint", "Ctrl+Q Quit");

        // Tree data for the tree widget
        List<TreeNode<String>> projectTree = List.of(
                TreeNode.<String>of("src")
                        .child(TreeNode.<String>of("main")
                                .child(TreeNode.<String>of("java").leaf())
                                .child(TreeNode.<String>of("resources").leaf()))
                        .child(TreeNode.<String>of("test")
                                .child(TreeNode.<String>of("java").leaf())),
                TreeNode.<String>of("docs")
                        .child(TreeNode.<String>of("architecture.md").leaf())
                        .child(TreeNode.<String>of("template-engine.md").leaf()),
                TreeNode.<String>of("README.md").leaf(),
                TreeNode.<String>of("build.gradle").leaf()
        );
        model.bindState("projectTree", projectTree);

        // Flow widget labels
        model.put("tag1", "Spring Boot")
             .put("tag2", "TamboUI")
             .put("tag3", "Java 17+")
             .put("tag4", "Terminal UI")
             .put("tag5", "Annotations")
             .put("tag6", "Templates")
             .put("tag7", "Reactive");
    }

    @OnKey("ctrl+q")
    void quit() {
        tamboSpringApp.getRunner().quit();
    }
}
