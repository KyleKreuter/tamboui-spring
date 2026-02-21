package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:panel>}.
 * Creates a TamboUI {@link Block} widget with a title and border.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code title} - Panel title text</li>
 *   <li>{@code borderType} or {@code border-style} or {@code border-type} - Border style (PLAIN, ROUNDED, DOUBLE, THICK, NONE)</li>
 * </ul>
 */
public class PanelTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "panel";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        Block.Builder builder = Block.builder()
                .borders(Borders.ALL);

        String title = attributes.get("title");
        if (title != null) {
            builder.title(title);
        }

        String borderTypeAttr = attributes.get("borderType");
        if (borderTypeAttr == null) {
            borderTypeAttr = attributes.get("border-style");
        }
        if (borderTypeAttr == null) {
            borderTypeAttr = attributes.get("border-type");
        }
        if (borderTypeAttr != null) {
            try {
                BorderType type = BorderType.valueOf(borderTypeAttr.toUpperCase());
                builder.borderType(type);
            } catch (IllegalArgumentException ignored) {
                // Fall back to default border type
            }
        }

        String cssClass = attributes.get("class");

        return new PanelWidget(builder.build(), title, cssClass);
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof PanelWidget panelWidget) {
            panelWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that associates a {@link Block} with its child widgets.
     * The TamboUI Block itself is a container decorator, so we need a holder
     * to carry both the block and its child widgets through the rendering pipeline.
     */
    public static final class PanelWidget {
        private final Block block;
        private final String title;
        private final String cssClass;
        private final List<Object> children = new ArrayList<>();

        public PanelWidget(Block block, String title, String cssClass) {
            this.block = block;
            this.title = title;
            this.cssClass = cssClass;
        }

        public Block block() {
            return block;
        }

        public String title() {
            return title;
        }

        public String cssClass() {
            return cssClass;
        }

        public List<Object> children() {
            return children;
        }
    }
}
