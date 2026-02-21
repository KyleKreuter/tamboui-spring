package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Constraint;
import dev.tamboui.toolkit.elements.Spacer;
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
 * Unit tests for {@link SpacerTagHandler}.
 */
class SpacerTagHandlerTest {

    private SpacerTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SpacerTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'spacer'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("spacer");
    }

    @Test
    @DisplayName("implements TagHandler but not ParentTagHandler")
    void implementsTagHandler() {
        assertThat(handler).isInstanceOf(TagHandler.class);
        assertThat(handler).isNotInstanceOf(ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates fill Spacer with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }

        @Test
        @DisplayName("creates fill Spacer with empty attributes")
        void withEmptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }

        @Test
        @DisplayName("creates fixed-size Spacer with size attribute")
        void withSize() {
            Map<String, String> attrs = Map.of("size", "5");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Length.class);
            assertThat(((Constraint.Length) spacer.constraint()).value()).isEqualTo(5);
        }

        @Test
        @DisplayName("creates weighted Spacer with weight attribute")
        void withWeight() {
            Map<String, String> attrs = Map.of("weight", "3");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }

        @Test
        @DisplayName("size attribute takes precedence over weight")
        void sizePrecedenceOverWeight() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("size", "10");
            attrs.put("weight", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Length.class);
            assertThat(((Constraint.Length) spacer.constraint()).value()).isEqualTo(10);
        }

        @Test
        @DisplayName("ignores invalid size value gracefully, falls back to fill")
        void invalidSize() {
            Map<String, String> attrs = Map.of("size", "not-a-number");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }

        @Test
        @DisplayName("ignores invalid weight value gracefully, falls back to fill")
        void invalidWeight() {
            Map<String, String> attrs = Map.of("weight", "abc");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("handles size with whitespace")
        void sizeWithWhitespace() {
            Map<String, String> attrs = Map.of("size", " 7 ");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Length.class);
            assertThat(((Constraint.Length) spacer.constraint()).value()).isEqualTo(7);
        }

        @Test
        @DisplayName("handles weight with whitespace")
        void weightWithWhitespace() {
            Map<String, String> attrs = Map.of("weight", " 2 ");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Spacer.class);
            Spacer spacer = (Spacer) result;
            assertThat(spacer.constraint()).isInstanceOf(Constraint.Fill.class);
        }
    }
}
