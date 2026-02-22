package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.select.Select;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SelectTagHandler}.
 */
class SelectTagHandlerTest {

    private SelectTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SelectTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'select'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("select");
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
        @DisplayName("creates SelectWidget with field attribute (form mode)")
        void withField() {
            Map<String, String> attrs = Map.of("field", "country");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(SelectTagHandler.SelectWidget.class);
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.field()).isEqualTo("country");
            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("creates SelectWidget with bind attribute (standalone mode)")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "themeSelect");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(SelectTagHandler.SelectWidget.class);
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.bind()).isEqualTo("themeSelect");
            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("creates SelectWidget with options attribute")
        void withOptions() {
            Map<String, String> attrs = Map.of(
                    "bind", "theme",
                    "options", "Light,Dark,System"
            );

            Object result = handler.createElement(attrs);

            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.options()).isEqualTo("Light,Dark,System");
        }

        @Test
        @DisplayName("creates SelectWidget with left-indicator and right-indicator")
        void withIndicators() {
            Map<String, String> attrs = Map.of(
                    "bind", "selector",
                    "left-indicator", "<< ",
                    "right-indicator", " >>"
            );

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(SelectTagHandler.SelectWidget.class);
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.select()).isNotNull();
        }

        @Test
        @DisplayName("creates SelectWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("field", "country");
            attrs.put("bind", "countrySelect");
            attrs.put("options", "DE,US,UK");
            attrs.put("left-indicator", "[");
            attrs.put("right-indicator", "]");

            Object result = handler.createElement(attrs);

            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.field()).isEqualTo("country");
            assertThat(widget.bind()).isEqualTo("countrySelect");
            assertThat(widget.options()).isEqualTo("DE,US,UK");
        }

        @Test
        @DisplayName("creates SelectWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(SelectTagHandler.SelectWidget.class);
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.field()).isNull();
            assertThat(widget.bind()).isNull();
            assertThat(widget.options()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates SelectWidget with Select widget")
        void hasSelect() {
            Object result = handler.createElement(Map.of("bind", "test"));

            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget) result;
            assertThat(widget.select()).isNotNull();
            assertThat(widget.select()).isInstanceOf(Select.class);
        }
    }

    @Nested
    @DisplayName("SelectWidget")
    class SelectWidgetTests {

        @Test
        @DisplayName("select() returns the configured Select")
        void selectReturnsSelect() {
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.select()).isNotNull();
        }

        @Test
        @DisplayName("field() returns null when not set")
        void fieldNullWhenNotSet() {
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.field()).isNull();
        }

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget)
                    handler.createElement(Map.of("field", "name"));

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("options() returns null when not set")
        void optionsNullWhenNotSet() {
            SelectTagHandler.SelectWidget widget = (SelectTagHandler.SelectWidget)
                    handler.createElement(Map.of());

            assertThat(widget.options()).isNull();
        }
    }
}
