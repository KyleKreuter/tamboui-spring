package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.input.TextInput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link InputTagHandler}.
 */
class InputTagHandlerTest {

    private InputTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new InputTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'input'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("input");
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
        @DisplayName("creates InputWidget with field attribute (form mode)")
        void withField() {
            Map<String, String> attrs = Map.of("field", "username");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(InputTagHandler.InputWidget.class);
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.field()).isEqualTo("username");
            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("creates InputWidget with bind attribute (standalone mode)")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "searchInput");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(InputTagHandler.InputWidget.class);
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.bind()).isEqualTo("searchInput");
            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("creates InputWidget with placeholder attribute")
        void withPlaceholder() {
            Map<String, String> attrs = Map.of(
                    "bind", "search",
                    "placeholder", "Type to search..."
            );

            Object result = handler.createElement(attrs);

            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.placeholder()).isEqualTo("Type to search...");
        }

        @Test
        @DisplayName("creates InputWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("field", "email");
            attrs.put("bind", "emailInput");
            attrs.put("placeholder", "Enter email");

            Object result = handler.createElement(attrs);

            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.field()).isEqualTo("email");
            assertThat(widget.bind()).isEqualTo("emailInput");
            assertThat(widget.placeholder()).isEqualTo("Enter email");
        }

        @Test
        @DisplayName("creates InputWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(InputTagHandler.InputWidget.class);
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.field()).isNull();
            assertThat(widget.bind()).isNull();
            assertThat(widget.placeholder()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates InputWidget with TextInput widget")
        void hasTextInput() {
            Object result = handler.createElement(Map.of("bind", "test"));

            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget) result;
            assertThat(widget.textInput()).isNotNull();
            assertThat(widget.textInput()).isInstanceOf(TextInput.class);
        }
    }

    @Nested
    @DisplayName("InputWidget")
    class InputWidgetTests {

        @Test
        @DisplayName("textInput() returns the configured TextInput")
        void textInputReturnsTextInput() {
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.textInput()).isNotNull();
        }

        @Test
        @DisplayName("field() returns null when not set")
        void fieldNullWhenNotSet() {
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget)
                    handler.createElement(Map.of("field", "name"));

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("placeholder() returns null when not set")
        void placeholderNullWhenNotSet() {
            InputTagHandler.InputWidget widget = (InputTagHandler.InputWidget)
                    handler.createElement(Map.of());

            assertThat(widget.placeholder()).isNull();
        }
    }
}
