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
        @DisplayName("creates ColumnWidget with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates ColumnWidget with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates ColumnWidget with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "CENTER");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.flex()).isEqualTo("CENTER");
        }

        @Test
        @DisplayName("creates ColumnWidget with lowercase flex attribute")
        void withLowercaseFlex() {
            Map<String, String> attrs = Map.of("flex", "space_evenly");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.flex()).isEqualTo("space_evenly");
        }

        @Test
        @DisplayName("creates ColumnWidget with margin attribute")
        void withMargin() {
            Map<String, String> attrs = Map.of("margin", "1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.margin()).isEqualTo("1");
        }

        @Test
        @DisplayName("creates ColumnWidget with id attribute")
        void withId() {
            Map<String, String> attrs = Map.of("id", "my-column");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.id()).isEqualTo("my-column");
        }

        @Test
        @DisplayName("creates ColumnWidget with class attribute")
        void withCssClass() {
            Map<String, String> attrs = Map.of("class", "text-cyan p-2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.cssClass()).isEqualTo("text-cyan p-2");
        }

        @Test
        @DisplayName("creates ColumnWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "1");
            attrs.put("flex", "SPACE_AROUND");
            attrs.put("margin", "3");
            attrs.put("id", "main-col");
            attrs.put("class", "bg-blue bold");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.spacing()).isEqualTo("1");
            assertThat(widget.flex()).isEqualTo("SPACE_AROUND");
            assertThat(widget.margin()).isEqualTo("3");
            assertThat(widget.id()).isEqualTo("main-col");
            assertThat(widget.cssClass()).isEqualTo("bg-blue bold");
        }

        @Test
        @DisplayName("stores invalid spacing value as-is (validated during conversion)")
        void invalidSpacing() {
            Map<String, String> attrs = Map.of("spacing", "not-a-number");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.spacing()).isEqualTo("not-a-number");
        }

        @Test
        @DisplayName("stores invalid flex value as-is (validated during conversion)")
        void invalidFlex() {
            Map<String, String> attrs = Map.of("flex", "invalid_flex");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.flex()).isEqualTo("invalid_flex");
        }

        @Test
        @DisplayName("stores invalid margin value as-is (validated during conversion)")
        void invalidMargin() {
            Map<String, String> attrs = Map.of("margin", "xyz");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnTagHandler.ColumnWidget.class);
            var widget = (ColumnTagHandler.ColumnWidget) result;
            assertThat(widget.margin()).isEqualTo("xyz");
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
        @DisplayName("should add children to ColumnWidget")
        void addElementChildren() {
            var parent = (ColumnTagHandler.ColumnWidget) handler.createElement(Map.of());
            Column child1 = new Column();
            Column child2 = new Column();

            handler.addChildren(parent, List.of(child1, child2));

            assertThat(parent.children()).containsExactly(child1, child2);
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            var parent = (ColumnTagHandler.ColumnWidget) handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            assertThat(parent.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-ColumnWidget parent")
        void nonColumnParent() {
            // Should not throw when parent is not a ColumnWidget
            handler.addChildren("not a column", List.of(new Column()));
        }

        @Test
        @DisplayName("should accept non-Element children (converted later)")
        void nonElementChildren() {
            var parent = (ColumnTagHandler.ColumnWidget) handler.createElement(Map.of());

            handler.addChildren(parent, List.of("not an element", 42));

            assertThat(parent.children()).hasSize(2);
        }

        @Test
        @DisplayName("should add all children including non-Element objects")
        void mixedChildren() {
            var parent = (ColumnTagHandler.ColumnWidget) handler.createElement(Map.of());
            Column validChild = new Column();

            handler.addChildren(parent, List.of(validChild, "not an element"));

            assertThat(parent.children()).hasSize(2);
            assertThat(parent.children()).first().isEqualTo(validChild);
        }
    }
}
