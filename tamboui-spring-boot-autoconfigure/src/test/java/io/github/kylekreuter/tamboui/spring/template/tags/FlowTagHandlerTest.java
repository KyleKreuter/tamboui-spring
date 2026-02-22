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
 * Unit tests for {@link FlowTagHandler}.
 */
class FlowTagHandlerTest {

    private FlowTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FlowTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'flow'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("flow");
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
        @DisplayName("creates FlowWidget with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FlowTagHandler.FlowWidget.class);
            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates FlowWidget with row-spacing attribute")
        void withRowSpacing() {
            Map<String, String> attrs = Map.of("row-spacing", "3");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.rowSpacing()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates FlowWidget with margin attribute")
        void withMarginAttribute() {
            Map<String, String> attrs = Map.of("margin", "4");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.margin()).isEqualTo("4");
        }

        @Test
        @DisplayName("creates FlowWidget with class attribute")
        void withClassAttribute() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.cssClass()).isEqualTo("border-rounded p-1");
        }

        @Test
        @DisplayName("creates FlowWidget with id attribute")
        void withIdAttribute() {
            Map<String, String> attrs = Map.of("id", "main-flow");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.id()).isEqualTo("main-flow");
        }

        @Test
        @DisplayName("creates FlowWidget with spacing and row-spacing combined")
        void withSpacingAndRowSpacing() {
            Map<String, String> attrs = Map.of("spacing", "2", "row-spacing", "4");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
            assertThat(widget.rowSpacing()).isEqualTo("4");
        }

        @Test
        @DisplayName("creates FlowWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("spacing", "2");
            attrs.put("row-spacing", "3");
            attrs.put("margin", "1");
            attrs.put("class", "my-flow");
            attrs.put("id", "tag-flow");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
            assertThat(widget.rowSpacing()).isEqualTo("3");
            assertThat(widget.margin()).isEqualTo("1");
            assertThat(widget.cssClass()).isEqualTo("my-flow");
            assertThat(widget.id()).isEqualTo("tag-flow");
        }

        @Test
        @DisplayName("creates empty FlowWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(FlowTagHandler.FlowWidget.class);
            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isNull();
            assertThat(widget.rowSpacing()).isNull();
            assertThat(widget.margin()).isNull();
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
            attrs.put("row-spacing", "");
            attrs.put("margin", "   ");
            attrs.put("class", "  ");
            attrs.put("id", "");

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isNull();
            assertThat(widget.rowSpacing()).isNull();
            assertThat(widget.margin()).isNull();
            assertThat(widget.cssClass()).isNull();
            assertThat(widget.id()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of(
                    "spacing", "  2  ",
                    "row-spacing", "  3  ",
                    "margin", "  1  "
            );

            Object result = handler.createElement(attrs);

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) result;
            assertThat(widget.spacing()).isEqualTo("2");
            assertThat(widget.rowSpacing()).isEqualTo("3");
            assertThat(widget.margin()).isEqualTo("1");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add child widgets to FlowWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("spacing", "2"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-FlowWidget parent")
        void nonFlowWidgetParent() {
            // Should not throw when parent is not FlowWidget
            handler.addChildren("not a flow", List.of("child"));
        }

        @Test
        @DisplayName("FlowWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("can add children incrementally")
        void addChildrenIncrementally() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2", "child3"));

            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }
    }

    @Nested
    @DisplayName("FlowWidget")
    class FlowWidgetTests {

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            FlowTagHandler.FlowWidget widget = (FlowTagHandler.FlowWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }

        @Test
        @DisplayName("all properties can be set and retrieved")
        void allPropertiesWork() {
            FlowTagHandler.FlowWidget widget = new FlowTagHandler.FlowWidget();

            widget.setSpacing("5");
            widget.setRowSpacing("3");
            widget.setMargin("2");
            widget.setCssClass("my-class");
            widget.setId("my-id");

            assertThat(widget.spacing()).isEqualTo("5");
            assertThat(widget.rowSpacing()).isEqualTo("3");
            assertThat(widget.margin()).isEqualTo("2");
            assertThat(widget.cssClass()).isEqualTo("my-class");
            assertThat(widget.id()).isEqualTo("my-id");
        }
    }
}
