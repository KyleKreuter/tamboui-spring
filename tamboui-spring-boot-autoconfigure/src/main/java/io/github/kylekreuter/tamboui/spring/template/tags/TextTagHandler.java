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
        return Paragraph.from(text);
    }
}
