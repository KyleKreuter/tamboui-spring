package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:tabs>}.
 * Creates a widget descriptor for the TamboUI
 * {@link dev.tamboui.toolkit.elements.TabsElement TabsElement}.
 * <p>
 * The tabs widget displays a horizontal set of tab titles with one selected.
 * It binds to a {@link dev.tamboui.widgets.tabs.TabsState TabsState} from the
 * model via the {@code bind} attribute.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code titles} - Comma-separated tab title strings
 *       (e.g. {@code "Home,Settings,About"})</li>
 *   <li>{@code bind} - State binding name referencing a
 *       {@link dev.tamboui.widgets.tabs.TabsState TabsState} in the model</li>
 *   <li>{@code divider} - Divider string between tabs (default: {@code " | "})</li>
 *   <li>{@code highlight-color} - Highlight color name for the selected tab</li>
 *   <li>{@code padding-left} - Left padding string around tab titles</li>
 *   <li>{@code padding-right} - Right padding string around tab titles</li>
 *   <li>{@code title} - Border title text</li>
 *   <li>{@code border-type} - Border type (e.g. {@code "rounded"})</li>
 *   <li>{@code border-color} - Border color name</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:tabs titles="Home,Settings,About" bind="tabsState"
 *         highlight-color="YELLOW" divider=" | " />
 * }</pre>
 */
public class TabsTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "tabs";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String titles = attributes.get("titles");
        String bind = attributes.get("bind");
        String divider = attributes.get("divider");
        String highlightColor = attributes.get("highlight-color");
        String paddingLeft = attributes.get("padding-left");
        String paddingRight = attributes.get("padding-right");
        String title = attributes.get("title");
        String borderType = attributes.get("border-type");
        String borderColor = attributes.get("border-color");

        return new TabsWidget(titles, bind, divider, highlightColor,
                paddingLeft, paddingRight, title, borderType, borderColor);
    }

    /**
     * Widget wrapper that holds all parsed attributes for the Tabs element.
     * <p>
     * The rendering pipeline inspects these attributes to construct a
     * {@link dev.tamboui.toolkit.elements.TabsElement TabsElement} and connect
     * it to the appropriate {@link dev.tamboui.widgets.tabs.TabsState TabsState}.
     */
    public static final class TabsWidget {
        private final String titles;
        private final String bind;
        private final String divider;
        private final String highlightColor;
        private final String paddingLeft;
        private final String paddingRight;
        private final String title;
        private final String borderType;
        private final String borderColor;

        /**
         * Creates a new TabsWidget.
         *
         * @param titles         comma-separated tab titles (may be {@code null})
         * @param bind           state binding name (may be {@code null})
         * @param divider        divider string between tabs (may be {@code null})
         * @param highlightColor highlight color name for the selected tab (may be {@code null})
         * @param paddingLeft    left padding string (may be {@code null})
         * @param paddingRight   right padding string (may be {@code null})
         * @param title          border title (may be {@code null})
         * @param borderType     border type name (may be {@code null})
         * @param borderColor    border color name (may be {@code null})
         */
        public TabsWidget(String titles, String bind, String divider,
                           String highlightColor, String paddingLeft,
                           String paddingRight, String title,
                           String borderType, String borderColor) {
            this.titles = titles;
            this.bind = bind;
            this.divider = divider;
            this.highlightColor = highlightColor;
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.title = title;
            this.borderType = borderType;
            this.borderColor = borderColor;
        }

        /**
         * Returns the comma-separated tab titles.
         *
         * @return the titles string, or {@code null}
         */
        public String titles() {
            return titles;
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
         * Returns the divider string between tabs.
         *
         * @return the divider, or {@code null}
         */
        public String divider() {
            return divider;
        }

        /**
         * Returns the highlight color name for the selected tab.
         *
         * @return the highlight color name, or {@code null}
         */
        public String highlightColor() {
            return highlightColor;
        }

        /**
         * Returns the left padding string.
         *
         * @return the left padding, or {@code null}
         */
        public String paddingLeft() {
            return paddingLeft;
        }

        /**
         * Returns the right padding string.
         *
         * @return the right padding, or {@code null}
         */
        public String paddingRight() {
            return paddingRight;
        }

        /**
         * Returns the border title.
         *
         * @return the title, or {@code null}
         */
        public String title() {
            return title;
        }

        /**
         * Returns the border type name.
         *
         * @return the border type, or {@code null}
         */
        public String borderType() {
            return borderType;
        }

        /**
         * Returns the border color name.
         *
         * @return the border color, or {@code null}
         */
        public String borderColor() {
            return borderColor;
        }
    }
}
