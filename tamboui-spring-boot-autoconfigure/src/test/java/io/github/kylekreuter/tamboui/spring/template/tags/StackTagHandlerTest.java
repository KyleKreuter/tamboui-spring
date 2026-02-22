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
 * Unit tests for {@link StackTagHandler}.
 */
class StackTagHandlerTest {

    private StackTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StackTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'stack'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("stack");
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
        @DisplayName("creates StackWidget with alignment 'stretch'")
        void withAlignmentStretch() {
            Map<String, String> attrs = Map.of("alignment", "stretch");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(StackTagHandler.StackWidget.class);
            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("stretch");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'center'")
        void withAlignmentCenter() {
            Map<String, String> attrs = Map.of("alignment", "center");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("center");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'top-left'")
        void withAlignmentTopLeft() {
            Map<String, String> attrs = Map.of("alignment", "top-left");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("top-left");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'top-right'")
        void withAlignmentTopRight() {
            Map<String, String> attrs = Map.of("alignment", "top-right");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("top-right");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'bottom-left'")
        void withAlignmentBottomLeft() {
            Map<String, String> attrs = Map.of("alignment", "bottom-left");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("bottom-left");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'bottom-right'")
        void withAlignmentBottomRight() {
            Map<String, String> attrs = Map.of("alignment", "bottom-right");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("bottom-right");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'top-center'")
        void withAlignmentTopCenter() {
            Map<String, String> attrs = Map.of("alignment", "top-center");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("top-center");
        }

        @Test
        @DisplayName("creates StackWidget with alignment 'bottom-center'")
        void withAlignmentBottomCenter() {
            Map<String, String> attrs = Map.of("alignment", "bottom-center");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("bottom-center");
        }

        @Test
        @DisplayName("creates StackWidget with margin attribute")
        void withMarginAttribute() {
            Map<String, String> attrs = Map.of("margin", "2");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.margin()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates StackWidget with class attribute")
        void withClassAttribute() {
            Map<String, String> attrs = Map.of("class", "border-rounded p-1");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.cssClass()).isEqualTo("border-rounded p-1");
        }

        @Test
        @DisplayName("creates StackWidget with id attribute")
        void withIdAttribute() {
            Map<String, String> attrs = Map.of("id", "main-stack");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.id()).isEqualTo("main-stack");
        }

        @Test
        @DisplayName("creates StackWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("alignment", "center");
            attrs.put("margin", "3");
            attrs.put("class", "my-stack");
            attrs.put("id", "dashboard-stack");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("center");
            assertThat(widget.margin()).isEqualTo("3");
            assertThat(widget.cssClass()).isEqualTo("my-stack");
            assertThat(widget.id()).isEqualTo("dashboard-stack");
        }

        @Test
        @DisplayName("creates empty StackWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(StackTagHandler.StackWidget.class);
            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isNull();
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
            attrs.put("alignment", "  ");
            attrs.put("margin", "");
            attrs.put("class", "   ");
            attrs.put("id", "  ");

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isNull();
            assertThat(widget.margin()).isNull();
            assertThat(widget.cssClass()).isNull();
            assertThat(widget.id()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of(
                    "alignment", "  center  ",
                    "margin", "  2  "
            );

            Object result = handler.createElement(attrs);

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) result;
            assertThat(widget.alignment()).isEqualTo("center");
            assertThat(widget.margin()).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add child widgets to StackWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("alignment", "stretch"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-StackWidget parent")
        void nonStackWidgetParent() {
            // Should not throw when parent is not StackWidget
            handler.addChildren("not a stack", List.of("child"));
        }

        @Test
        @DisplayName("StackWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("can add children incrementally")
        void addChildrenIncrementally() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2", "child3"));

            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }
    }

    @Nested
    @DisplayName("StackWidget")
    class StackWidgetTests {

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            StackTagHandler.StackWidget widget = (StackTagHandler.StackWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }

        @Test
        @DisplayName("all properties can be set and retrieved")
        void allPropertiesWork() {
            StackTagHandler.StackWidget widget = new StackTagHandler.StackWidget();

            widget.setAlignment("top-left");
            widget.setMargin("5");
            widget.setCssClass("my-class");
            widget.setId("my-id");

            assertThat(widget.alignment()).isEqualTo("top-left");
            assertThat(widget.margin()).isEqualTo("5");
            assertThat(widget.cssClass()).isEqualTo("my-class");
            assertThat(widget.id()).isEqualTo("my-id");
        }
    }
}
