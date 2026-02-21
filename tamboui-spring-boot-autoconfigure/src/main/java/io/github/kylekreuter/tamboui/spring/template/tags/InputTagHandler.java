package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import dev.tamboui.widgets.input.TextInput;
import dev.tamboui.widgets.input.TextInputState;

import java.util.Map;

/**
 * Tag handler for {@code <t:input>}.
 * Creates a TamboUI {@link TextInput} widget connected to a {@link TextInputState}.
 * <p>
 * The input can operate in two modes:
 * <ul>
 *   <li><b>Standalone</b> — when used outside a {@code <t:form>}, binds to a
 *       {@link TextInputState} from the model via the {@code bind} attribute</li>
 *   <li><b>Form field</b> — when used inside a {@code <t:form>}, references a field
 *       name within the parent form's {@link dev.tamboui.widgets.form.FormState FormState}
 *       via the {@code field} attribute</li>
 * </ul>
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code field} - Field name within a parent {@code <t:form>} FormState</li>
 *   <li>{@code bind} - State binding name for standalone usage (references a
 *       {@link TextInputState} in the model)</li>
 *   <li>{@code placeholder} - Placeholder text shown when the input is empty</li>
 * </ul>
 * <p>
 * Example (standalone):
 * <pre>{@code
 * <t:input bind="searchInput" placeholder="Search..." />
 * }</pre>
 * <p>
 * Example (within form):
 * <pre>{@code
 * <t:form bind="settingsForm">
 *     <t:input field="username" placeholder="Username" />
 * </t:form>
 * }</pre>
 */
public class InputTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "input";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String field = attributes.get("field");
        String bind = attributes.get("bind");
        String placeholder = attributes.get("placeholder");

        TextInput.Builder builder = TextInput.builder();

        if (placeholder != null) {
            builder.placeholder(placeholder);
        }

        TextInput textInput = builder.build();

        return new InputWidget(textInput, field, bind, placeholder);
    }

    /**
     * Widget wrapper that holds the TextInput, its field reference (for form usage),
     * its bind reference (for standalone usage), and the placeholder text.
     * <p>
     * The rendering pipeline inspects these attributes to connect the input to
     * the appropriate {@link TextInputState}.
     */
    public static final class InputWidget {
        private final TextInput textInput;
        private final String field;
        private final String bind;
        private final String placeholder;

        /**
         * Creates a new InputWidget.
         *
         * @param textInput   the TamboUI TextInput widget
         * @param field       field name within a parent form (may be {@code null})
         * @param bind        standalone state binding name (may be {@code null})
         * @param placeholder placeholder text (may be {@code null})
         */
        public InputWidget(TextInput textInput, String field, String bind, String placeholder) {
            this.textInput = textInput;
            this.field = field;
            this.bind = bind;
            this.placeholder = placeholder;
        }

        /**
         * Returns the TamboUI TextInput widget.
         *
         * @return the text input
         */
        public TextInput textInput() {
            return textInput;
        }

        /**
         * Returns the field name for form usage.
         *
         * @return the field name, or {@code null} for standalone inputs
         */
        public String field() {
            return field;
        }

        /**
         * Returns the state binding name for standalone usage.
         *
         * @return the binding name, or {@code null} for form inputs
         */
        public String bind() {
            return bind;
        }

        /**
         * Returns the placeholder text.
         *
         * @return the placeholder, or {@code null}
         */
        public String placeholder() {
            return placeholder;
        }
    }
}
