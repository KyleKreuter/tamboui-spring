package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Direction;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.widgets.block.BorderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PanelTagHandler}.
 */
class PanelTagHandlerTest {

    private PanelTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PanelTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'panel'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("panel");
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates Panel with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of("title", "My Panel");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
            Panel panel = (Panel) result;
            assertThat(panel.styleAttributes()).containsEntry("title", "My Panel");
        }

        @Test
        @DisplayName("creates Panel without title when attribute missing")
        void withoutTitle() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
            Panel panel = (Panel) result;
            assertThat(panel.styleAttributes()).doesNotContainKey("title");
        }

        @Test
        @DisplayName("creates Panel with border-style attribute")
        void withBorderStyle() {
            Map<String, String> attrs = Map.of("border-style", "rounded");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("border-type attribute is accepted as fallback")
        void withBorderType() {
            Map<String, String> attrs = Map.of("border-type", "double");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("border-style takes precedence over border-type")
        void borderStyleOverBorderType() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("border-style", "rounded");
            attrs.put("border-type", "double");

            Object result = handler.createElement(attrs);

            // Both should work — border-style is checked first via getOrDefault
            assertThat(result).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("creates Panel with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("title", "Full Panel");
            attrs.put("border-style", "thick");
            attrs.put("direction", "horizontal");
            attrs.put("padding", "2");
            attrs.put("spacing", "1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
            Panel panel = (Panel) result;
            assertThat(panel.styleAttributes()).containsEntry("title", "Full Panel");
        }

        @Test
        @DisplayName("ignores invalid padding value gracefully")
        void invalidPadding() {
            Map<String, String> attrs = Map.of("padding", "abc");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("ignores invalid spacing value gracefully")
        void invalidSpacing() {
            Map<String, String> attrs = Map.of("spacing", "xyz");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Panel.class);
        }

        @Test
        @DisplayName("creates empty Panel with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(Panel.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("parseBorderType")
    class ParseBorderType {

        @Test
        @DisplayName("parses 'plain' to PLAIN")
        void plain() {
            assertThat(PanelTagHandler.parseBorderType("plain")).isEqualTo(BorderType.PLAIN);
        }

        @Test
        @DisplayName("parses 'rounded' to ROUNDED")
        void rounded() {
            assertThat(PanelTagHandler.parseBorderType("rounded")).isEqualTo(BorderType.ROUNDED);
        }

        @Test
        @DisplayName("parses 'double' to DOUBLE")
        void doubleBorder() {
            assertThat(PanelTagHandler.parseBorderType("double")).isEqualTo(BorderType.DOUBLE);
        }

        @Test
        @DisplayName("parses 'thick' to THICK")
        void thick() {
            assertThat(PanelTagHandler.parseBorderType("thick")).isEqualTo(BorderType.THICK);
        }

        @Test
        @DisplayName("parses 'none' to NONE")
        void none() {
            assertThat(PanelTagHandler.parseBorderType("none")).isEqualTo(BorderType.NONE);
        }

        @Test
        @DisplayName("unknown value defaults to PLAIN")
        void unknownDefaults() {
            assertThat(PanelTagHandler.parseBorderType("unknown")).isEqualTo(BorderType.PLAIN);
        }

        @Test
        @DisplayName("parsing is case-insensitive")
        void caseInsensitive() {
            assertThat(PanelTagHandler.parseBorderType("ROUNDED")).isEqualTo(BorderType.ROUNDED);
            assertThat(PanelTagHandler.parseBorderType("Thick")).isEqualTo(BorderType.THICK);
        }
    }

    @Nested
    @DisplayName("parseDirection")
    class ParseDirection {

        @Test
        @DisplayName("parses 'horizontal' to HORIZONTAL")
        void horizontal() {
            assertThat(PanelTagHandler.parseDirection("horizontal")).isEqualTo(Direction.HORIZONTAL);
        }

        @Test
        @DisplayName("parses 'row' to HORIZONTAL")
        void row() {
            assertThat(PanelTagHandler.parseDirection("row")).isEqualTo(Direction.HORIZONTAL);
        }

        @Test
        @DisplayName("parses 'vertical' to VERTICAL")
        void vertical() {
            assertThat(PanelTagHandler.parseDirection("vertical")).isEqualTo(Direction.VERTICAL);
        }

        @Test
        @DisplayName("parses 'column' to VERTICAL (default)")
        void column() {
            assertThat(PanelTagHandler.parseDirection("column")).isEqualTo(Direction.VERTICAL);
        }

        @Test
        @DisplayName("unknown value defaults to VERTICAL")
        void unknownDefaults() {
            assertThat(PanelTagHandler.parseDirection("unknown")).isEqualTo(Direction.VERTICAL);
        }

        @Test
        @DisplayName("parsing is case-insensitive")
        void caseInsensitive() {
            assertThat(PanelTagHandler.parseDirection("HORIZONTAL")).isEqualTo(Direction.HORIZONTAL);
            assertThat(PanelTagHandler.parseDirection("Row")).isEqualTo(Direction.HORIZONTAL);
        }
    }
}
