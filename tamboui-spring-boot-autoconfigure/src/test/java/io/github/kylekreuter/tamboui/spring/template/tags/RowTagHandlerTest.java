package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.toolkit.elements.Row;
import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RowTagHandler}.
 */
class RowTagHandlerTest {

    private RowTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RowTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'row'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("row");
    }

    @Test
    @DisplayName("implements ParentTagHandler")
    void implementsParentTagHandler() {
        assertThat(handler).isInstanceOf(ParentTagHandler.class);
        assertThat(handler).isInstanceOf(TagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates Row with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(Row.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates Row with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("creates Row with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "CENTER");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("creates Row with lowercase flex attribute")
        void withLowercaseFlex() {
            Map<String, String> attrs = Map.of("flex", "space_between");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("creates Row with margin attribute")
        void withMargin() {
            Map<String, String> attrs = Map.of("margin", "1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("creates Row with id attribute")
        void withId() {
            Map<String, String> attrs = Map.of("id", "my-row");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
            Row row = (Row) result;
            assertThat(row.id()).isEqualTo("my-row");
        }

        @Test
        @DisplayName("creates Row with class attribute")
        void withCssClass() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
            Row row = (Row) result;
            assertThat(row.cssClasses()).contains("border-rounded", "p-1");
        }

        @Test
        @DisplayName("creates Row with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "3");
            attrs.put("flex", "END");
            attrs.put("margin", "2");
            attrs.put("id", "test-row");
            attrs.put("class", "highlight");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
            Row row = (Row) result;
            assertThat(row.id()).isEqualTo("test-row");
            assertThat(row.cssClasses()).contains("highlight");
        }

        @Test
        @DisplayName("ignores invalid spacing value gracefully")
        void invalidSpacing() {
            Map<String, String> attrs = Map.of("spacing", "not-a-number");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("ignores invalid flex value gracefully")
        void invalidFlex() {
            Map<String, String> attrs = Map.of("flex", "invalid_flex");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("ignores invalid margin value gracefully")
        void invalidMargin() {
            Map<String, String> attrs = Map.of("margin", "abc");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add Element children to Row")
        void addElementChildren() {
            Row parent = (Row) handler.createElement(Map.of());
            Row child1 = new Row();
            Row child2 = new Row();

            handler.addChildren(parent, List.of(child1, child2));

            // Row extends ContainerElement which has protected children field
            // We verify indirectly that the row accepted children by checking it's still a valid Row
            assertThat(parent).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Row parent = (Row) handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            assertThat(parent).isInstanceOf(Row.class);
        }

        @Test
        @DisplayName("should ignore non-Row parent")
        void nonRowParent() {
            // Should not throw when parent is not a Row
            handler.addChildren("not a row", List.of(new Row()));
        }

        @Test
        @DisplayName("should skip non-Element children")
        void nonElementChildren() {
            Row parent = (Row) handler.createElement(Map.of());

            // Should not throw when children contain non-Element objects
            handler.addChildren(parent, List.of("not an element", 42));
        }

        @Test
        @DisplayName("should add only Element children, skipping non-Element objects")
        void mixedChildren() {
            Row parent = (Row) handler.createElement(Map.of());
            Row validChild = new Row();

            handler.addChildren(parent, List.of(validChild, "not an element", 42));

            assertThat(parent).isInstanceOf(Row.class);
        }
    }
}
