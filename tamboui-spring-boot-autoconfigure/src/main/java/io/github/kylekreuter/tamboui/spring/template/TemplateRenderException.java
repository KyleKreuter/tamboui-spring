package io.github.kylekreuter.tamboui.spring.template;

/**
 * Exception thrown when a template cannot be rendered.
 */
public class TemplateRenderException extends RuntimeException {

    public TemplateRenderException(String message) {
        super(message);
    }

    public TemplateRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
