package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:panel>}.
 * Creates a TamboUI Panel element with title and border attributes.
 */
public class PanelTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "panel";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        // TODO: Create TamboUI Panel element from attributes (title, border style, etc.)
        return null;
    }
}
