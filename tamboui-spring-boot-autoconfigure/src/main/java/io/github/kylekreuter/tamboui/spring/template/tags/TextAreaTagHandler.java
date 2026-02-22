package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:textarea>}.
 * Creates a widget descriptor for the TamboUI
 * {@link dev.tamboui.toolkit.elements.TextAreaElement TextAreaElement}.
 * <p>
 * The text area widget provides a multi-line text input field with scrolling
 * support. It binds to a {@link dev.tamboui.widgets.input.TextAreaState TextAreaState}
 * from the model via the {@code bind} attribute.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code bind} - State binding name referencing a
 *       {@link dev.tamboui.widgets.input.TextAreaState TextAreaState} in the model</li>
 *   <li>{@code placeholder} - Placeholder text shown when the text area is empty</li>
 *   <li>{@code title} - Border title text</li>
 *   <li>{@code border-type} - Border type (e.g. {@code "rounded"})</li>
 *   <li>{@code border-color} - Border color name</li>
 *   <li>{@code focused-border-color} - Border color name used when focused</li>
 *   <li>{@code show-line-numbers} - Whether to show line numbers
 *       ({@code "true"} to enable)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:textarea bind="editorState" placeholder="Enter text..."
 *             title="Editor" border-type="rounded"
 *             show-line-numbers="true" />
 * }</pre>
 */
public class TextAreaTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "textarea";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String bind = attributes.get("bind");
        String placeholder = attributes.get("placeholder");
        String title = attributes.get("title");
        String borderType = attributes.get("border-type");
        String borderColor = attributes.get("border-color");
        String focusedBorderColor = attributes.get("focused-border-color");
        String showLineNumbersAttr = attributes.get("show-line-numbers");

        boolean showLineNumbers = "true".equalsIgnoreCase(showLineNumbersAttr);

        return new TextAreaWidget(bind, placeholder, title, borderType,
                borderColor, focusedBorderColor, showLineNumbers);
    }

    /**
     * Widget wrapper that holds all parsed attributes for the TextArea element.
     * <p>
     * The rendering pipeline inspects these attributes to construct a
     * {@link dev.tamboui.toolkit.elements.TextAreaElement TextAreaElement} and
     * connect it to the appropriate
     * {@link dev.tamboui.widgets.input.TextAreaState TextAreaState}.
     */
    public static final class TextAreaWidget {
        private final String bind;
        private final String placeholder;
        private final String title;
        private final String borderType;
        private final String borderColor;
        private final String focusedBorderColor;
        private final boolean showLineNumbers;

        /**
         * Creates a new TextAreaWidget.
         *
         * @param bind               state binding name (may be {@code null})
         * @param placeholder        placeholder text (may be {@code null})
         * @param title              border title (may be {@code null})
         * @param borderType         border type name (may be {@code null})
         * @param borderColor        border color name (may be {@code null})
         * @param focusedBorderColor focused border color name (may be {@code null})
         * @param showLineNumbers    whether to show line numbers
         */
        public TextAreaWidget(String bind, String placeholder, String title,
                               String borderType, String borderColor,
                               String focusedBorderColor, boolean showLineNumbers) {
            this.bind = bind;
            this.placeholder = placeholder;
            this.title = title;
            this.borderType = borderType;
            this.borderColor = borderColor;
            this.focusedBorderColor = focusedBorderColor;
            this.showLineNumbers = showLineNumbers;
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
         * Returns the placeholder text.
         *
         * @return the placeholder, or {@code null}
         */
        public String placeholder() {
            return placeholder;
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

        /**
         * Returns the focused border color name.
         *
         * @return the focused border color, or {@code null}
         */
        public String focusedBorderColor() {
            return focusedBorderColor;
        }

        /**
         * Returns whether line numbers should be displayed.
         *
         * @return {@code true} if line numbers are enabled
         */
        public boolean showLineNumbers() {
            return showLineNumbers;
        }
    }
}
