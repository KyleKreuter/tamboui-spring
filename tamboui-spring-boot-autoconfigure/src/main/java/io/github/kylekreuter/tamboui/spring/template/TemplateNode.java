package io.github.kylekreuter.tamboui.spring.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a parsed node in a {@code .ttl} template.
 * Each node corresponds to an XML element and maps to a TamboUI Element via a {@link TagHandler}.
 */
public class TemplateNode {

    private final String tagName;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<TemplateNode> children = new ArrayList<>();
    private String textContent;

    public TemplateNode(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public List<TemplateNode> getChildren() {
        return children;
    }

    public void addChild(TemplateNode child) {
        children.add(child);
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
