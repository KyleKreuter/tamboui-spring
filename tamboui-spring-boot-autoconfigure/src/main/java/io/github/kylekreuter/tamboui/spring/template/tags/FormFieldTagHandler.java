package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Map;

/**
 * Tag handler for {@code <t:form-field>}.
 * Creates a wrapper representing a TamboUI {@code FormFieldElement} which provides
 * a labeled form field with configurable input type, styling, and state binding.
 * <p>
 * The form-field can operate in two modes:
 * <ul>
 *   <li><b>Form field</b> — when used inside a {@code <t:form>}, references a field
 *       name within the parent form's {@link dev.tamboui.widgets.form.FormState FormState}
 *       via the {@code field} attribute</li>
 *   <li><b>Standalone</b> — when used outside a {@code <t:form>}, binds to a state
 *       object from the model via the {@code bind} attribute</li>
 * </ul>
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code label} - Display label for the form field</li>
 *   <li>{@code field} - Field name within a parent {@code <t:form>} FormState</li>
 *   <li>{@code bind} - State binding name for standalone usage</li>
 *   <li>{@code type} - Field type: text, checkbox, toggle, or select (default: text)</li>
 *   <li>{@code label-width} - Width of the label in characters</li>
 *   <li>{@code placeholder} - Placeholder text shown when the input is empty</li>
 *   <li>{@code border-type} - Border style: rounded or default</li>
 *   <li>{@code border-color} - Border color name</li>
 *   <li>{@code focused-border-color} - Border color when the field is focused</li>
 * </ul>
 * <p>
 * Example (within form):
 * <pre>{@code
 * <t:form bind="settingsForm">
 *     <t:form-field label="Username" field="username" placeholder="Enter name" />
 *     <t:form-field label="Active" field="active" type="toggle" />
 * </t:form>
 * }</pre>
 * <p>
 * Example (standalone):
 * <pre>{@code
 * <t:form-field label="Search" bind="searchInput" placeholder="Type to search..." />
 * }</pre>
 */
public class FormFieldTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "form-field";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String label = attributes.get("label");
        String field = attributes.get("field");
        String bind = attributes.get("bind");
        String type = attributes.get("type");
        String labelWidth = attributes.get("label-width");
        String placeholder = attributes.get("placeholder");
        String borderType = attributes.get("border-type");
        String borderColor = attributes.get("border-color");
        String focusedBorderColor = attributes.get("focused-border-color");

        return new FormFieldWidget(label, field, bind, type, labelWidth,
                placeholder, borderType, borderColor, focusedBorderColor);
    }

    /**
     * Widget wrapper that holds all form field configuration attributes.
     * This intermediate representation carries all attributes through the
     * rendering pipeline before being converted to TamboUI's {@code FormFieldElement}.
     */
    public static final class FormFieldWidget {
        private final String label;
        private final String field;
        private final String bind;
        private final String type;
        private final String labelWidth;
        private final String placeholder;
        private final String borderType;
        private final String borderColor;
        private final String focusedBorderColor;

        /**
         * Creates a new FormFieldWidget.
         *
         * @param label              display label for the field (may be {@code null})
         * @param field              field name within a parent form (may be {@code null})
         * @param bind               standalone state binding name (may be {@code null})
         * @param type               field type: text, checkbox, toggle, select (may be {@code null})
         * @param labelWidth         label width in characters (may be {@code null})
         * @param placeholder        placeholder text (may be {@code null})
         * @param borderType         border style (may be {@code null})
         * @param borderColor        border color name (may be {@code null})
         * @param focusedBorderColor focused border color name (may be {@code null})
         */
        public FormFieldWidget(String label, String field, String bind, String type,
                               String labelWidth, String placeholder, String borderType,
                               String borderColor, String focusedBorderColor) {
            this.label = label;
            this.field = field;
            this.bind = bind;
            this.type = type;
            this.labelWidth = labelWidth;
            this.placeholder = placeholder;
            this.borderType = borderType;
            this.borderColor = borderColor;
            this.focusedBorderColor = focusedBorderColor;
        }

        /**
         * Returns the display label for the form field.
         *
         * @return the label, or {@code null}
         */
        public String label() {
            return label;
        }

        /**
         * Returns the field name for form usage.
         *
         * @return the field name, or {@code null} for standalone fields
         */
        public String field() {
            return field;
        }

        /**
         * Returns the state binding name for standalone usage.
         *
         * @return the binding name, or {@code null} for form fields
         */
        public String bind() {
            return bind;
        }

        /**
         * Returns the field type (text, checkbox, toggle, select).
         *
         * @return the type, or {@code null} (defaults to text)
         */
        public String type() {
            return type;
        }

        /**
         * Returns the label width in characters.
         *
         * @return the label width string, or {@code null}
         */
        public String labelWidth() {
            return labelWidth;
        }

        /**
         * Returns the placeholder text.
         *
         * @return the placeholder, or {@code null}
         */
        public String placeholder() {
            return placeholder;
        }

        /**
         * Returns the border type (e.g. "rounded").
         *
         * @return the border type, or {@code null}
         */
        public String borderType() {
            return borderType;
        }

        /**
         * Returns the border color name.
         *
         * @return the border color, or {@code null}
         */
        public String borderColor() {
            return borderColor;
        }

        /**
         * Returns the focused border color name.
         *
         * @return the focused border color, or {@code null}
         */
        public String focusedBorderColor() {
            return focusedBorderColor;
        }
    }
}
