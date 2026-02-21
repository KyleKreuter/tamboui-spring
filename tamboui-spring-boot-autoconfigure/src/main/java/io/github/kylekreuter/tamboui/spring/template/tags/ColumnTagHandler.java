package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:column>}.
 * Creates a {@link ColumnWidget} wrapper that the {@link
 * io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter WidgetToElementConverter}
 * converts into a TamboUI {@link dev.tamboui.toolkit.elements.Column Column} element.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code spacing} - Gap between children in cells (integer)</li>
 *   <li>{@code flex} - Flex positioning mode: START, CENTER, END, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY</li>
 *   <li>{@code margin} - Uniform margin around the column (integer)</li>
 *   <li>{@code class} - CSS class for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 */
public class ColumnTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "column";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        return new ColumnWidget(
                attributes.get("spacing"),
                attributes.get("flex"),
                attributes.get("margin"),
                attributes.get("id"),
                attributes.get("class")
        );
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof ColumnWidget columnWidget) {
            columnWidget.children().addAll(children);
        }
    }

    /**
     * Intermediate wrapper holding column configuration and children.
     * Converted to a {@link dev.tamboui.toolkit.elements.Column} by the
     * {@link io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter}.
     */
    public static final class ColumnWidget {
        private final String spacing;
        private final String flex;
        private final String margin;
        private final String id;
        private final String cssClass;
        private final List<Object> children = new ArrayList<>();

        public ColumnWidget(String spacing, String flex, String margin, String id, String cssClass) {
            this.spacing = spacing;
            this.flex = flex;
            this.margin = margin;
            this.id = id;
            this.cssClass = cssClass;
        }

        public String spacing() { return spacing; }
        public String flex() { return flex; }
        public String margin() { return margin; }
        public String id() { return id; }
        public String cssClass() { return cssClass; }
        public List<Object> children() { return children; }
    }
}
