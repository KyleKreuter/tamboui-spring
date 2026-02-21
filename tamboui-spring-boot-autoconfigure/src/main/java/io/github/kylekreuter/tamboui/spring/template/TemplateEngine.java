package io.github.kylekreuter.tamboui.spring.template;

import io.github.kylekreuter.tamboui.spring.autoconfigure.TamboUiProperties;

/**
 * Renders {@code .ttl} templates into TamboUI Element trees.
 * <p>
 * Parses XML-based templates, evaluates SpEL expressions against
 * the model, and delegates to {@link TagHandler} implementations
 * for building the element tree.
 */
public class TemplateEngine {

    private final TemplateCache cache;
    private final TamboUiProperties properties;

    public TemplateEngine(TemplateCache cache, TamboUiProperties properties) {
        this.cache = cache;
        this.properties = properties;
    }

    /**
     * Render a template by name with the given model attributes.
     *
     * @param templateName the template name (without prefix/suffix)
     * @param model        the model attributes for SpEL evaluation
     * @return the rendered element tree root (TamboUI Element)
     */
    public Object render(String templateName, java.util.Map<String, Object> model) {
        String path = properties.getTemplatePrefix() + templateName + properties.getTemplateSuffix();
        TemplateNode root = cache.get(path);
        if (root == null) {
            root = parse(path);
            cache.put(path, root);
        }
        // TODO: Evaluate SpEL expressions and build TamboUI Element tree
        return null;
    }

    private TemplateNode parse(String resourcePath) {
        // TODO: Parse XML template from classpath resource
        return new TemplateNode("root");
    }
}
