package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Direction;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.widgets.block.BorderType;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Locale;
import java.util.Map;

/**
 * Tag handler for {@code <t:panel>}.
 * Creates a TamboUI {@link Panel} element with title, border, and layout attributes.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code title} — the panel title text</li>
 *   <li>{@code border-style} or {@code border-type} — border style:
 *       plain, rounded, double, thick, none</li>
 *   <li>{@code direction} — layout direction: vertical/column or horizontal/row</li>
 *   <li>{@code padding} — uniform padding value (integer)</li>
 *   <li>{@code spacing} — gap between children (integer)</li>
 * </ul>
 */
public class PanelTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "panel";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        Panel panel = new Panel();

        String title = attributes.get("title");
        if (title != null) {
            panel.title(title);
        }

        String borderStyle = attributes.getOrDefault("border-style", attributes.get("border-type"));
        if (borderStyle != null) {
            panel.borderType(parseBorderType(borderStyle));
        }

        String direction = attributes.get("direction");
        if (direction != null) {
            panel.direction(parseDirection(direction));
        }

        String padding = attributes.get("padding");
        if (padding != null) {
            try {
                panel.padding(Integer.parseInt(padding.trim()));
            } catch (NumberFormatException ignored) {
                // Invalid padding value — use default
            }
        }

        String spacing = attributes.get("spacing");
        if (spacing != null) {
            try {
                panel.spacing(Integer.parseInt(spacing.trim()));
            } catch (NumberFormatException ignored) {
                // Invalid spacing value — use default
            }
        }

        return panel;
    }

    /**
     * Parses the border-style attribute string to a TamboUI {@link BorderType} enum value.
     */
    static BorderType parseBorderType(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "rounded" -> BorderType.ROUNDED;
            case "double" -> BorderType.DOUBLE;
            case "thick" -> BorderType.THICK;
            case "none" -> BorderType.NONE;
            default -> BorderType.PLAIN;
        };
    }

    /**
     * Parses the direction attribute string to a TamboUI {@link Direction} enum value.
     */
    static Direction parseDirection(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "horizontal", "row" -> Direction.HORIZONTAL;
            default -> Direction.VERTICAL;
        };
    }
}
