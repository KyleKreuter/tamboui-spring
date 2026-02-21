package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.list.ListItem;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ItemTagHandler}.
 */
class ItemTagHandlerTest {

    private ItemTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ItemTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'item'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("item");
    }

    @Test
    @DisplayName("implements TagHandler but not ParentTagHandler")
    void implementsTagHandler() {
        assertThat(handler).isInstanceOf(TagHandler.class);
        assertThat(handler).isNotInstanceOf(io.github.kylekreuter.tamboui.spring.template.ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates ListItem with t:text attribute")
        void withTTextAttribute() {
            Map<String, String> attrs = Map.of("t:text", "Hello World");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ListItem.class);
            ListItem item = (ListItem) result;
            assertThat(item).isEqualTo(ListItem.from("Hello World"));
        }

        @Test
        @DisplayName("creates ListItem with content attribute")
        void withContentAttribute() {
            Map<String, String> attrs = Map.of("content", "Content Text");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ListItem.class);
            assertThat(result).isEqualTo(ListItem.from("Content Text"));
        }

        @Test
        @DisplayName("creates ListItem with value attribute as fallback")
        void withValueAttribute() {
            Map<String, String> attrs = Map.of("value", "Value Text");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ListItem.class);
            assertThat(result).isEqualTo(ListItem.from("Value Text"));
        }

        @Test
        @DisplayName("t:text attribute takes highest precedence")
        void tTextPrecedence() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("t:text", "Primary");
            attrs.put("content", "Secondary");
            attrs.put("value", "Tertiary");

            Object result = handler.createElement(attrs);

            assertThat(result).isEqualTo(ListItem.from("Primary"));
        }

        @Test
        @DisplayName("content attribute takes precedence over value")
        void contentOverValue() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("content", "Primary");
            attrs.put("value", "Secondary");

            Object result = handler.createElement(attrs);

            assertThat(result).isEqualTo(ListItem.from("Primary"));
        }

        @Test
        @DisplayName("creates ListItem with empty text when no attributes")
        void withEmptyAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(ListItem.class);
            assertThat(result).isEqualTo(ListItem.from(""));
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }
}
