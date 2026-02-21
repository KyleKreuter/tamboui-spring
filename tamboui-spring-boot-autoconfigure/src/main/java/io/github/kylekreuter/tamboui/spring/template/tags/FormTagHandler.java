package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import dev.tamboui.widgets.form.FormState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:form>}.
 * Creates a form container that connects to a {@link FormState} via the {@code bind} attribute.
 * <p>
 * The {@code bind} attribute references a state binding key in the {@link
 * io.github.kylekreuter.tamboui.spring.core.TemplateModel TemplateModel}. Child
 * {@code <t:input>} tags within this form can reference fields from the bound FormState
 * using the {@code field} attribute.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code bind} - The state binding name referencing a {@link FormState} in the model (required)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * <t:form bind="settingsForm">
 *     <t:input field="username" placeholder="Username" />
 *     <t:input field="email" placeholder="E-Mail" />
 * </t:form>
 * }</pre>
 */
public class FormTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "form";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String bind = attributes.get("bind");
        return new FormWidget(bind);
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof FormWidget formWidget) {
            formWidget.children().addAll(children);
        }
    }

    /**
     * Widget wrapper that holds the form binding name and its child widgets.
     * The rendering pipeline uses the bind name to look up the {@link FormState}
     * from the model's state bindings.
     */
    public static final class FormWidget {
        private final String bind;
        private final List<Object> children = new ArrayList<>();

        /**
         * Creates a new FormWidget with the given binding name.
         *
         * @param bind the state binding name (may be {@code null} if not specified)
         */
        public FormWidget(String bind) {
            this.bind = bind;
        }

        /**
         * Returns the state binding name.
         *
         * @return the binding name, or {@code null}
         */
        public String bind() {
            return bind;
        }

        /**
         * Returns the mutable list of child widgets.
         *
         * @return the children
         */
        public List<Object> children() {
            return children;
        }
    }
}
