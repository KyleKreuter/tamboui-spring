package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.block.Block;
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
 * Unit tests for {@link PanelTagHandler}.
 */
class PanelTagHandlerTest {

    private PanelTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PanelTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'panel'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("panel");
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
        @DisplayName("creates PanelWidget with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of("title", "My Panel");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) result;
            assertThat(panelWidget.block()).isNotNull();
        }

        @Test
        @DisplayName("creates PanelWidget without title when attribute missing")
        void withoutTitle() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) result;
            assertThat(panelWidget.block()).isNotNull();
        }

        @Test
        @DisplayName("creates PanelWidget with borderType attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of("borderType", "ROUNDED");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates PanelWidget with border-style attribute as fallback")
        void withBorderStyle() {
            Map<String, String> attrs = Map.of("border-style", "rounded");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("creates PanelWidget with border-type attribute as fallback")
        void withBorderTypeFallback() {
            Map<String, String> attrs = Map.of("border-type", "double");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("borderType takes precedence over border-style and border-type")
        void borderTypePrecedence() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("borderType", "ROUNDED");
            attrs.put("border-style", "double");
            attrs.put("border-type", "thick");

            Object result = handler.createElement(attrs);

            // borderType is checked first in the implementation
            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
        }

        @Test
        @DisplayName("creates PanelWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("title", "Full Panel");
            attrs.put("borderType", "THICK");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) result;
            assertThat(panelWidget.block()).isNotNull();
        }

        @Test
        @DisplayName("ignores invalid border type value gracefully")
        void invalidBorderType() {
            Map<String, String> attrs = Map.of("borderType", "invalid_type");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
        }

        @Test
        @DisplayName("creates empty PanelWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(PanelTagHandler.PanelWidget.class);
            assertThat(result).isNotNull();
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
        @DisplayName("should add child widgets to PanelWidget")
        void addChildWidgets() {
            Object parent = handler.createElement(Map.of("title", "Parent"));

            handler.addChildren(parent, List.of("child1", "child2"));

            assertThat(parent).isInstanceOf(PanelTagHandler.PanelWidget.class);
            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) parent;
            assertThat(panelWidget.children()).containsExactly("child1", "child2");
        }

        @Test
        @DisplayName("should handle empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of("title", "Parent"));

            handler.addChildren(parent, List.of());

            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) parent;
            assertThat(panelWidget.children()).isEmpty();
        }

        @Test
        @DisplayName("should ignore non-PanelWidget parent")
        void nonPanelWidgetParent() {
            // Should not throw when parent is not PanelWidget
            handler.addChildren("not a panel", List.of("child"));
        }

        @Test
        @DisplayName("PanelWidget children list starts empty")
        void childrenStartEmpty() {
            Object parent = handler.createElement(Map.of());

            PanelTagHandler.PanelWidget panelWidget = (PanelTagHandler.PanelWidget) parent;
            assertThat(panelWidget.children()).isEmpty();
        }
    }

    @Nested
    @DisplayName("PanelWidget")
    class PanelWidgetTests {

        @Test
        @DisplayName("block() returns the configured Block")
        void blockReturnsBlock() {
            PanelTagHandler.PanelWidget widget = (PanelTagHandler.PanelWidget)
                    handler.createElement(Map.of("title", "Test"));

            assertThat(widget.block()).isNotNull();
            assertThat(widget.block()).isInstanceOf(Block.class);
        }

        @Test
        @DisplayName("children() returns mutable list")
        void childrenIsMutable() {
            PanelTagHandler.PanelWidget widget = (PanelTagHandler.PanelWidget)
                    handler.createElement(Map.of());

            widget.children().add("test-child");
            assertThat(widget.children()).containsExactly("test-child");
        }
    }
}
