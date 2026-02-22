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
 * Unit tests for {@link DialogTagHandler}.
 */
class DialogTagHandlerTest {

    private DialogTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DialogTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'dialog'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("dialog");
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
        @DisplayName("creates DialogWidget with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of("title", "Confirm");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(DialogTagHandler.DialogWidget.class);
            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.title()).isEqualTo("Confirm");
        }

        @Test
        @DisplayName("creates DialogWidget with border-type attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of("border-type", "rounded");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.borderType()).isEqualTo("rounded");
        }

        @Test
        @DisplayName("creates DialogWidget with border-color attribute")
        void withBorderColor() {
            Map<String, String> attrs = Map.of("border-color", "blue");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.borderColor()).isEqualTo("blue");
        }

        @Test
        @DisplayName("creates DialogWidget with width attribute")
        void withWidth() {
            Map<String, String> attrs = Map.of("width", "40");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.width()).isEqualTo("40");
        }

        @Test
        @DisplayName("creates DialogWidget with height attribute")
        void withHeight() {
            Map<String, String> attrs = Map.of("height", "20");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.height()).isEqualTo("20");
        }

        @Test
        @DisplayName("creates DialogWidget with min-width attribute")
        void withMinWidth() {
            Map<String, String> attrs = Map.of("min-width", "30");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.minWidth()).isEqualTo("30");
        }

        @Test
        @DisplayName("creates DialogWidget with padding attribute")
        void withPadding() {
            Map<String, String> attrs = Map.of("padding", "2");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.padding()).isEqualTo("2");
        }

        @Test
        @DisplayName("creates DialogWidget with direction attribute (horizontal)")
        void withDirectionHorizontal() {
            Map<String, String> attrs = Map.of("direction", "horizontal");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.direction()).isEqualTo("horizontal");
        }

        @Test
        @DisplayName("creates DialogWidget with direction attribute (vertical)")
        void withDirectionVertical() {
            Map<String, String> attrs = Map.of("direction", "vertical");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.direction()).isEqualTo("vertical");
        }

        @Test
        @DisplayName("creates DialogWidget with flex attribute")
        void withFlex() {
            Map<String, String> attrs = Map.of("flex", "stretch");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.flex()).isEqualTo("stretch");
        }

        @Test
        @DisplayName("creates DialogWidget with spacing attribute")
        void withSpacing() {
            Map<String, String> attrs = Map.of("spacing", "1");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.spacing()).isEqualTo("1");
        }

        @Test
        @DisplayName("creates DialogWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("title", "Settings");
            attrs.put("border-type", "double");
            attrs.put("border-color", "red");
            attrs.put("width", "50");
            attrs.put("height", "25");
            attrs.put("min-width", "30");
            attrs.put("padding", "2");
            attrs.put("direction", "vertical");
            attrs.put("flex", "center");
            attrs.put("spacing", "1");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.title()).isEqualTo("Settings");
            assertThat(widget.borderType()).isEqualTo("double");
            assertThat(widget.borderColor()).isEqualTo("red");
            assertThat(widget.width()).isEqualTo("50");
            assertThat(widget.height()).isEqualTo("25");
            assertThat(widget.minWidth()).isEqualTo("30");
            assertThat(widget.padding()).isEqualTo("2");
            assertThat(widget.direction()).isEqualTo("vertical");
            assertThat(widget.flex()).isEqualTo("center");
            assertThat(widget.spacing()).isEqualTo("1");
        }

        @Test
        @DisplayName("creates empty DialogWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(DialogTagHandler.DialogWidget.class);
            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.title()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.borderColor()).isNull();
            assertThat(widget.width()).isNull();
            assertThat(widget.height()).isNull();
            assertThat(widget.minWidth()).isNull();
            assertThat(widget.padding()).isNull();
            assertThat(widget.direction()).isNull();
            assertThat(widget.flex()).isNull();
            assertThat(widget.spacing()).isNull();
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
            attrs.put("title", "  ");
            attrs.put("border-type", "");
            attrs.put("width", "   ");
            attrs.put("direction", "  ");

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.title()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.width()).isNull();
            assertThat(widget.direction()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of(
                    "title", "  My Dialog  ",
                    "border-type", "  rounded  ",
                    "width", "  40  ",
                    "spacing", "  2  "
            );

            Object result = handler.createElement(attrs);

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) result;
            assertThat(widget.title()).isEqualTo("My Dialog");
            assertThat(widget.borderType()).isEqualTo("rounded");
            assertThat(widget.width()).isEqualTo("40");
            assertThat(widget.spacing()).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("should add child widgets to DialogWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("title", "Test"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-DialogWidget parent")
        void nonDialogWidgetParent() {
            // Should not throw when parent is not DialogWidget
            handler.addChildren("not a dialog", List.of("child"));
        }

        @Test
        @DisplayName("DialogWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) parent;
            assertThat(widget.children()).isEmpty();
        }

        @Test
        @DisplayName("can add children incrementally")
        void addChildrenIncrementally() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2", "child3"));

            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget) parent;
            assertThat(widget.children()).containsExactly("child1", "child2", "child3");
        }
    }

    @Nested
    @DisplayName("DialogWidget")
    class DialogWidgetTests {

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            DialogTagHandler.DialogWidget widget = (DialogTagHandler.DialogWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }

        @Test
        @DisplayName("all properties can be set and retrieved")
        void allPropertiesWork() {
            DialogTagHandler.DialogWidget widget = new DialogTagHandler.DialogWidget();

            widget.setTitle("Test");
            widget.setBorderType("plain");
            widget.setBorderColor("green");
            widget.setWidth("60");
            widget.setHeight("30");
            widget.setMinWidth("40");
            widget.setPadding("3");
            widget.setDirection("horizontal");
            widget.setFlex("start");
            widget.setSpacing("2");

            assertThat(widget.title()).isEqualTo("Test");
            assertThat(widget.borderType()).isEqualTo("plain");
            assertThat(widget.borderColor()).isEqualTo("green");
            assertThat(widget.width()).isEqualTo("60");
            assertThat(widget.height()).isEqualTo("30");
            assertThat(widget.minWidth()).isEqualTo("40");
            assertThat(widget.padding()).isEqualTo("3");
            assertThat(widget.direction()).isEqualTo("horizontal");
            assertThat(widget.flex()).isEqualTo("start");
            assertThat(widget.spacing()).isEqualTo("2");
        }
    }
}
