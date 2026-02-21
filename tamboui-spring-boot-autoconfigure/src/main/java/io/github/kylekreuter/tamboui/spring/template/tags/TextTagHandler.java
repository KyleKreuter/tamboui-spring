package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.widgets.paragraph.Paragraph;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:text>}.
 * Creates a TamboUI {@link Paragraph} widget with the resolved text content.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code t:text} - The text content to display</li>
 *   <li>{@code content} or {@code value} - Alternative text content attributes</li>
 *   <li>{@code class} - CSS class names for styling</li>
 * </ul>
 */
public class TextTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "text";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String text = attributes.get("t:text");
        if (text == null) {
            text = attributes.get("content");
        }
        if (text == null) {
            text = attributes.get("value");
        }
        if (text == null) {
            text = "";
        }

        String cssClass = attributes.get("class");
        if (cssClass != null && !cssClass.isBlank()) {
            return new TextWidget(text, cssClass.trim());
        }
        return Paragraph.from(text);
    }

    /**
     * Wrapper that carries text content and CSS class names through the
     * rendering pipeline. When no CSS class is specified, the handler
     * returns a plain {@link Paragraph} instead.
     */
    public static final class TextWidget {
        private final String text;
        private final String cssClass;

        public TextWidget(String text, String cssClass) {
            this.text = text;
            this.cssClass = cssClass;
        }

        public String text() {
            return text;
        }

        public String cssClass() {
            return cssClass;
        }
    }
}
