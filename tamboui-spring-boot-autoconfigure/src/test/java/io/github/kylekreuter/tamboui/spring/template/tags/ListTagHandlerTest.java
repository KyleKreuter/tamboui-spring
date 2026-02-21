package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.list.ListItem;
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
 * Unit tests for {@link ListTagHandler}.
 */
class ListTagHandlerTest {

    private ListTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ListTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'list'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("list");
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
        @DisplayName("creates ListWidgetHolder with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(ListTagHandler.ListWidgetHolder.class);
        }

        @Test
        @DisplayName("creates ListWidgetHolder with id attribute")
        void withIdAttribute() {
            Object result = handler.createElement(Map.of("id", "myList"));

            assertThat(result).isInstanceOf(ListTagHandler.ListWidgetHolder.class);
            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) result;
            assertThat(holder.id()).isEqualTo("myList");
        }

        @Test
        @DisplayName("creates ListWidgetHolder with class attribute")
        void withClassAttribute() {
            Object result = handler.createElement(Map.of("class", "styled-list"));

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) result;
            assertThat(holder.cssClass()).isEqualTo("styled-list");
        }

        @Test
        @DisplayName("creates ListWidgetHolder with highlight-symbol attribute")
        void withHighlightSymbol() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("highlight-symbol", ">> ");

            Object result = handler.createElement(attrs);

            assertThat(result).isInstanceOf(ListTagHandler.ListWidgetHolder.class);
        }

        @Test
        @DisplayName("id and class are null when not provided")
        void nullIdAndClass() {
            ListTagHandler.ListWidgetHolder holder =
                    (ListTagHandler.ListWidgetHolder) handler.createElement(Map.of());

            assertThat(holder.id()).isNull();
            assertThat(holder.cssClass()).isNull();
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
        @DisplayName("adds ListItem children to holder")
        void addListItemChildren() {
            Object parent = handler.createElement(Map.of());
            ListItem item1 = ListItem.from("Item 1");
            ListItem item2 = ListItem.from("Item 2");

            handler.addChildren(parent, List.of(item1, item2));

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) parent;
            assertThat(holder.items()).containsExactly(item1, item2);
        }

        @Test
        @DisplayName("converts String children to ListItems")
        void convertsStringChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("Text Item 1", "Text Item 2"));

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) parent;
            assertThat(holder.items()).hasSize(2);
            assertThat(holder.items().get(0)).isEqualTo(ListItem.from("Text Item 1"));
            assertThat(holder.items().get(1)).isEqualTo(ListItem.from("Text Item 2"));
        }

        @Test
        @DisplayName("handles mixed ListItem and String children")
        void handlesMixedChildren() {
            Object parent = handler.createElement(Map.of());
            ListItem item = ListItem.from("ListItem");

            handler.addChildren(parent, List.of(item, "StringItem"));

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) parent;
            assertThat(holder.items()).hasSize(2);
            assertThat(holder.items().get(0)).isEqualTo(ListItem.from("ListItem"));
            assertThat(holder.items().get(1)).isEqualTo(ListItem.from("StringItem"));
        }

        @Test
        @DisplayName("handles empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) parent;
            assertThat(holder.items()).isEmpty();
        }

        @Test
        @DisplayName("ignores non-ListWidgetHolder parent")
        void nonHolderParent() {
            // Should not throw when parent is not ListWidgetHolder
            handler.addChildren("not a holder", List.of(ListItem.from("child")));
        }

        @Test
        @DisplayName("ignores unsupported child types")
        void ignoresUnsupportedChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of(42, 3.14));

            ListTagHandler.ListWidgetHolder holder = (ListTagHandler.ListWidgetHolder) parent;
            assertThat(holder.items()).isEmpty();
        }
    }

    @Nested
    @DisplayName("ListWidgetHolder")
    class ListWidgetHolderTests {

        @Test
        @DisplayName("items starts empty")
        void itemsStartEmpty() {
            ListTagHandler.ListWidgetHolder holder =
                    (ListTagHandler.ListWidgetHolder) handler.createElement(Map.of());

            assertThat(holder.items()).isEmpty();
        }

        @Test
        @DisplayName("build creates a ListWidget")
        void buildCreatesListWidget() {
            ListTagHandler.ListWidgetHolder holder =
                    (ListTagHandler.ListWidgetHolder) handler.createElement(Map.of());
            handler.addChildren(holder, List.of(ListItem.from("A"), ListItem.from("B")));

            Object widget = holder.build();

            assertThat(widget).isNotNull();
            assertThat(widget).isInstanceOf(dev.tamboui.widgets.list.ListWidget.class);
        }

        @Test
        @DisplayName("build with no items creates empty ListWidget")
        void buildEmptyCreatesListWidget() {
            ListTagHandler.ListWidgetHolder holder =
                    (ListTagHandler.ListWidgetHolder) handler.createElement(Map.of());

            Object widget = holder.build();

            assertThat(widget).isNotNull();
            assertThat(widget).isInstanceOf(dev.tamboui.widgets.list.ListWidget.class);
        }
    }
}
