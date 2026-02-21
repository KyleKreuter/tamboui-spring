package io.github.kylekreuter.tamboui.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method to a form submit event.
 * <p>
 * The annotated method will be invoked when the form with the specified
 * binding name is submitted. The method may have zero parameters or accept
 * a single {@code FormState} parameter.
 * <p>
 * Example usage:
 * <pre>{@code
 * @OnSubmit("settingsForm")
 * public void onSave(FormState state) {
 *     String username = state.textValue("username");
 *     // process form data
 * }
 * }</pre>
 *
 * @see OnKey
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnSubmit {

    /**
     * The form binding name that this method handles submission for.
     * This must match the {@code bind} attribute on the corresponding
     * {@code <t:form>} tag or the state binding key used in
     * {@code model.bindState(name, formState)}.
     */
    String value();
}
