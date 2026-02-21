package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.toolkit.elements.Spacer;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:spacer>}.
 * Creates a TamboUI {@link Spacer} element that takes up space in layouts.
 * <p>
 * Spacers are used inside rows and columns to push elements apart or create gaps.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code size} - Fixed size in cells (integer). Creates a spacer with exact length.</li>
 *   <li>{@code weight} - Fill weight (integer). Creates a spacer that fills proportional space.</li>
 * </ul>
 * <p>
 * Without any attributes, the spacer behaves as {@link Spacer#fill()}, taking up all remaining space.
 * If both {@code size} and {@code weight} are specified, {@code size} takes precedence.
 */
public class SpacerTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "spacer";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String sizeAttr = attributes.get("size");
        if (sizeAttr != null) {
            try {
                return Spacer.length(Integer.parseInt(sizeAttr.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default fill spacer
            }
        }

        String weightAttr = attributes.get("weight");
        if (weightAttr != null) {
            try {
                return Spacer.fill().withWeight(Integer.parseInt(weightAttr.trim()));
            } catch (NumberFormatException ignored) {
                // Fall back to default fill spacer
            }
        }

        return Spacer.fill();
    }
}
