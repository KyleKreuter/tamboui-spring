package io.github.kylekreuter.tamboui.spring.template;

/**
 * Exception thrown when a template cannot be parsed or loaded.
 */
public class TemplateParseException extends RuntimeException {

    public TemplateParseException(String message) {
        super(message);
    }

    public TemplateParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
