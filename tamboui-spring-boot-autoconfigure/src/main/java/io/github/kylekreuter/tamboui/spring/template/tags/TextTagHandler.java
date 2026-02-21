package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:text>}.
 * Creates a TamboUI Text element with the resolved text content.
 */
public class TextTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "text";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        // TODO: Create TamboUI Text element from attributes (t:text content)
        return null;
    }
}
