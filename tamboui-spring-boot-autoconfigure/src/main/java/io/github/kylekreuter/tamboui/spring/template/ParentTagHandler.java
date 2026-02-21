package io.github.kylekreuter.tamboui.spring.template;

import java.util.List;

/**
 * Extension of {@link TagHandler} for tags that can contain child elements.
 * <p>
 * Container tags like {@code <t:panel>} implement this interface so the
 * {@link TemplateEngine} can pass rendered child widgets to the parent.
 */
public interface ParentTagHandler extends TagHandler {

    /**
     * Add child widgets to the parent widget created by {@link #createElement}.
     *
     * @param parent   the parent widget returned by {@link #createElement}
     * @param children the rendered child widgets
     */
    void addChildren(Object parent, List<Object> children);
}
