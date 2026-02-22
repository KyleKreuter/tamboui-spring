package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import dev.tamboui.widgets.select.Select;

import java.util.Map;

/**
 * Tag handler for {@code <t:select>}.
 * Creates a TamboUI {@link Select} widget connected to a
 * {@link dev.tamboui.widgets.select.SelectState SelectState} or
 * {@link dev.tamboui.widgets.form.SelectFieldState SelectFieldState}.
 * <p>
 * The select can operate in two modes:
 * <ul>
 *   <li><b>Form field</b> — when used inside a {@code <t:form>}, references a field
 *       name within the parent form's {@link dev.tamboui.widgets.form.FormState FormState}
 *       via the {@code field} attribute. The field must be registered as a select field.</li>
 *   <li><b>Standalone</b> — when used outside a {@code <t:form>}, binds to a
 *       {@link dev.tamboui.widgets.select.SelectState SelectState} from the model
 *       via the {@code bind} attribute. Optionally, the {@code options} attribute
 *       provides comma-separated default options when no state is bound.</li>
 * </ul>
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code field} - Field name within a parent {@code <t:form>} FormState</li>
 *   <li>{@code bind} - State binding name for standalone usage (references a
 *       {@link dev.tamboui.widgets.select.SelectState SelectState} in the model)</li>
 *   <li>{@code options} - Comma-separated option values for standalone usage without
 *       a pre-defined state (e.g. {@code "Option A,Option B,Option C"})</li>
 *   <li>{@code left-indicator} - Custom left navigation indicator (default: {@code "< "})</li>
 *   <li>{@code right-indicator} - Custom right navigation indicator (default: {@code " >"})</li>
 * </ul>
 * <p>
 * Example (standalone with state binding):
 * <pre>{@code
 * <t:select bind="themeSelect" />
 * }</pre>
 * <p>
 * Example (standalone with inline options):
 * <pre>{@code
 * <t:select options="Light,Dark,System" />
 * }</pre>
 * <p>
 * Example (within form):
 * <pre>{@code
 * <t:form bind="settingsForm">
 *     <t:select field="country" />
 * </t:form>
 * }</pre>
 */
public class SelectTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "select";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String field = attributes.get("field");
        String bind = attributes.get("bind");
        String options = attributes.get("options");
        String leftIndicator = attributes.get("left-indicator");
        String rightIndicator = attributes.get("right-indicator");

        Select.Builder builder = Select.builder();

        if (leftIndicator != null) {
            builder.leftIndicator(leftIndicator);
        }

        if (rightIndicator != null) {
            builder.rightIndicator(rightIndicator);
        }

        Select select = builder.build();

        return new SelectWidget(select, field, bind, options);
    }

    /**
     * Widget wrapper that holds the Select, its field reference (for form usage),
     * its bind reference (for standalone usage), and the options string.
     * <p>
     * The rendering pipeline inspects these attributes to connect the select to
     * the appropriate {@link dev.tamboui.widgets.select.SelectState SelectState} or
     * {@link dev.tamboui.widgets.form.SelectFieldState SelectFieldState}.
     */
    public static final class SelectWidget {
        private final Select select;
        private final String field;
        private final String bind;
        private final String options;

        /**
         * Creates a new SelectWidget.
         *
         * @param select  the TamboUI Select widget
         * @param field   field name within a parent form (may be {@code null})
         * @param bind    standalone state binding name (may be {@code null})
         * @param options comma-separated option values for standalone fallback (may be {@code null})
         */
        public SelectWidget(Select select, String field, String bind, String options) {
            this.select = select;
            this.field = field;
            this.bind = bind;
            this.options = options;
        }

        /**
         * Returns the TamboUI Select widget.
         *
         * @return the select widget
         */
        public Select select() {
            return select;
        }

        /**
         * Returns the field name for form usage.
         *
         * @return the field name, or {@code null} for standalone selects
         */
        public String field() {
            return field;
        }

        /**
         * Returns the state binding name for standalone usage.
         *
         * @return the binding name, or {@code null} for form selects
         */
        public String bind() {
            return bind;
        }

        /**
         * Returns the comma-separated options string for standalone fallback.
         *
         * @return the options string, or {@code null}
         */
        public String options() {
            return options;
        }
    }
}
