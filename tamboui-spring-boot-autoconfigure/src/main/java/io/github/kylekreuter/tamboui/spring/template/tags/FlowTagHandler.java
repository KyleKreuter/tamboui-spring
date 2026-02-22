package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:flow>}.
 * Creates a wrapper representing a flow layout container that arranges children
 * in a wrapping horizontal flow with configurable spacing and margin.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code spacing} - Horizontal spacing between items</li>
 *   <li>{@code row-spacing} - Vertical spacing between rows</li>
 *   <li>{@code margin} - Margin spacing around the flow container</li>
 *   <li>{@code class} - CSS class names for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class FlowTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "flow";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        FlowWidget widget = new FlowWidget();

        String spacing = attributes.get("spacing");
        if (spacing != null && !spacing.isBlank()) {
            widget.setSpacing(spacing.trim());
        }

        String rowSpacing = attributes.get("row-spacing");
        if (rowSpacing != null && !rowSpacing.isBlank()) {
            widget.setRowSpacing(rowSpacing.trim());
        }

        String margin = attributes.get("margin");
        if (margin != null && !margin.isBlank()) {
            widget.setMargin(margin.trim());
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
        if (parent instanceof FlowWidget flowWidget) {
            flowWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that holds flow layout configuration and its child widgets.
     * This intermediate representation carries all flow attributes through the
     * rendering pipeline before being converted to TamboUI's {@code FlowElement}.
     */
    public static final class FlowWidget {
        private String spacing;
        private String rowSpacing;
        private String margin;
        private String cssClass;
        private String id;
        private final List<Object> children = new ArrayList<>();

        /**
         * Returns the horizontal spacing between items.
         *
         * @return the spacing or {@code null}
         */
        public String spacing() {
            return spacing;
        }

        /**
         * Sets the horizontal spacing between items.
         *
         * @param spacing the spacing string
         */
        public void setSpacing(String spacing) {
            this.spacing = spacing;
        }

        /**
         * Returns the vertical spacing between rows.
         *
         * @return the row spacing or {@code null}
         */
        public String rowSpacing() {
            return rowSpacing;
        }

        /**
         * Sets the vertical spacing between rows.
         *
         * @param rowSpacing the row spacing string
         */
        public void setRowSpacing(String rowSpacing) {
            this.rowSpacing = rowSpacing;
        }

        /**
         * Returns the margin value.
         *
         * @return the margin or {@code null}
         */
        public String margin() {
            return margin;
        }

        /**
         * Sets the margin value.
         *
         * @param margin the margin string
         */
        public void setMargin(String margin) {
            this.margin = margin;
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
