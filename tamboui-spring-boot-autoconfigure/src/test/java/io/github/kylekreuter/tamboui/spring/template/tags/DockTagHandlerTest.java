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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link DockTagHandler}.
 */
class DockTagHandlerTest {

    private DockTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DockTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'dock'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("dock");
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
        @DisplayName("creates DockWidget with top-height attribute")
        void withTopHeight() {
            Map<String, String> attrs = Map.of("top-height", "3");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(DockTagHandler.DockWidget.class);
            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.topHeight()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates DockWidget with bottom-height attribute")
        void withBottomHeight() {
            Map<String, String> attrs = Map.of("bottom-height", "3");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.bottomHeight()).isEqualTo("3");
        }

        @Test
        @DisplayName("creates DockWidget with left-width attribute")
        void withLeftWidth() {
            Map<String, String> attrs = Map.of("left-width", "20");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.leftWidth()).isEqualTo("20");
        }

        @Test
        @DisplayName("creates DockWidget with right-width attribute")
        void withRightWidth() {
            Map<String, String> attrs = Map.of("right-width", "15");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.rightWidth()).isEqualTo("15");
        }

        @Test
        @DisplayName("creates DockWidget with class attribute")
        void withClassAttribute() {
            Map<String, String> attrs = Map.of("class", "my-dock");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.cssClass()).isEqualTo("my-dock");
        }

        @Test
        @DisplayName("creates DockWidget with id attribute")
        void withIdAttribute() {
            Map<String, String> attrs = Map.of("id", "main-dock");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.id()).isEqualTo("main-dock");
        }

        @Test
        @DisplayName("creates DockWidget with all supported attributes")
        void withAllAttributes() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("top-height", "3");
            attrs.put("bottom-height", "2");
            attrs.put("left-width", "20");
            attrs.put("right-width", "15");
            attrs.put("class", "app-layout");
            attrs.put("id", "main-layout");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.topHeight()).isEqualTo("3");
            assertThat(widget.bottomHeight()).isEqualTo("2");
            assertThat(widget.leftWidth()).isEqualTo("20");
            assertThat(widget.rightWidth()).isEqualTo("15");
            assertThat(widget.cssClass()).isEqualTo("app-layout");
            assertThat(widget.id()).isEqualTo("main-layout");
        }

        @Test
        @DisplayName("creates empty DockWidget with no attributes")
        void emptyAttributes() {
            Object result = handler.createElement(new HashMap<>());

            assertThat(result).isInstanceOf(DockTagHandler.DockWidget.class);
            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.topHeight()).isNull();
            assertThat(widget.bottomHeight()).isNull();
            assertThat(widget.leftWidth()).isNull();
            assertThat(widget.rightWidth()).isNull();
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
            attrs.put("top-height", "  ");
            attrs.put("left-width", "");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.topHeight()).isNull();
            assertThat(widget.leftWidth()).isNull();
        }

        @Test
        @DisplayName("trims attribute values")
        void trimsValues() {
            Map<String, String> attrs = Map.of("top-height", "  3  ", "left-width", " 20 ");

            Object result = handler.createElement(attrs);

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) result;
            assertThat(widget.topHeight()).isEqualTo("3");
            assertThat(widget.leftWidth()).isEqualTo("20");
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("assigns RegionChild to the correct region")
        void assignRegionChild() {
            Object parent = handler.createElement(Map.of());
            DockTagHandler.RegionChild topChild = new DockTagHandler.RegionChild("top", "header");
            DockTagHandler.RegionChild bottomChild = new DockTagHandler.RegionChild("bottom", "footer");

            handler.addChildren(parent, List.of(topChild, bottomChild));

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.top()).isEqualTo("header");
            assertThat(widget.bottom()).isEqualTo("footer");
        }

        @Test
        @DisplayName("assigns all five regions")
        void assignAllRegions() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of(
                    new DockTagHandler.RegionChild("top", "header"),
                    new DockTagHandler.RegionChild("bottom", "footer"),
                    new DockTagHandler.RegionChild("left", "sidebar"),
                    new DockTagHandler.RegionChild("right", "aside"),
                    new DockTagHandler.RegionChild("center", "content")
            ));

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.top()).isEqualTo("header");
            assertThat(widget.bottom()).isEqualTo("footer");
            assertThat(widget.left()).isEqualTo("sidebar");
            assertThat(widget.right()).isEqualTo("aside");
            assertThat(widget.center()).isEqualTo("content");
        }

        @Test
        @DisplayName("defaults non-RegionChild to center")
        void defaultsToCenter() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("plain-widget"));

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.center()).isEqualTo("plain-widget");
        }

        @Test
        @DisplayName("handles empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.regions()).isEmpty();
        }

        @Test
        @DisplayName("ignores non-DockWidget parent")
        void nonDockWidgetParent() {
            // Should not throw when parent is not DockWidget
            handler.addChildren("not a dock", List.of("child"));
        }

        @Test
        @DisplayName("last child for same region wins")
        void lastChildForRegionWins() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of(
                    new DockTagHandler.RegionChild("top", "first"),
                    new DockTagHandler.RegionChild("top", "second")
            ));

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.top()).isEqualTo("second");
        }

        @Test
        @DisplayName("mixed RegionChild and plain objects")
        void mixedChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of(
                    new DockTagHandler.RegionChild("top", "header"),
                    "plain-content" // defaults to center
            ));

            DockTagHandler.DockWidget widget = (DockTagHandler.DockWidget) parent;
            assertThat(widget.top()).isEqualTo("header");
            assertThat(widget.center()).isEqualTo("plain-content");
        }
    }

    @Nested
    @DisplayName("RegionChild")
    class RegionChildTests {

        @Test
        @DisplayName("normalizes region name to lowercase")
        void normalizesToLowercase() {
            DockTagHandler.RegionChild child = new DockTagHandler.RegionChild("TOP", "widget");
            assertThat(child.region()).isEqualTo("top");
        }

        @Test
        @DisplayName("trims region name")
        void trimsRegionName() {
            DockTagHandler.RegionChild child = new DockTagHandler.RegionChild("  left  ", "widget");
            assertThat(child.region()).isEqualTo("left");
        }

        @Test
        @DisplayName("throws on null region")
        void throwsOnNullRegion() {
            assertThatThrownBy(() -> new DockTagHandler.RegionChild(null, "widget"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Region must not be null");
        }

        @Test
        @DisplayName("throws on blank region")
        void throwsOnBlankRegion() {
            assertThatThrownBy(() -> new DockTagHandler.RegionChild("  ", "widget"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Region must not be null");
        }

        @Test
        @DisplayName("throws on null child")
        void throwsOnNullChild() {
            assertThatThrownBy(() -> new DockTagHandler.RegionChild("top", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Child must not be null");
        }

        @Test
        @DisplayName("isValidRegion returns true for valid regions")
        void validRegions() {
            assertThat(new DockTagHandler.RegionChild("top", "w").isValidRegion()).isTrue();
            assertThat(new DockTagHandler.RegionChild("bottom", "w").isValidRegion()).isTrue();
            assertThat(new DockTagHandler.RegionChild("left", "w").isValidRegion()).isTrue();
            assertThat(new DockTagHandler.RegionChild("right", "w").isValidRegion()).isTrue();
            assertThat(new DockTagHandler.RegionChild("center", "w").isValidRegion()).isTrue();
        }

        @Test
        @DisplayName("isValidRegion returns false for invalid regions")
        void invalidRegions() {
            assertThat(new DockTagHandler.RegionChild("middle", "w").isValidRegion()).isFalse();
            assertThat(new DockTagHandler.RegionChild("header", "w").isValidRegion()).isFalse();
        }
    }

    @Nested
    @DisplayName("DockWidget")
    class DockWidgetTests {

        @Test
        @DisplayName("regions() returns immutable copy")
        void regionsIsImmutable() {
            DockTagHandler.DockWidget widget = new DockTagHandler.DockWidget();
            widget.setRegion("top", "header");

            Map<String, Object> regions = widget.regions();
            assertThatThrownBy(() -> regions.put("bottom", "footer"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getRegion is case-insensitive")
        void getRegionCaseInsensitive() {
            DockTagHandler.DockWidget widget = new DockTagHandler.DockWidget();
            widget.setRegion("TOP", "header");

            assertThat(widget.getRegion("top")).isEqualTo("header");
        }

        @Test
        @DisplayName("getRegion returns null for unset region")
        void getRegionReturnsNull() {
            DockTagHandler.DockWidget widget = new DockTagHandler.DockWidget();

            assertThat(widget.getRegion("top")).isNull();
            assertThat(widget.top()).isNull();
            assertThat(widget.bottom()).isNull();
            assertThat(widget.left()).isNull();
            assertThat(widget.right()).isNull();
            assertThat(widget.center()).isNull();
        }

        @Test
        @DisplayName("all constraint properties can be set and retrieved")
        void allPropertiesWork() {
            DockTagHandler.DockWidget widget = new DockTagHandler.DockWidget();

            widget.setTopHeight("3");
            widget.setBottomHeight("2");
            widget.setLeftWidth("20");
            widget.setRightWidth("15");
            widget.setCssClass("my-class");
            widget.setId("my-id");

            assertThat(widget.topHeight()).isEqualTo("3");
            assertThat(widget.bottomHeight()).isEqualTo("2");
            assertThat(widget.leftWidth()).isEqualTo("20");
            assertThat(widget.rightWidth()).isEqualTo("15");
            assertThat(widget.cssClass()).isEqualTo("my-class");
            assertThat(widget.id()).isEqualTo("my-id");
        }
    }
}
