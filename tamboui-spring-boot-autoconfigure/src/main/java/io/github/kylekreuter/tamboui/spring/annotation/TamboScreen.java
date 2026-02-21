package io.github.kylekreuter.tamboui.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Stereotype annotation for TamboUI screen controllers.
 * <p>
 * Marks a class as a TUI screen controller, similar to {@code @Controller}
 * in Spring MVC. The annotated class should implement {@link io.github.kylekreuter.tamboui.spring.core.ScreenController}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TamboScreen {

    /**
     * The value may indicate a suggestion for a logical component name.
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * The template name to render for this screen (without path prefix or suffix).
     */
    String template();
}
