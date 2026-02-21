package io.github.kylekreuter.tamboui.spring.template;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache for parsed template nodes.
 * Templates are parsed once and reused across frames.
 */
public class TemplateCache {

    private final ConcurrentMap<String, TemplateNode> cache = new ConcurrentHashMap<>();

    /**
     * Get a cached template node by resource path.
     *
     * @param path the classpath resource path
     * @return the cached node, or {@code null} if not cached
     */
    public TemplateNode get(String path) {
        return cache.get(path);
    }

    /**
     * Store a parsed template node.
     *
     * @param path the classpath resource path
     * @param node the parsed root node
     */
    public void put(String path, TemplateNode node) {
        cache.put(path, node);
    }

    /**
     * Clear all cached templates.
     */
    public void clear() {
        cache.clear();
    }
}
