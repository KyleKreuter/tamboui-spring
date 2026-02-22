package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:columns>}.
 * Creates a wrapper representing a TamboUI {@code ColumnsElement} which arranges
 * child elements in a multi-column layout with configurable spacing and ordering.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code spacing} - Spacing between columns</li>
 *   <li>{@code flex} - Flex layout configuration</li>
 *   <li>{@code margin} - Margin around the columns container</li>
 *   <li>{@code column-count} - Number of columns</li>
 *   <li>{@code column-order} - Child ordering: "row-first" or "column-first"</li>
 *   <li>{@code class} - CSS class names for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:columns column-count="3" spacing="2" column-order="row-first">
 *     <t:text>Column 1</t:text>
 *     <t:text>Column 2</t:text>
 *     <t:text>Column 3</t:text>
 * </t:columns>
 * }</pre>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class ColumnsTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "columns";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        ColumnsWidget widget = new ColumnsWidget();

        String spacing = attributes.get("spacing");
        if (spacing != null && !spacing.isBlank()) {
            widget.setSpacing(spacing.trim());
        }

        String flex = attributes.get("flex");
        if (flex != null && !flex.isBlank()) {
            widget.setFlex(flex.trim());
        }

        String margin = attributes.get("margin");
        if (margin != null && !margin.isBlank()) {
            widget.setMargin(margin.trim());
        }

        String columnCount = attributes.get("column-count");
        if (columnCount != null && !columnCount.isBlank()) {
            widget.setColumnCount(columnCount.trim());
        }

        String columnOrder = attributes.get("column-order");
        if (columnOrder != null && !columnOrder.isBlank()) {
            widget.setColumnOrder(columnOrder.trim());
        }

        String cssClass = attributes.get("class");
        if (cssClass != null && !cssClass.isBlank()) {
            widget.setCssClass(cssClass.trim());
        }

        String id = attributes.get("id");
        if (id != null && !id.isBlank()) {
            widget.setId(id.trim());
        }

        return widget;
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof ColumnsWidget columnsWidget) {
            columnsWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that holds columns layout configuration and its child widgets.
     * This intermediate representation carries all columns attributes through the
     * rendering pipeline before being converted to TamboUI's {@code ColumnsElement}.
     */
    public static final class ColumnsWidget {
        private String spacing;
        private String flex;
        private String margin;
        private String columnCount;
        private String columnOrder;
        private String cssClass;
        private String id;
        private final List<Object> children = new ArrayList<>();

        /**
         * Returns the spacing between columns.
         *
         * @return the spacing value or {@code null}
         */
        public String spacing() {
            return spacing;
        }

        /**
         * Sets the spacing between columns.
         *
         * @param spacing the spacing value
         */
        public void setSpacing(String spacing) {
            this.spacing = spacing;
        }

        /**
         * Returns the flex layout configuration.
         *
         * @return the flex value or {@code null}
         */
        public String flex() {
            return flex;
        }

        /**
         * Sets the flex layout configuration.
         *
         * @param flex the flex value
         */
        public void setFlex(String flex) {
            this.flex = flex;
        }

        /**
         * Returns the margin around the columns container.
         *
         * @return the margin value or {@code null}
         */
        public String margin() {
            return margin;
        }

        /**
         * Sets the margin around the columns container.
         *
         * @param margin the margin value
         */
        public void setMargin(String margin) {
            this.margin = margin;
        }

        /**
         * Returns the number of columns.
         *
         * @return the column count or {@code null}
         */
        public String columnCount() {
            return columnCount;
        }

        /**
         * Sets the number of columns.
         *
         * @param columnCount the column count
         */
        public void setColumnCount(String columnCount) {
            this.columnCount = columnCount;
        }

        /**
         * Returns the column ordering (row-first or column-first).
         *
         * @return the column order or {@code null}
         */
        public String columnOrder() {
            return columnOrder;
        }

        /**
         * Sets the column ordering.
         *
         * @param columnOrder the column order (row-first or column-first)
         */
        public void setColumnOrder(String columnOrder) {
            this.columnOrder = columnOrder;
        }

        /**
         * Returns the CSS class names.
         *
         * @return the CSS class or {@code null}
         */
        public String cssClass() {
            return cssClass;
        }

        /**
         * Sets the CSS class names.
         *
         * @param cssClass the CSS class string
         */
        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * Returns the element identifier.
         *
         * @return the id or {@code null}
         */
        public String id() {
            return id;
        }

        /**
         * Sets the element identifier.
         *
         * @param id the element id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Returns the mutable list of child widgets.
         *
         * @return the children list
         */
        public List<Object> children() {
            return children;
        }
    }
}
