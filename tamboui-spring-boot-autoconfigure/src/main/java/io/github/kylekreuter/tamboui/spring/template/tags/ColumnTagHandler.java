package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Flex;
import dev.tamboui.layout.Margin;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Column;
import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:column>}.
 * Creates a TamboUI {@link Column} element that arranges children vertically.
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
        Column column = new Column();

        String spacing = attributes.get("spacing");
        if (spacing != null) {
            try {
                column.spacing(Integer.parseInt(spacing.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default spacing
            }
        }

        String flexAttr = attributes.get("flex");
        if (flexAttr != null) {
            try {
                Flex flex = Flex.valueOf(flexAttr.trim().toUpperCase());
                column.flex(flex);
            } catch (IllegalArgumentException ignored) {
                // Fall back to default flex
            }
        }

        String marginAttr = attributes.get("margin");
        if (marginAttr != null) {
            try {
                column.margin(Integer.parseInt(marginAttr.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default margin
            }
        }

        String id = attributes.get("id");
        if (id != null) {
            column.id(id);
        }

        String cssClass = attributes.get("class");
        if (cssClass != null) {
            column.addClass(cssClass.trim().split("\\s+"));
        }

        return column;
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof Column column) {
            for (Object child : children) {
                if (child instanceof Element element) {
                    column.add(element);
                }
            }
        }
    }
}
