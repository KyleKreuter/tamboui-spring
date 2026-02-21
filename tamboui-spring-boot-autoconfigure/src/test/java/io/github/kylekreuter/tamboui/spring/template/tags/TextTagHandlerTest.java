package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Alignment;
import dev.tamboui.style.Overflow;
import dev.tamboui.toolkit.elements.TextElement;
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
        @DisplayName("creates TextElement with content attribute")
        void withContentAttribute() {
            Map<String, String> attrs = Map.of("content", "Hello World");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TextElement.class);
            TextElement textElement = (TextElement) result;
            assertThat(textElement.content()).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("creates TextElement with value attribute as fallback")
        void withValueAttribute() {
            Map<String, String> attrs = Map.of("value", "Fallback Text");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TextElement.class);
            TextElement textElement = (TextElement) result;
            assertThat(textElement.content()).isEqualTo("Fallback Text");
        }

        @Test
        @DisplayName("content attribute takes precedence over value")
        void contentOverValue() {
            Map<String, String> attrs = Map.of(
                    "content", "Primary",
                    "value", "Secondary"
            );

            Object result = handler.createElement(attrs);

            TextElement textElement = (TextElement) result;
            assertThat(textElement.content()).isEqualTo("Primary");
        }

        @Test
        @DisplayName("creates TextElement with empty content when no attributes")
        void withEmptyAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TextElement.class);
            TextElement textElement = (TextElement) result;
            assertThat(textElement.content()).isEmpty();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("parseOverflow")
    class ParseOverflow {

        @Test
        @DisplayName("parses 'clip' to CLIP")
        void clip() {
            assertThat(TextTagHandler.parseOverflow("clip")).isEqualTo(Overflow.CLIP);
        }

        @Test
        @DisplayName("parses 'ellipsis' to ELLIPSIS")
        void ellipsis() {
            assertThat(TextTagHandler.parseOverflow("ellipsis")).isEqualTo(Overflow.ELLIPSIS);
        }

        @Test
        @DisplayName("parses 'ellipsis-start' to ELLIPSIS_START")
        void ellipsisStart() {
            assertThat(TextTagHandler.parseOverflow("ellipsis-start")).isEqualTo(Overflow.ELLIPSIS_START);
        }

        @Test
        @DisplayName("parses 'ellipsis-middle' to ELLIPSIS_MIDDLE")
        void ellipsisMiddle() {
            assertThat(TextTagHandler.parseOverflow("ellipsis-middle")).isEqualTo(Overflow.ELLIPSIS_MIDDLE);
        }

        @Test
        @DisplayName("parses 'wrap-word' to WRAP_WORD")
        void wrapWord() {
            assertThat(TextTagHandler.parseOverflow("wrap-word")).isEqualTo(Overflow.WRAP_WORD);
        }

        @Test
        @DisplayName("parses 'wrap-character' to WRAP_CHARACTER")
        void wrapCharacter() {
            assertThat(TextTagHandler.parseOverflow("wrap-character")).isEqualTo(Overflow.WRAP_CHARACTER);
        }

        @Test
        @DisplayName("unknown value defaults to CLIP")
        void unknownDefaults() {
            assertThat(TextTagHandler.parseOverflow("unknown")).isEqualTo(Overflow.CLIP);
        }

        @Test
        @DisplayName("parsing is case-insensitive")
        void caseInsensitive() {
            assertThat(TextTagHandler.parseOverflow("ELLIPSIS")).isEqualTo(Overflow.ELLIPSIS);
            assertThat(TextTagHandler.parseOverflow("Wrap-Word")).isEqualTo(Overflow.WRAP_WORD);
        }
    }

    @Nested
    @DisplayName("parseAlignment")
    class ParseAlignment {

        @Test
        @DisplayName("parses 'left' to LEFT")
        void left() {
            assertThat(TextTagHandler.parseAlignment("left")).isEqualTo(Alignment.LEFT);
        }

        @Test
        @DisplayName("parses 'center' to CENTER")
        void center() {
            assertThat(TextTagHandler.parseAlignment("center")).isEqualTo(Alignment.CENTER);
        }

        @Test
        @DisplayName("parses 'right' to RIGHT")
        void right() {
            assertThat(TextTagHandler.parseAlignment("right")).isEqualTo(Alignment.RIGHT);
        }

        @Test
        @DisplayName("unknown value defaults to LEFT")
        void unknownDefaults() {
            assertThat(TextTagHandler.parseAlignment("unknown")).isEqualTo(Alignment.LEFT);
        }

        @Test
        @DisplayName("parsing is case-insensitive")
        void caseInsensitive() {
            assertThat(TextTagHandler.parseAlignment("CENTER")).isEqualTo(Alignment.CENTER);
            assertThat(TextTagHandler.parseAlignment("Right")).isEqualTo(Alignment.RIGHT);
        }
    }
}
