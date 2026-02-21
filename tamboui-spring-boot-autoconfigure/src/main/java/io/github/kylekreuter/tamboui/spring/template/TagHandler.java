package io.github.kylekreuter.tamboui.spring.template;

import java.util.Map;

/**
 * Handles a specific template tag and creates the corresponding TamboUI Element.
 * <p>
 * Each XML tag name in a {@code .ttl} template (e.g. {@code <t:panel>}, {@code <t:text>})
 * maps to a {@code TagHandler} implementation. Handlers are thin translators that
 * convert XML attributes to TamboUI Element builder calls.
 */
public interface TagHandler {

    /**
     * The tag name this handler is responsible for (without namespace prefix).
     * For example, {@code "panel"} handles {@code <t:panel>}.
     */
    String getTagName();

    /**
     * Create a TamboUI Element from the given template node attributes.
     *
     * @param attributes the resolved attributes (SpEL already evaluated)
     * @return the created element (TamboUI Element)
     */
    Object createElement(Map<String, String> attributes);
}
