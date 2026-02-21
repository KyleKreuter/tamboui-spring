package io.github.kylekreuter.tamboui.spring.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model container for template data, populated by a {@link ScreenController} each frame.
 * Provides a fluent API for adding attributes.
 * <p>
 * Supports two types of bindings:
 * <ul>
 *   <li><b>Attributes</b> — transient values set via {@link #put(String, Object)}, cleared between frames</li>
 *   <li><b>State bindings</b> — long-lived state objects set via {@link #bindState(String, Object)},
 *       which survive {@link #clear()} cycles. Ideal for mutable UI state like {@code FormState}
 *       or {@code TextInputState} that must persist across frames.</li>
 * </ul>
 */
public class TemplateModel {

    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final Map<String, Object> stateBindings = new LinkedHashMap<>();

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
     * Bind a long-lived state object to the model.
     * <p>
     * Unlike regular attributes set via {@link #put(String, Object)}, state bindings
     * are <b>not</b> cleared when {@link #clear()} is called. This makes them suitable
     * for mutable UI state objects (e.g. {@code FormState}, {@code TextInputState})
     * that must persist across rendering frames.
     *
     * @param key   the state binding name
     * @param state the state object to bind
     * @return this model for fluent chaining
     */
    public TemplateModel bindState(String key, Object state) {
        stateBindings.put(key, state);
        return this;
    }

    /**
     * Get an attribute or state binding value by key.
     * <p>
     * State bindings take precedence over regular attributes if both exist
     * for the same key.
     *
     * @param key the attribute or state binding name
     * @return the value, or {@code null} if not present
     */
    public Object get(String key) {
        Object stateValue = stateBindings.get(key);
        if (stateValue != null) {
            return stateValue;
        }
        return attributes.get(key);
    }

    /**
     * Get a state binding value by key.
     *
     * @param key the state binding name
     * @return the state object, or {@code null} if not bound
     */
    public Object getState(String key) {
        return stateBindings.get(key);
    }

    /**
     * Check if the model contains the given key (in attributes or state bindings).
     */
    public boolean containsKey(String key) {
        return stateBindings.containsKey(key) || attributes.containsKey(key);
    }

    /**
     * Return an unmodifiable view of all attributes and state bindings merged.
     * State bindings take precedence over regular attributes.
     */
    public Map<String, Object> asMap() {
        Map<String, Object> merged = new LinkedHashMap<>(attributes);
        merged.putAll(stateBindings);
        return Collections.unmodifiableMap(merged);
    }

    /**
     * Return an unmodifiable view of all state bindings.
     *
     * @return the state bindings map
     */
    public Map<String, Object> stateBindings() {
        return Collections.unmodifiableMap(stateBindings);
    }

    /**
     * Clear transient attributes. Called between frames.
     * <p>
     * State bindings set via {@link #bindState(String, Object)} are <b>not</b> affected.
     */
    public void clear() {
        attributes.clear();
    }

    /**
     * Clear all state bindings. Typically called on screen unmount.
     */
    public void clearState() {
        stateBindings.clear();
    }
}
