package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import dev.tamboui.widgets.list.ListItem;
import dev.tamboui.widgets.list.ListWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:list>}.
 * Creates a TamboUI {@link ListWidget} with items from child {@code <t:item>} tags.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code class} - CSS class for styling</li>
 *   <li>{@code id} - Element identifier</li>
 *   <li>{@code highlight-symbol} - Symbol shown before the selected item (default: {@code "> "})</li>
 * </ul>
 * <p>
 * Items can be added statically via {@code <t:item>} children or dynamically
 * via the {@code t:each} attribute with a SpEL expression that resolves to a
 * collection of strings. Dynamic iteration is handled by the {@link io.github.kylekreuter.tamboui.spring.template.TemplateEngine}.
 *
 * <pre>{@code
 * <t:list id="myList">
 *   <t:item>First Item</t:item>
 *   <t:item>Second Item</t:item>
 * </t:list>
 * }</pre>
 */
public class ListTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "list";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        ListWidget.Builder builder = ListWidget.builder();

        String highlightSymbol = attributes.get("highlight-symbol");
        if (highlightSymbol != null) {
            builder.highlightSymbol(highlightSymbol);
        }

        return new ListWidgetHolder(builder, attributes.get("id"), attributes.get("class"), highlightSymbol);
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (!(parent instanceof ListWidgetHolder holder)) {
            return;
        }

        List<ListItem> items = new ArrayList<>();
        for (Object child : children) {
            if (child instanceof ListItem listItem) {
                items.add(listItem);
            } else if (child instanceof String text) {
                items.add(ListItem.from(text));
            }
        }
        holder.setItems(items);
    }

    /**
     * Holder that carries the {@link ListWidget.Builder} and collected items
     * through the rendering pipeline. The final {@link ListWidget} is built
     * lazily via {@link #build()} after all children have been added.
     */
    public static final class ListWidgetHolder {
        private final ListWidget.Builder builder;
        private final String id;
        private final String cssClass;
        private final String highlightSymbol;
        private List<ListItem> items = new ArrayList<>();

        ListWidgetHolder(ListWidget.Builder builder, String id, String cssClass, String highlightSymbol) {
            this.builder = builder;
            this.id = id;
            this.cssClass = cssClass;
            this.highlightSymbol = highlightSymbol;
        }

        void setItems(List<ListItem> items) {
            this.items = items;
        }

        /**
         * Returns the element identifier, or {@code null} if not set.
         *
         * @return the element id
         */
        public String id() {
            return id;
        }

        /**
         * Returns the CSS class, or {@code null} if not set.
         *
         * @return the CSS class
         */
        public String cssClass() {
            return cssClass;
        }

        /**
         * Returns the highlight symbol, or {@code null} if not set.
         *
         * @return the highlight symbol
         */
        public String highlightSymbol() {
            return highlightSymbol;
        }

        /**
         * Returns the collected list items.
         *
         * @return the list items
         */
        public List<ListItem> items() {
            return items;
        }

        /**
         * Builds the final {@link ListWidget} from the builder and collected items.
         *
         * @return the built ListWidget
         */
        public ListWidget build() {
            builder.items(items.toArray(new ListItem[0]));
            return builder.build();
        }
    }
}
