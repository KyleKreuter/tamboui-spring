package io.github.kylekreuter.tamboui.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method to a keyboard event.
 * <p>
 * The annotated method will be invoked when the specified key is pressed
 * while this screen is active.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnKey {

    /**
     * The key or key combination to bind to (e.g. {@code "q"}, {@code "ctrl+c"}, {@code "esc"}).
     */
    String value();
}
