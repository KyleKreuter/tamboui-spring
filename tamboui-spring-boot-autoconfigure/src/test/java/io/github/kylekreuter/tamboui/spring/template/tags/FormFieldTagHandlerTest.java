package io.github.kylekreuter.tamboui.spring.template.tags;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FormFieldTagHandler}.
 */
class FormFieldTagHandlerTest {

    private FormFieldTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FormFieldTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'form-field'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("form-field");
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
        @DisplayName("creates FormFieldWidget with label attribute")
        void withLabel() {
            Map<String, String> attrs = Map.of("label", "Username");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormFieldTagHandler.FormFieldWidget.class);
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.label()).isEqualTo("Username");
        }

        @Test
        @DisplayName("creates FormFieldWidget with field attribute (form mode)")
        void withField() {
            Map<String, String> attrs = Map.of("field", "username");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormFieldTagHandler.FormFieldWidget.class);
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.field()).isEqualTo("username");
            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("creates FormFieldWidget with bind attribute (standalone mode)")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "searchInput");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormFieldTagHandler.FormFieldWidget.class);
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.bind()).isEqualTo("searchInput");
            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("creates FormFieldWidget with type attribute")
        void withType() {
            Map<String, String> attrs = Map.of("type", "checkbox");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.type()).isEqualTo("checkbox");
        }

        @Test
        @DisplayName("creates FormFieldWidget with toggle type")
        void withToggleType() {
            Map<String, String> attrs = Map.of("type", "toggle");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.type()).isEqualTo("toggle");
        }

        @Test
        @DisplayName("creates FormFieldWidget with select type")
        void withSelectType() {
            Map<String, String> attrs = Map.of("type", "select");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.type()).isEqualTo("select");
        }

        @Test
        @DisplayName("creates FormFieldWidget with label-width attribute")
        void withLabelWidth() {
            Map<String, String> attrs = Map.of("label-width", "20");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.labelWidth()).isEqualTo("20");
        }

        @Test
        @DisplayName("creates FormFieldWidget with placeholder attribute")
        void withPlaceholder() {
            Map<String, String> attrs = Map.of("placeholder", "Enter value...");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.placeholder()).isEqualTo("Enter value...");
        }

        @Test
        @DisplayName("creates FormFieldWidget with border-type attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of("border-type", "rounded");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.borderType()).isEqualTo("rounded");
        }

        @Test
        @DisplayName("creates FormFieldWidget with border-color attribute")
        void withBorderColor() {
            Map<String, String> attrs = Map.of("border-color", "blue");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.borderColor()).isEqualTo("blue");
        }

        @Test
        @DisplayName("creates FormFieldWidget with focused-border-color attribute")
        void withFocusedBorderColor() {
            Map<String, String> attrs = Map.of("focused-border-color", "cyan");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.focusedBorderColor()).isEqualTo("cyan");
        }

        @Test
        @DisplayName("creates FormFieldWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("label", "Email");
            attrs.put("field", "email");
            attrs.put("bind", "emailInput");
            attrs.put("type", "text");
            attrs.put("label-width", "15");
            attrs.put("placeholder", "user@example.com");
            attrs.put("border-type", "rounded");
            attrs.put("border-color", "green");
            attrs.put("focused-border-color", "bright-green");

            Object result = handler.createElement(attrs);

            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.label()).isEqualTo("Email");
            assertThat(widget.field()).isEqualTo("email");
            assertThat(widget.bind()).isEqualTo("emailInput");
            assertThat(widget.type()).isEqualTo("text");
            assertThat(widget.labelWidth()).isEqualTo("15");
            assertThat(widget.placeholder()).isEqualTo("user@example.com");
            assertThat(widget.borderType()).isEqualTo("rounded");
            assertThat(widget.borderColor()).isEqualTo("green");
            assertThat(widget.focusedBorderColor()).isEqualTo("bright-green");
        }

        @Test
        @DisplayName("creates FormFieldWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormFieldTagHandler.FormFieldWidget.class);
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget) result;
            assertThat(widget.label()).isNull();
            assertThat(widget.field()).isNull();
            assertThat(widget.bind()).isNull();
            assertThat(widget.type()).isNull();
            assertThat(widget.labelWidth()).isNull();
            assertThat(widget.placeholder()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.borderColor()).isNull();
            assertThat(widget.focusedBorderColor()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("FormFieldWidget")
    class FormFieldWidgetTests {

        @Test
        @DisplayName("label() returns null when not set")
        void labelNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.label()).isNull();
        }

        @Test
        @DisplayName("field() returns null when not set")
        void fieldNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of("field", "name"));

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("type() returns null when not set")
        void typeNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.type()).isNull();
        }

        @Test
        @DisplayName("labelWidth() returns null when not set")
        void labelWidthNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.labelWidth()).isNull();
        }

        @Test
        @DisplayName("placeholder() returns null when not set")
        void placeholderNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.placeholder()).isNull();
        }

        @Test
        @DisplayName("borderType() returns null when not set")
        void borderTypeNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderType()).isNull();
        }

        @Test
        @DisplayName("borderColor() returns null when not set")
        void borderColorNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderColor()).isNull();
        }

        @Test
        @DisplayName("focusedBorderColor() returns null when not set")
        void focusedBorderColorNullWhenNotSet() {
            FormFieldTagHandler.FormFieldWidget widget = (FormFieldTagHandler.FormFieldWidget)
                    handler.createElement(Map.of());

            assertThat(widget.focusedBorderColor()).isNull();
        }
    }
}
