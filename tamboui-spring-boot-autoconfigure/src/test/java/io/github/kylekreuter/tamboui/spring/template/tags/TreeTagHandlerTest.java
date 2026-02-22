package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TreeTagHandler}.
 */
class TreeTagHandlerTest {

    private TreeTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TreeTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'tree'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("tree");
    }

    @Test
    @DisplayName("is a TagHandler but not ParentTagHandler")
    void isNotParentTagHandler() {
        assertThat(handler).isInstanceOf(TagHandler.class);
        assertThat(handler).isNotInstanceOf(ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates TreeWidget with bind attribute")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "fileTree");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TreeTagHandler.TreeWidget.class);
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.bind()).isEqualTo("fileTree");
        }

        @Test
        @DisplayName("creates TreeWidget with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of("title", "Files");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.title()).isEqualTo("Files");
        }

        @Test
        @DisplayName("creates TreeWidget with border-type attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of("border-type", "rounded");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.borderType()).isEqualTo("rounded");
        }

        @Test
        @DisplayName("creates TreeWidget with border-color attribute")
        void withBorderColor() {
            Map<String, String> attrs = Map.of("border-color", "cyan");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.borderColor()).isEqualTo("cyan");
        }

        @Test
        @DisplayName("creates TreeWidget with guide-style attribute (unicode)")
        void withGuideStyleUnicode() {
            Map<String, String> attrs = Map.of("guide-style", "unicode");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.guideStyle()).isEqualTo("unicode");
        }

        @Test
        @DisplayName("creates TreeWidget with guide-style attribute (ascii)")
        void withGuideStyleAscii() {
            Map<String, String> attrs = Map.of("guide-style", "ascii");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.guideStyle()).isEqualTo("ascii");
        }

        @Test
        @DisplayName("creates TreeWidget with guide-style attribute (none)")
        void withGuideStyleNone() {
            Map<String, String> attrs = Map.of("guide-style", "none");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.guideStyle()).isEqualTo("none");
        }

        @Test
        @DisplayName("creates TreeWidget with highlight-color attribute")
        void withHighlightColor() {
            Map<String, String> attrs = Map.of("highlight-color", "yellow");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.highlightColor()).isEqualTo("yellow");
        }

        @Test
        @DisplayName("creates TreeWidget with highlight-symbol attribute")
        void withHighlightSymbol() {
            Map<String, String> attrs = Map.of("highlight-symbol", ">> ");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.highlightSymbol()).isEqualTo(">> ");
        }

        @Test
        @DisplayName("creates TreeWidget with scrollbar-policy attribute")
        void withScrollbarPolicy() {
            Map<String, String> attrs = Map.of("scrollbar-policy", "auto");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.scrollbarPolicy()).isEqualTo("auto");
        }

        @Test
        @DisplayName("creates TreeWidget with indent-width attribute")
        void withIndentWidth() {
            Map<String, String> attrs = Map.of("indent-width", "4");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.indentWidth()).isEqualTo("4");
        }

        @Test
        @DisplayName("creates TreeWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("bind", "projectTree");
            attrs.put("title", "Project");
            attrs.put("border-type", "double");
            attrs.put("border-color", "magenta");
            attrs.put("guide-style", "unicode");
            attrs.put("highlight-color", "cyan");
            attrs.put("highlight-symbol", "> ");
            attrs.put("scrollbar-policy", "always");
            attrs.put("indent-width", "3");

            Object result = handler.createElement(attrs);

            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.bind()).isEqualTo("projectTree");
            assertThat(widget.title()).isEqualTo("Project");
            assertThat(widget.borderType()).isEqualTo("double");
            assertThat(widget.borderColor()).isEqualTo("magenta");
            assertThat(widget.guideStyle()).isEqualTo("unicode");
            assertThat(widget.highlightColor()).isEqualTo("cyan");
            assertThat(widget.highlightSymbol()).isEqualTo("> ");
            assertThat(widget.scrollbarPolicy()).isEqualTo("always");
            assertThat(widget.indentWidth()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates TreeWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TreeTagHandler.TreeWidget.class);
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget) result;
            assertThat(widget.bind()).isNull();
            assertThat(widget.title()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.borderColor()).isNull();
            assertThat(widget.guideStyle()).isNull();
            assertThat(widget.highlightColor()).isNull();
            assertThat(widget.highlightSymbol()).isNull();
            assertThat(widget.scrollbarPolicy()).isNull();
            assertThat(widget.indentWidth()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("TreeWidget")
    class TreeWidgetTests {

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("title() returns null when not set")
        void titleNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.title()).isNull();
        }

        @Test
        @DisplayName("borderType() returns null when not set")
        void borderTypeNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderType()).isNull();
        }

        @Test
        @DisplayName("borderColor() returns null when not set")
        void borderColorNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderColor()).isNull();
        }

        @Test
        @DisplayName("guideStyle() returns null when not set")
        void guideStyleNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.guideStyle()).isNull();
        }

        @Test
        @DisplayName("highlightColor() returns null when not set")
        void highlightColorNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.highlightColor()).isNull();
        }

        @Test
        @DisplayName("highlightSymbol() returns null when not set")
        void highlightSymbolNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.highlightSymbol()).isNull();
        }

        @Test
        @DisplayName("scrollbarPolicy() returns null when not set")
        void scrollbarPolicyNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.scrollbarPolicy()).isNull();
        }

        @Test
        @DisplayName("indentWidth() returns null when not set")
        void indentWidthNullWhenNotSet() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of());

            assertThat(widget.indentWidth()).isNull();
        }

        @Test
        @DisplayName("bind() returns correct value when set")
        void bindReturnsValue() {
            TreeTagHandler.TreeWidget widget = (TreeTagHandler.TreeWidget)
                    handler.createElement(Map.of("bind", "myTree"));

            assertThat(widget.bind()).isEqualTo("myTree");
        }

        @Test
        @DisplayName("all constructor fields are stored correctly")
        void constructorStoresAllFields() {
            TreeTagHandler.TreeWidget widget = new TreeTagHandler.TreeWidget(
                    "binding", "My Tree", "rounded", "blue",
                    "unicode", "green", "-> ", "auto", "2"
            );

            assertThat(widget.bind()).isEqualTo("binding");
            assertThat(widget.title()).isEqualTo("My Tree");
            assertThat(widget.borderType()).isEqualTo("rounded");
            assertThat(widget.borderColor()).isEqualTo("blue");
            assertThat(widget.guideStyle()).isEqualTo("unicode");
            assertThat(widget.highlightColor()).isEqualTo("green");
            assertThat(widget.highlightSymbol()).isEqualTo("-> ");
            assertThat(widget.scrollbarPolicy()).isEqualTo("auto");
            assertThat(widget.indentWidth()).isEqualTo("2");
        }
    }
}
