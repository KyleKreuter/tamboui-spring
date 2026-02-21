package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Flex;
import dev.tamboui.layout.Margin;
import dev.tamboui.toolkit.elements.Row;
import io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter;
import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:row>}.
 * Creates a TamboUI {@link Row} element that arranges children horizontally.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code spacing} - Gap between children in cells (integer)</li>
 *   <li>{@code flex} - Flex positioning mode: START, CENTER, END, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY</li>
 *   <li>{@code margin} - Uniform margin around the row (integer)</li>
 *   <li>{@code class} - CSS class for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 */
public class RowTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "row";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        Row row = new Row();

        String spacing = attributes.get("spacing");
        if (spacing != null) {
            try {
                row.spacing(Integer.parseInt(spacing.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default spacing
            }
        }

        String flexAttr = attributes.get("flex");
        if (flexAttr != null) {
            try {
                Flex flex = Flex.valueOf(flexAttr.trim().toUpperCase());
                row.flex(flex);
            } catch (IllegalArgumentException ignored) {
                // Fall back to default flex
            }
        }

        String marginAttr = attributes.get("margin");
        if (marginAttr != null) {
            try {
                row.margin(Integer.parseInt(marginAttr.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default margin
            }
        }

        String id = attributes.get("id");
        if (id != null) {
            row.id(id);
        }

        String cssClass = attributes.get("class");
        if (cssClass != null) {
            row.addClass(cssClass.trim().split("\\s+"));
        }

        return row;
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof Row row) {
            WidgetToElementConverter converter = new WidgetToElementConverter();
            for (Object child : children) {
                row.add(converter.convert(child));
            }
        }
    }
}
