package io.github.kylekreuter.tamboui.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a state binding for automatic registration in the {@link
 * io.github.kylekreuter.tamboui.spring.core.TemplateModel TemplateModel}.
 * <p>
 * Fields annotated with {@code @BindState} on {@link TamboScreen} controllers
 * are automatically registered as state bindings before {@code populate()} is called.
 * This enables true two-way binding between the controller field and the corresponding
 * template widget (e.g. {@code <t:select bind="...">} or {@code <t:input bind="...">}).
 * <p>
 * The binding key defaults to the field name but can be overridden via {@link #value()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @TamboScreen(template = "dashboard")
 * public class DashboardController implements ScreenController {
 *
 *     @BindState
 *     private SelectState languageSelect = new SelectState("Deutsch", "English");
 *
 *     @BindState("search")
 *     private TextInputState searchInput = new TextInputState();
 * }
 * }</pre>
 *
 * @see io.github.kylekreuter.tamboui.spring.core.BindStateRegistrar
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindState {

    /**
     * The binding key used in the template's {@code bind} attribute.
     * <p>
     * Defaults to the field name if left empty.
     *
     * @return the explicit binding name, or empty string for field name
     */
    String value() default "";
}
