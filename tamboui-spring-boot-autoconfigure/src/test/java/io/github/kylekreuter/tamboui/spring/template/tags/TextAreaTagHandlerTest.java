package io.github.kylekreuter.tamboui.spring.template.tags;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TextAreaTagHandler}.
 */
class TextAreaTagHandlerTest {

    private TextAreaTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TextAreaTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'textarea'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("textarea");
    }

    @Test
    @DisplayName("is a TagHandler but not ParentTagHandler")
    void isNotParentTagHandler() {
        assertThat(handler).isInstanceOf(io.github.kylekreuter.tamboui.spring.template.TagHandler.class);
        assertThat(handler).isNotInstanceOf(io.github.kylekreuter.tamboui.spring.template.ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates TextAreaWidget with bind attribute")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "editorState");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TextAreaTagHandler.TextAreaWidget.class);
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.bind()).isEqualTo("editorState");
        }

        @Test
        @DisplayName("creates TextAreaWidget with placeholder attribute")
        void withPlaceholder() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "placeholder", "Enter text..."
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.placeholder()).isEqualTo("Enter text...");
        }

        @Test
        @DisplayName("creates TextAreaWidget with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "title", "Description"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.title()).isEqualTo("Description");
        }

        @Test
        @DisplayName("creates TextAreaWidget with border-type attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "border-type", "rounded"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.borderType()).isEqualTo("rounded");
        }

        @Test
        @DisplayName("creates TextAreaWidget with border-color attribute")
        void withBorderColor() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "border-color", "CYAN"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.borderColor()).isEqualTo("CYAN");
        }

        @Test
        @DisplayName("creates TextAreaWidget with focused-border-color attribute")
        void withFocusedBorderColor() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "focused-border-color", "YELLOW"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.focusedBorderColor()).isEqualTo("YELLOW");
        }

        @Test
        @DisplayName("creates TextAreaWidget with show-line-numbers=true")
        void withShowLineNumbersTrue() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "show-line-numbers", "true"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.showLineNumbers()).isTrue();
        }

        @Test
        @DisplayName("creates TextAreaWidget with show-line-numbers=false")
        void withShowLineNumbersFalse() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "show-line-numbers", "false"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.showLineNumbers()).isFalse();
        }

        @Test
        @DisplayName("creates TextAreaWidget with show-line-numbers case insensitive")
        void withShowLineNumbersCaseInsensitive() {
            Map<String, String> attrs = Map.of(
                    "bind", "editor",
                    "show-line-numbers", "TRUE"
            );

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.showLineNumbers()).isTrue();
        }

        @Test
        @DisplayName("show-line-numbers defaults to false when not set")
        void showLineNumbersDefaultsFalse() {
            Map<String, String> attrs = Map.of("bind", "editor");

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.showLineNumbers()).isFalse();
        }

        @Test
        @DisplayName("creates TextAreaWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("bind", "editorState");
            attrs.put("placeholder", "Type here...");
            attrs.put("title", "Code Editor");
            attrs.put("border-type", "rounded");
            attrs.put("border-color", "GREEN");
            attrs.put("focused-border-color", "YELLOW");
            attrs.put("show-line-numbers", "true");

            Object result = handler.createElement(attrs);

            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.bind()).isEqualTo("editorState");
            assertThat(widget.placeholder()).isEqualTo("Type here...");
            assertThat(widget.title()).isEqualTo("Code Editor");
            assertThat(widget.borderType()).isEqualTo("rounded");
            assertThat(widget.borderColor()).isEqualTo("GREEN");
            assertThat(widget.focusedBorderColor()).isEqualTo("YELLOW");
            assertThat(widget.showLineNumbers()).isTrue();
        }

        @Test
        @DisplayName("creates TextAreaWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TextAreaTagHandler.TextAreaWidget.class);
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget) result;
            assertThat(widget.bind()).isNull();
            assertThat(widget.placeholder()).isNull();
            assertThat(widget.title()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.borderColor()).isNull();
            assertThat(widget.focusedBorderColor()).isNull();
            assertThat(widget.showLineNumbers()).isFalse();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("TextAreaWidget")
    class TextAreaWidgetTests {

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("placeholder() returns null when not set")
        void placeholderNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.placeholder()).isNull();
        }

        @Test
        @DisplayName("title() returns null when not set")
        void titleNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.title()).isNull();
        }

        @Test
        @DisplayName("borderType() returns null when not set")
        void borderTypeNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderType()).isNull();
        }

        @Test
        @DisplayName("borderColor() returns null when not set")
        void borderColorNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderColor()).isNull();
        }

        @Test
        @DisplayName("focusedBorderColor() returns null when not set")
        void focusedBorderColorNullWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.focusedBorderColor()).isNull();
        }

        @Test
        @DisplayName("showLineNumbers() returns false when not set")
        void showLineNumbersFalseWhenNotSet() {
            TextAreaTagHandler.TextAreaWidget widget = (TextAreaTagHandler.TextAreaWidget)
                    handler.createElement(Map.of());

            assertThat(widget.showLineNumbers()).isFalse();
        }
    }
}
