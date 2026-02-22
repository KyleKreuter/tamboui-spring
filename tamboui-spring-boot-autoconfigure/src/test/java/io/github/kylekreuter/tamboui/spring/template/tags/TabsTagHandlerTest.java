package io.github.kylekreuter.tamboui.spring.template.tags;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TabsTagHandler}.
 */
class TabsTagHandlerTest {

    private TabsTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TabsTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'tabs'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("tabs");
    }

    @Test
    @DisplayName("is a TagHandler but not ParentTagHandler")
    void isNotParentTagHandler() {
        assertThat(handler).isInstanceOf(io.github.kylekreuter.tamboui.spring.template.TagHandler.class);
        assertThat(handler).isNotInstanceOf(io.github.kylekreuter.tamboui.spring.template.ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates TabsWidget with titles attribute")
        void withTitles() {
            Map<String, String> attrs = Map.of("titles", "Home,Settings,About");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TabsTagHandler.TabsWidget.class);
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.titles()).isEqualTo("Home,Settings,About");
        }

        @Test
        @DisplayName("creates TabsWidget with bind attribute")
        void withBind() {
            Map<String, String> attrs = Map.of("bind", "tabsState");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TabsTagHandler.TabsWidget.class);
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.bind()).isEqualTo("tabsState");
            assertThat(widget.titles()).isNull();
        }

        @Test
        @DisplayName("creates TabsWidget with divider attribute")
        void withDivider() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B,C",
                    "divider", " | "
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.divider()).isEqualTo(" | ");
        }

        @Test
        @DisplayName("creates TabsWidget with highlight-color attribute")
        void withHighlightColor() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B",
                    "highlight-color", "YELLOW"
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.highlightColor()).isEqualTo("YELLOW");
        }

        @Test
        @DisplayName("creates TabsWidget with padding attributes")
        void withPadding() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B",
                    "padding-left", " ",
                    "padding-right", " "
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.paddingLeft()).isEqualTo(" ");
            assertThat(widget.paddingRight()).isEqualTo(" ");
        }

        @Test
        @DisplayName("creates TabsWidget with title attribute")
        void withTitle() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B",
                    "title", "Navigation"
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.title()).isEqualTo("Navigation");
        }

        @Test
        @DisplayName("creates TabsWidget with border-type attribute")
        void withBorderType() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B",
                    "border-type", "rounded"
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.borderType()).isEqualTo("rounded");
        }

        @Test
        @DisplayName("creates TabsWidget with border-color attribute")
        void withBorderColor() {
            Map<String, String> attrs = Map.of(
                    "titles", "A,B",
                    "border-color", "CYAN"
            );

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.borderColor()).isEqualTo("CYAN");
        }

        @Test
        @DisplayName("creates TabsWidget with all attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("titles", "Home,Settings,About");
            attrs.put("bind", "tabsState");
            attrs.put("divider", " - ");
            attrs.put("highlight-color", "GREEN");
            attrs.put("padding-left", ">> ");
            attrs.put("padding-right", " <<");
            attrs.put("title", "Menu");
            attrs.put("border-type", "rounded");
            attrs.put("border-color", "BLUE");

            Object result = handler.createElement(attrs);

            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.titles()).isEqualTo("Home,Settings,About");
            assertThat(widget.bind()).isEqualTo("tabsState");
            assertThat(widget.divider()).isEqualTo(" - ");
            assertThat(widget.highlightColor()).isEqualTo("GREEN");
            assertThat(widget.paddingLeft()).isEqualTo(">> ");
            assertThat(widget.paddingRight()).isEqualTo(" <<");
            assertThat(widget.title()).isEqualTo("Menu");
            assertThat(widget.borderType()).isEqualTo("rounded");
            assertThat(widget.borderColor()).isEqualTo("BLUE");
        }

        @Test
        @DisplayName("creates TabsWidget without any attributes")
        void withoutAttributes() {
            Map<String, String> attrs = Map.of();

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(TabsTagHandler.TabsWidget.class);
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget) result;
            assertThat(widget.titles()).isNull();
            assertThat(widget.bind()).isNull();
            assertThat(widget.divider()).isNull();
            assertThat(widget.highlightColor()).isNull();
            assertThat(widget.paddingLeft()).isNull();
            assertThat(widget.paddingRight()).isNull();
            assertThat(widget.title()).isNull();
            assertThat(widget.borderType()).isNull();
            assertThat(widget.borderColor()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("TabsWidget")
    class TabsWidgetTests {

        @Test
        @DisplayName("titles() returns null when not set")
        void titlesNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of("bind", "test"));

            assertThat(widget.titles()).isNull();
        }

        @Test
        @DisplayName("bind() returns null when not set")
        void bindNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of("titles", "A,B"));

            assertThat(widget.bind()).isNull();
        }

        @Test
        @DisplayName("divider() returns null when not set")
        void dividerNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.divider()).isNull();
        }

        @Test
        @DisplayName("highlightColor() returns null when not set")
        void highlightColorNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.highlightColor()).isNull();
        }

        @Test
        @DisplayName("paddingLeft() returns null when not set")
        void paddingLeftNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.paddingLeft()).isNull();
        }

        @Test
        @DisplayName("paddingRight() returns null when not set")
        void paddingRightNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.paddingRight()).isNull();
        }

        @Test
        @DisplayName("title() returns null when not set")
        void titleNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.title()).isNull();
        }

        @Test
        @DisplayName("borderType() returns null when not set")
        void borderTypeNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderType()).isNull();
        }

        @Test
        @DisplayName("borderColor() returns null when not set")
        void borderColorNullWhenNotSet() {
            TabsTagHandler.TabsWidget widget = (TabsTagHandler.TabsWidget)
                    handler.createElement(Map.of());

            assertThat(widget.borderColor()).isNull();
        }
    }
}
