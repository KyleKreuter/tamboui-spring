package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import dev.tamboui.widgets.list.ListItem;

import java.util.Map;

/**
 * Tag handler for {@code <t:item>}.
 * Creates a TamboUI {@link ListItem} for use inside a {@code <t:list>}.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code t:text} - The item text content</li>
 *   <li>{@code content} or {@code value} - Alternative text content attributes</li>
 * </ul>
 */
public class ItemTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "item";
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
        return ListItem.from(text);
    }
}
