package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ColumnsTagHandler}.
 */
class ColumnsTagHandlerTest {

    private ColumnsTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ColumnsTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'columns'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("columns");
    }

    @Test
    @DisplayName("implements ParentTagHandler")
    void implementsParentTagHandler() {
        assertThat(handler).isInstanceOf(ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates ColumnsWidget with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ColumnsTagHandler.ColumnsWidget.class);
            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates ColumnsWidget with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "grow");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.flex()).isEqualTo("grow");
        }

        @Test
        @DisplayName("creates ColumnsWidget with margin attribute")
        void withMargin() {
            Map<String, String> attrs = Map.of("margin", "1 2 1 2");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.margin()).isEqualTo("1 2 1 2");
        }

        @Test
        @DisplayName("creates ColumnsWidget with uniform margin")
        void withUniformMargin() {
            Map<String, String> attrs = Map.of("margin", "3");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.margin()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates ColumnsWidget with column-count attribute")
        void withColumnCount() {
            Map<String, String> attrs = Map.of("column-count", "3");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.columnCount()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates ColumnsWidget with column-order row-first")
        void withColumnOrderRowFirst() {
            Map<String, String> attrs = Map.of("column-order", "row-first");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.columnOrder()).isEqualTo("row-first");
        }

        @Test
        @DisplayName("creates ColumnsWidget with column-order column-first")
        void withColumnOrderColumnFirst() {
            Map<String, String> attrs = Map.of("column-order", "column-first");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.columnOrder()).isEqualTo("column-first");
        }

        @Test
        @DisplayName("creates ColumnsWidget with class attribute")
        void withClassAttribute() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.cssClass()).isEqualTo("border-rounded p-1");
        }

        @Test
        @DisplayName("creates ColumnsWidget with id attribute")
        void withIdAttribute() {
            Map<String, String> attrs = Map.of("id", "main-columns");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.id()).isEqualTo("main-columns");
        }

        @Test
        @DisplayName("creates ColumnsWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "2");
            attrs.put("flex", "grow");
            attrs.put("margin", "1 2");
            attrs.put("column-count", "4");
            attrs.put("column-order", "row-first");
            attrs.put("class", "my-columns");
            attrs.put("id", "dashboard-cols");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
            assertThat(widget.flex()).isEqualTo("grow");
            assertThat(widget.margin()).isEqualTo("1 2");
            assertThat(widget.columnCount()).isEqualTo("4");
            assertThat(widget.columnOrder()).isEqualTo("row-first");
            assertThat(widget.cssClass()).isEqualTo("my-columns");
            assertThat(widget.id()).isEqualTo("dashboard-cols");
        }

        @Test
        @DisplayName("creates empty ColumnsWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(ColumnsTagHandler.ColumnsWidget.class);
            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.spacing()).isNull();
            assertThat(widget.flex()).isNull();
            assertThat(widget.margin()).isNull();
            assertThat(widget.columnCount()).isNull();
            assertThat(widget.columnOrder()).isNull();
            assertThat(widget.cssClass()).isNull();
            assertThat(widget.id()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ignores blank attribute values")
        void ignoresBlankValues() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "  ");
            attrs.put("column-count", "");
            attrs.put("class", "   ");

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.spacing()).isNull();
            assertThat(widget.columnCount()).isNull();
            assertThat(widget.cssClass()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of(
                    "spacing", "  2  ",
                    "column-count", "  3  ",
                    "column-order", "  row-first  "
            );

            Object result = handler.createElement(attrs);

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
            assertThat(widget.columnCount()).isEqualTo("3");
            assertThat(widget.columnOrder()).isEqualTo("row-first");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add child widgets to ColumnsWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("column-count", "2"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-ColumnsWidget parent")
        void nonColumnsWidgetParent() {
            // Should not throw when parent is not ColumnsWidget
            handler.addChildren("not a columns widget", List.of("child"));
        }

        @Test
        @DisplayName("ColumnsWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("can add children incrementally")
        void addChildrenIncrementally() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2", "child3"));

            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }
    }

    @Nested
    @DisplayName("ColumnsWidget")
    class ColumnsWidgetTests {

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            ColumnsTagHandler.ColumnsWidget widget = (ColumnsTagHandler.ColumnsWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }

        @Test
        @DisplayName("all properties can be set and retrieved")
        void allPropertiesWork() {
            ColumnsTagHandler.ColumnsWidget widget = new ColumnsTagHandler.ColumnsWidget();

            widget.setSpacing("3");
            widget.setFlex("shrink");
            widget.setMargin("2");
            widget.setColumnCount("5");
            widget.setColumnOrder("column-first");
            widget.setCssClass("my-class");
            widget.setId("my-id");

            assertThat(widget.spacing()).isEqualTo("3");
            assertThat(widget.flex()).isEqualTo("shrink");
            assertThat(widget.margin()).isEqualTo("2");
            assertThat(widget.columnCount()).isEqualTo("5");
            assertThat(widget.columnOrder()).isEqualTo("column-first");
            assertThat(widget.cssClass()).isEqualTo("my-class");
            assertThat(widget.id()).isEqualTo("my-id");
        }
    }
}
