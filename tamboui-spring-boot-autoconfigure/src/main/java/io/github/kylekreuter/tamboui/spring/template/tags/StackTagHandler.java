package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:stack>}.
 * Creates a wrapper representing a vertical stack layout container that arranges
 * children on top of each other with configurable alignment and margin.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code alignment} - Content alignment: stretch, center, top-left, top-right,
 *       bottom-left, bottom-right, top-center, bottom-center</li>
 *   <li>{@code margin} - Margin spacing around the stack</li>
 *   <li>{@code class} - CSS class names for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class StackTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "stack";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        StackWidget widget = new StackWidget();

        String alignment = attributes.get("alignment");
        if (alignment != null && !alignment.isBlank()) {
            widget.setAlignment(alignment.trim());
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
        if (parent instanceof StackWidget stackWidget) {
            stackWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that holds stack layout configuration and its child widgets.
     * This intermediate representation carries all stack attributes through the
     * rendering pipeline before being converted to TamboUI's {@code StackElement}.
     */
    public static final class StackWidget {
        private String alignment;
        private String margin;
        private String cssClass;
        private String id;
        private final List<Object> children = new ArrayList<>();

        /**
         * Returns the alignment value (e.g. "stretch", "center", "top-left").
         *
         * @return the alignment or {@code null}
         */
        public String alignment() {
            return alignment;
        }

        /**
         * Sets the alignment value.
         *
         * @param alignment the alignment string
         */
        public void setAlignment(String alignment) {
            this.alignment = alignment;
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
