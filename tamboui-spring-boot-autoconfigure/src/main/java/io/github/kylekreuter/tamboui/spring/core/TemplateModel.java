package io.github.kylekreuter.tamboui.spring.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model container for template data, populated by a {@link ScreenController} each frame.
 * Provides a fluent API for adding attributes.
 */
public class TemplateModel {

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    /**
     * Add an attribute to the model.
     *
     * @param key   the attribute name (used as {@code ${key}} in templates)
     * @param value the attribute value
     * @return this model for fluent chaining
     */
    public TemplateModel put(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    /**
     * Get an attribute value by key.
     *
     * @param key the attribute name
     * @return the value, or {@code null} if not present
     */
    public Object get(String key) {
        return attributes.get(key);
    }

    /**
     * Check if the model contains the given key.
     */
    public boolean containsKey(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Return an unmodifiable view of all attributes.
     */
    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Clear all attributes. Called between frames.
     */
    public void clear() {
        attributes.clear();
    }
}
