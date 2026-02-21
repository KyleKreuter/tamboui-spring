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
 * Unit tests for {@link FormTagHandler}.
 */
class FormTagHandlerTest {

    private FormTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FormTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'form'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("form");
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
        @DisplayName("creates FormWidget with bind attribute")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "settingsForm");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormTagHandler.FormWidget.class);
            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) result;
            assertThat(formWidget.bind()).isEqualTo("settingsForm");
        }

        @Test
        @DisplayName("creates FormWidget without bind attribute")
        void withoutBind() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(FormTagHandler.FormWidget.class);
            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) result;
            assertThat(formWidget.bind()).isNull();
        }

        @Test
        @DisplayName("creates FormWidget with empty children list")
        void emptyChildren() {
            Object result = handler.createElement(Map.of("bind", "test"));

            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) result;
            assertThat(formWidget.children()).isEmpty();
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
        @DisplayName("should add child widgets to FormWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("bind", "form"));

            handler.addChildren(parent, List.of("child1", "child2", "child3"));

            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) parent;
            assertThat(formWidget.children()).containsExactly("child1", "child2", "child3");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of("bind", "form"));

            handler.addChildren(parent, List.of());

            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) parent;
            assertThat(formWidget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-FormWidget parent")
        void nonFormWidgetParent() {
            // Should not throw when parent is not FormWidget
            handler.addChildren("not a form", List.of("child"));
        }

        @Test
        @DisplayName("should accumulate children across multiple calls")
        void accumulateChildren() {
            Object parent = handler.createElement(Map.of("bind", "form"));

            handler.addChildren(parent, List.of("child1"));
            handler.addChildren(parent, List.of("child2"));

            FormTagHandler.FormWidget formWidget = (FormTagHandler.FormWidget) parent;
            assertThat(formWidget.children()).containsExactly("child1", "child2");
        }
    }

    @Nested
    @DisplayName("FormWidget")
    class FormWidgetTests {

        @Test
        @DisplayName("bind() returns the configured bind name")
        void bindReturnsBind() {
            FormTagHandler.FormWidget widget = (FormTagHandler.FormWidget)
                    handler.createElement(Map.of("bind", "myForm"));

            assertThat(widget.bind()).isEqualTo("myForm");
        }

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            FormTagHandler.FormWidget widget = (FormTagHandler.FormWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }
    }
}
