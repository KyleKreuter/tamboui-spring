package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:dialog>}.
 * Creates a wrapper representing a dialog container that can hold child widgets
 * with configurable border, title, dimensions, direction, and spacing.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code title} - Dialog title text</li>
 *   <li>{@code border-type} - Border style: plain, rounded, double</li>
 *   <li>{@code border-color} - Border color name (e.g. "red", "blue")</li>
 *   <li>{@code width} - Width of the dialog (integer)</li>
 *   <li>{@code height} - Height of the dialog (integer)</li>
 *   <li>{@code min-width} - Minimum width of the dialog (integer)</li>
 *   <li>{@code padding} - Padding inside the dialog (integer)</li>
 *   <li>{@code direction} - Layout direction: horizontal or vertical</li>
 *   <li>{@code flex} - Flex layout mode</li>
 *   <li>{@code spacing} - Spacing between children (integer)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:dialog title="Confirm" border-type="rounded" width="40" direction="vertical" spacing="1">
 *   <t:text t:text="Are you sure?" />
 * </t:dialog>
 * }</pre>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class DialogTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "dialog";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        DialogWidget widget = new DialogWidget();

        String title = attributes.get("title");
        if (title != null && !title.isBlank()) {
            widget.setTitle(title.trim());
        }

        String borderType = attributes.get("border-type");
        if (borderType != null && !borderType.isBlank()) {
            widget.setBorderType(borderType.trim());
        }

        String borderColor = attributes.get("border-color");
        if (borderColor != null && !borderColor.isBlank()) {
            widget.setBorderColor(borderColor.trim());
        }

        String width = attributes.get("width");
        if (width != null && !width.isBlank()) {
            widget.setWidth(width.trim());
        }

        String height = attributes.get("height");
        if (height != null && !height.isBlank()) {
            widget.setHeight(height.trim());
        }

        String minWidth = attributes.get("min-width");
        if (minWidth != null && !minWidth.isBlank()) {
            widget.setMinWidth(minWidth.trim());
        }

        String padding = attributes.get("padding");
        if (padding != null && !padding.isBlank()) {
            widget.setPadding(padding.trim());
        }

        String direction = attributes.get("direction");
        if (direction != null && !direction.isBlank()) {
            widget.setDirection(direction.trim());
        }

        String flex = attributes.get("flex");
        if (flex != null && !flex.isBlank()) {
            widget.setFlex(flex.trim());
        }

        String spacing = attributes.get("spacing");
        if (spacing != null && !spacing.isBlank()) {
            widget.setSpacing(spacing.trim());
        }

        return widget;
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof DialogWidget dialogWidget) {
            dialogWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that holds dialog configuration and its child widgets.
     * This intermediate representation carries all dialog attributes through the
     * rendering pipeline before being converted to TamboUI's {@code DialogElement}.
     */
    public static final class DialogWidget {
        private String title;
        private String borderType;
        private String borderColor;
        private String width;
        private String height;
        private String minWidth;
        private String padding;
        private String direction;
        private String flex;
        private String spacing;
        private final List<Object> children = new ArrayList<>();

        /**
         * Returns the dialog title.
         *
         * @return the title or {@code null}
         */
        public String title() {
            return title;
        }

        /**
         * Sets the dialog title.
         *
         * @param title the title text
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * Returns the border type (plain, rounded, double).
         *
         * @return the border type or {@code null}
         */
        public String borderType() {
            return borderType;
        }

        /**
         * Sets the border type.
         *
         * @param borderType the border type string
         */
        public void setBorderType(String borderType) {
            this.borderType = borderType;
        }

        /**
         * Returns the border color.
         *
         * @return the border color or {@code null}
         */
        public String borderColor() {
            return borderColor;
        }

        /**
         * Sets the border color.
         *
         * @param borderColor the border color string
         */
        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        /**
         * Returns the width.
         *
         * @return the width or {@code null}
         */
        public String width() {
            return width;
        }

        /**
         * Sets the width.
         *
         * @param width the width string
         */
        public void setWidth(String width) {
            this.width = width;
        }

        /**
         * Returns the height.
         *
         * @return the height or {@code null}
         */
        public String height() {
            return height;
        }

        /**
         * Sets the height.
         *
         * @param height the height string
         */
        public void setHeight(String height) {
            this.height = height;
        }

        /**
         * Returns the minimum width.
         *
         * @return the minimum width or {@code null}
         */
        public String minWidth() {
            return minWidth;
        }

        /**
         * Sets the minimum width.
         *
         * @param minWidth the minimum width string
         */
        public void setMinWidth(String minWidth) {
            this.minWidth = minWidth;
        }

        /**
         * Returns the padding.
         *
         * @return the padding or {@code null}
         */
        public String padding() {
            return padding;
        }

        /**
         * Sets the padding.
         *
         * @param padding the padding string
         */
        public void setPadding(String padding) {
            this.padding = padding;
        }

        /**
         * Returns the layout direction (horizontal or vertical).
         *
         * @return the direction or {@code null}
         */
        public String direction() {
            return direction;
        }

        /**
         * Sets the layout direction.
         *
         * @param direction the direction string
         */
        public void setDirection(String direction) {
            this.direction = direction;
        }

        /**
         * Returns the flex mode.
         *
         * @return the flex or {@code null}
         */
        public String flex() {
            return flex;
        }

        /**
         * Sets the flex mode.
         *
         * @param flex the flex string
         */
        public void setFlex(String flex) {
            this.flex = flex;
        }

        /**
         * Returns the spacing between children.
         *
         * @return the spacing or {@code null}
         */
        public String spacing() {
            return spacing;
        }

        /**
         * Sets the spacing between children.
         *
         * @param spacing the spacing string
         */
        public void setSpacing(String spacing) {
            this.spacing = spacing;
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
