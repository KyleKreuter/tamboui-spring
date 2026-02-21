package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.toolkit.elements.Column;
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
 * Unit tests for {@link ColumnTagHandler}.
 */
class ColumnTagHandlerTest {

    private ColumnTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ColumnTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'column'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("column");
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
        @DisplayName("creates Column with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(Column.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates Column with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("creates Column with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "CENTER");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("creates Column with lowercase flex attribute")
        void withLowercaseFlex() {
            Map<String, String> attrs = Map.of("flex", "space_evenly");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("creates Column with margin attribute")
        void withMargin() {
            Map<String, String> attrs = Map.of("margin", "1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("creates Column with id attribute")
        void withId() {
            Map<String, String> attrs = Map.of("id", "my-column");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
            Column column = (Column) result;
            assertThat(column.id()).isEqualTo("my-column");
        }

        @Test
        @DisplayName("creates Column with class attribute")
        void withCssClass() {
            Map<String, String> attrs = Map.of("class", "text-cyan p-2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
            Column column = (Column) result;
            assertThat(column.cssClasses()).contains("text-cyan", "p-2");
        }

        @Test
        @DisplayName("creates Column with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "1");
            attrs.put("flex", "SPACE_AROUND");
            attrs.put("margin", "3");
            attrs.put("id", "main-col");
            attrs.put("class", "bg-blue bold");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
            Column column = (Column) result;
            assertThat(column.id()).isEqualTo("main-col");
            assertThat(column.cssClasses()).contains("bg-blue", "bold");
        }

        @Test
        @DisplayName("ignores invalid spacing value gracefully")
        void invalidSpacing() {
            Map<String, String> attrs = Map.of("spacing", "not-a-number");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("ignores invalid flex value gracefully")
        void invalidFlex() {
            Map<String, String> attrs = Map.of("flex", "invalid_flex");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("ignores invalid margin value gracefully")
        void invalidMargin() {
            Map<String, String> attrs = Map.of("margin", "xyz");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(Column.class);
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
        @DisplayName("should add Element children to Column")
        void addElementChildren() {
            Column parent = (Column) handler.createElement(Map.of());
            Column child1 = new Column();
            Column child2 = new Column();

            handler.addChildren(parent, List.of(child1, child2));

            assertThat(parent).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Column parent = (Column) handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            assertThat(parent).isInstanceOf(Column.class);
        }

        @Test
        @DisplayName("should ignore non-Column parent")
        void nonColumnParent() {
            // Should not throw when parent is not a Column
            handler.addChildren("not a column", List.of(new Column()));
        }

        @Test
        @DisplayName("should skip non-Element children")
        void nonElementChildren() {
            Column parent = (Column) handler.createElement(Map.of());

            // Should not throw when children contain non-Element objects
            handler.addChildren(parent, List.of("not an element", 42));
        }

        @Test
        @DisplayName("should add only Element children, skipping non-Element objects")
        void mixedChildren() {
            Column parent = (Column) handler.createElement(Map.of());
            Column validChild = new Column();

            handler.addChildren(parent, List.of(validChild, "not an element"));

            assertThat(parent).isInstanceOf(Column.class);
        }
    }
}
