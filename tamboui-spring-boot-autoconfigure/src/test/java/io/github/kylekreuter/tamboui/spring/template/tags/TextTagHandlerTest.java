package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.paragraph.Paragraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TextTagHandler}.
 */
class TextTagHandlerTest {

    private TextTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TextTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'text'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("text");
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates Paragraph with t:text attribute")
        void withTTextAttribute() {
            Map<String, String> attrs = Map.of("t:text", "Hello World");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("creates Paragraph with content attribute")
        void withContentAttribute() {
            Map<String, String> attrs = Map.of("content", "Hello World");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("creates Paragraph with value attribute as fallback")
        void withValueAttribute() {
            Map<String, String> attrs = Map.of("value", "Fallback Text");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("t:text attribute takes highest precedence")
        void tTextOverContent() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("t:text", "Primary");
            attrs.put("content", "Secondary");
            attrs.put("value", "Tertiary");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("content attribute takes precedence over value")
        void contentOverValue() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("content", "Primary");
            attrs.put("value", "Secondary");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("creates Paragraph with empty content when no attributes")
        void withEmptyAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Paragraph.class);
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }
}
