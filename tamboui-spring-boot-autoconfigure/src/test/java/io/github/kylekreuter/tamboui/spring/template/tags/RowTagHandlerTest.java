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
        @DisplayName("creates RowWidget with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates RowWidget with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates RowWidget with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "CENTER");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.flex()).isEqualTo("CENTER");
        }

        @Test
        @DisplayName("creates RowWidget with lowercase flex attribute")
        void withLowercaseFlex() {
            Map<String, String> attrs = Map.of("flex", "space_between");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.flex()).isEqualTo("space_between");
        }

        @Test
        @DisplayName("creates RowWidget with margin attribute")
        void withMargin() {
            Map<String, String> attrs = Map.of("margin", "1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.margin()).isEqualTo("1");
        }

        @Test
        @DisplayName("creates RowWidget with id attribute")
        void withId() {
            Map<String, String> attrs = Map.of("id", "my-row");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.id()).isEqualTo("my-row");
        }

        @Test
        @DisplayName("creates RowWidget with class attribute")
        void withCssClass() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.cssClass()).isEqualTo("border-rounded p-1");
        }

        @Test
        @DisplayName("creates RowWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "3");
            attrs.put("flex", "END");
            attrs.put("margin", "2");
            attrs.put("id", "test-row");
            attrs.put("class", "highlight");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.spacing()).isEqualTo("3");
            assertThat(widget.flex()).isEqualTo("END");
            assertThat(widget.margin()).isEqualTo("2");
            assertThat(widget.id()).isEqualTo("test-row");
            assertThat(widget.cssClass()).isEqualTo("highlight");
        }

        @Test
        @DisplayName("stores invalid spacing value as-is (validated during conversion)")
        void invalidSpacing() {
            Map<String, String> attrs = Map.of("spacing", "not-a-number");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.spacing()).isEqualTo("not-a-number");
        }

        @Test
        @DisplayName("stores invalid flex value as-is (validated during conversion)")
        void invalidFlex() {
            Map<String, String> attrs = Map.of("flex", "invalid_flex");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.flex()).isEqualTo("invalid_flex");
        }

        @Test
        @DisplayName("stores invalid margin value as-is (validated during conversion)")
        void invalidMargin() {
            Map<String, String> attrs = Map.of("margin", "abc");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(RowTagHandler.RowWidget.class);
            var widget = (RowTagHandler.RowWidget) result;
            assertThat(widget.margin()).isEqualTo("abc");
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
        @DisplayName("should add children to RowWidget")
        void addElementChildren() {
            var parent = (RowTagHandler.RowWidget) handler.createElement(Map.of());
            Row child1 = new Row();
            Row child2 = new Row();

            handler.addChildren(parent, List.of(child1, child2));

            assertThat(parent.children()).containsExactly(child1, child2);
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            var parent = (RowTagHandler.RowWidget) handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            assertThat(parent.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-RowWidget parent")
        void nonRowParent() {
            // Should not throw when parent is not a RowWidget
            handler.addChildren("not a row", List.of(new Row()));
        }

        @Test
        @DisplayName("should accept non-Element children (converted later)")
        void nonElementChildren() {
            var parent = (RowTagHandler.RowWidget) handler.createElement(Map.of());

            handler.addChildren(parent, List.of("not an element", 42));

            assertThat(parent.children()).hasSize(2);
        }

        @Test
        @DisplayName("should add all children including non-Element objects")
        void mixedChildren() {
            var parent = (RowTagHandler.RowWidget) handler.createElement(Map.of());
            Row validChild = new Row();

            handler.addChildren(parent, List.of(validChild, "not an element", 42));

            assertThat(parent.children()).hasSize(3);
            assertThat(parent.children()).first().isEqualTo(validChild);
        }
    }
}
