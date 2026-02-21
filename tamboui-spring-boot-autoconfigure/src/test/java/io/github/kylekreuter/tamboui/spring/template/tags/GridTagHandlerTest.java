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
 * Unit tests for {@link GridTagHandler}.
 */
class GridTagHandlerTest {

    private GridTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GridTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'grid'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("grid");
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
        @DisplayName("creates GridWidget with grid-size attribute (columns only)")
        void withGridSizeColumnsOnly() {
            Map<String, String> attrs = Map.of("grid-size", "3");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(GridTagHandler.GridWidget.class);
            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates GridWidget with grid-size attribute (columns and rows)")
        void withGridSizeColumnsAndRows() {
            Map<String, String> attrs = Map.of("grid-size", "3 4");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(GridTagHandler.GridWidget.class);
            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isEqualTo("3 4");
        }

        @Test
        @DisplayName("creates GridWidget with grid-columns attribute")
        void withGridColumns() {
            Map<String, String> attrs = Map.of("grid-columns", "fill fill(2) 20");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridColumns()).isEqualTo("fill fill(2) 20");
        }

        @Test
        @DisplayName("creates GridWidget with grid-rows attribute")
        void withGridRows() {
            Map<String, String> attrs = Map.of("grid-rows", "2 3");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridRows()).isEqualTo("2 3");
        }

        @Test
        @DisplayName("creates GridWidget with gutter attribute (uniform)")
        void withGutterUniform() {
            Map<String, String> attrs = Map.of("gutter", "2");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gutter()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates GridWidget with gutter attribute (asymmetric)")
        void withGutterAsymmetric() {
            Map<String, String> attrs = Map.of("gutter", "1 2");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gutter()).isEqualTo("1 2");
        }

        @Test
        @DisplayName("creates GridWidget with grid-template-areas attribute")
        void withGridTemplateAreas() {
            Map<String, String> attrs = Map.of("grid-template-areas", "header header; nav main");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridTemplateAreas()).isEqualTo("header header; nav main");
        }

        @Test
        @DisplayName("creates GridWidget with class attribute")
        void withClassAttribute() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.cssClass()).isEqualTo("border-rounded p-1");
        }

        @Test
        @DisplayName("creates GridWidget with id attribute")
        void withIdAttribute() {
            Map<String, String> attrs = Map.of("id", "main-grid");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.id()).isEqualTo("main-grid");
        }

        @Test
        @DisplayName("creates GridWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("grid-size", "3 2");
            attrs.put("grid-columns", "fill 20 fill");
            attrs.put("grid-rows", "3 fill");
            attrs.put("gutter", "1 2");
            attrs.put("grid-template-areas", "a a b; c c d");
            attrs.put("class", "my-grid");
            attrs.put("id", "dashboard");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isEqualTo("3 2");
            assertThat(widget.gridColumns()).isEqualTo("fill 20 fill");
            assertThat(widget.gridRows()).isEqualTo("3 fill");
            assertThat(widget.gutter()).isEqualTo("1 2");
            assertThat(widget.gridTemplateAreas()).isEqualTo("a a b; c c d");
            assertThat(widget.cssClass()).isEqualTo("my-grid");
            assertThat(widget.id()).isEqualTo("dashboard");
        }

        @Test
        @DisplayName("creates empty GridWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(GridTagHandler.GridWidget.class);
            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isNull();
            assertThat(widget.gridColumns()).isNull();
            assertThat(widget.gridRows()).isNull();
            assertThat(widget.gutter()).isNull();
            assertThat(widget.gridTemplateAreas()).isNull();
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
            attrs.put("grid-size", "  ");
            attrs.put("gutter", "");
            attrs.put("class", "   ");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isNull();
            assertThat(widget.gutter()).isNull();
            assertThat(widget.cssClass()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of("grid-size", "  3  ", "gutter", "  2  ");

            Object result = handler.createElement(attrs);

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) result;
            assertThat(widget.gridSize()).isEqualTo("3");
            assertThat(widget.gutter()).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add child widgets to GridWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("grid-size", "2"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-GridWidget parent")
        void nonGridWidgetParent() {
            // Should not throw when parent is not GridWidget
            handler.addChildren("not a grid", List.of("child"));
        }

        @Test
        @DisplayName("GridWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("can add children incrementally")
        void addChildrenIncrementally() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2", "child3"));

            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }
    }

    @Nested
    @DisplayName("GridWidget")
    class GridWidgetTests {

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            GridTagHandler.GridWidget widget = (GridTagHandler.GridWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }

        @Test
        @DisplayName("all properties can be set and retrieved")
        void allPropertiesWork() {
            GridTagHandler.GridWidget widget = new GridTagHandler.GridWidget();

            widget.setGridSize("4");
            widget.setGridColumns("fill 10");
            widget.setGridRows("3 fill");
            widget.setGutter("1");
            widget.setGridTemplateAreas("a b; c d");
            widget.setCssClass("my-class");
            widget.setId("my-id");

            assertThat(widget.gridSize()).isEqualTo("4");
            assertThat(widget.gridColumns()).isEqualTo("fill 10");
            assertThat(widget.gridRows()).isEqualTo("3 fill");
            assertThat(widget.gutter()).isEqualTo("1");
            assertThat(widget.gridTemplateAreas()).isEqualTo("a b; c d");
            assertThat(widget.cssClass()).isEqualTo("my-class");
            assertThat(widget.id()).isEqualTo("my-id");
        }
    }
}
