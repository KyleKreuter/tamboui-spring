package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:tree>}.
 * Creates a wrapper representing a tree widget that displays hierarchical data
 * bound to a {@code TreeState} or root nodes from the model.
 * <p>
 * The tree is a styled element (not a container), so it implements {@link TagHandler}
 * rather than {@link io.github.kylekreuter.tamboui.spring.template.ParentTagHandler}.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code bind} - State binding name referencing a TreeState or root nodes in the model</li>
 *   <li>{@code title} - Tree title text</li>
 *   <li>{@code border-type} - Border style: plain, rounded, double</li>
 *   <li>{@code border-color} - Border color name (e.g. "red", "blue")</li>
 *   <li>{@code guide-style} - Guide line style: unicode, ascii, none</li>
 *   <li>{@code highlight-color} - Color for the highlighted/selected item</li>
 *   <li>{@code highlight-symbol} - Symbol shown before the selected item</li>
 *   <li>{@code scrollbar-policy} - Scrollbar policy (e.g. "auto", "always", "never")</li>
 *   <li>{@code indent-width} - Indentation width per tree level (integer)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:tree bind="fileTree" title="Files" border-type="rounded"
 *         guide-style="unicode" highlight-color="cyan" />
 * }</pre>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.TagHandler
 */
public class TreeTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "tree";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String bind = attributes.get("bind");
        String title = attributes.get("title");
        String borderType = attributes.get("border-type");
        String borderColor = attributes.get("border-color");
        String guideStyle = attributes.get("guide-style");
        String highlightColor = attributes.get("highlight-color");
        String highlightSymbol = attributes.get("highlight-symbol");
        String scrollbarPolicy = attributes.get("scrollbar-policy");
        String indentWidth = attributes.get("indent-width");

        return new TreeWidget(
                bind,
                title,
                borderType,
                borderColor,
                guideStyle,
                highlightColor,
                highlightSymbol,
                scrollbarPolicy,
                indentWidth
        );
    }

    /**
     * Widget wrapper that holds all tree configuration attributes.
     * The rendering pipeline inspects these attributes to create and configure
     * the TamboUI {@code TreeElement} and connect it to the appropriate state.
     */
    public static final class TreeWidget {
        private final String bind;
        private final String title;
        private final String borderType;
        private final String borderColor;
        private final String guideStyle;
        private final String highlightColor;
        private final String highlightSymbol;
        private final String scrollbarPolicy;
        private final String indentWidth;

        /**
         * Creates a new TreeWidget with all configuration attributes.
         *
         * @param bind            state binding name (may be {@code null})
         * @param title           tree title (may be {@code null})
         * @param borderType      border style (may be {@code null})
         * @param borderColor     border color (may be {@code null})
         * @param guideStyle      guide line style (may be {@code null})
         * @param highlightColor  highlight color (may be {@code null})
         * @param highlightSymbol highlight symbol (may be {@code null})
         * @param scrollbarPolicy scrollbar policy (may be {@code null})
         * @param indentWidth     indent width (may be {@code null})
         */
        public TreeWidget(String bind, String title, String borderType,
                           String borderColor, String guideStyle, String highlightColor,
                           String highlightSymbol, String scrollbarPolicy, String indentWidth) {
            this.bind = bind;
            this.title = title;
            this.borderType = borderType;
            this.borderColor = borderColor;
            this.guideStyle = guideStyle;
            this.highlightColor = highlightColor;
            this.highlightSymbol = highlightSymbol;
            this.scrollbarPolicy = scrollbarPolicy;
            this.indentWidth = indentWidth;
        }

        /**
         * Returns the state binding name.
         *
         * @return the binding name, or {@code null}
         */
        public String bind() {
            return bind;
        }

        /**
         * Returns the tree title.
         *
         * @return the title, or {@code null}
         */
        public String title() {
            return title;
        }

        /**
         * Returns the border type (plain, rounded, double).
         *
         * @return the border type, or {@code null}
         */
        public String borderType() {
            return borderType;
        }

        /**
         * Returns the border color.
         *
         * @return the border color, or {@code null}
         */
        public String borderColor() {
            return borderColor;
        }

        /**
         * Returns the guide line style (unicode, ascii, none).
         *
         * @return the guide style, or {@code null}
         */
        public String guideStyle() {
            return guideStyle;
        }

        /**
         * Returns the highlight color.
         *
         * @return the highlight color, or {@code null}
         */
        public String highlightColor() {
            return highlightColor;
        }

        /**
         * Returns the highlight symbol.
         *
         * @return the highlight symbol, or {@code null}
         */
        public String highlightSymbol() {
            return highlightSymbol;
        }

        /**
         * Returns the scrollbar policy.
         *
         * @return the scrollbar policy, or {@code null}
         */
        public String scrollbarPolicy() {
            return scrollbarPolicy;
        }

        /**
         * Returns the indent width per tree level.
         *
         * @return the indent width, or {@code null}
         */
        public String indentWidth() {
            return indentWidth;
        }
    }
}
