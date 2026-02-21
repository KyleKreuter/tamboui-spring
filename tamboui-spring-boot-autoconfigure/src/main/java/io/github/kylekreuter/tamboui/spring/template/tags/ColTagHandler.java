package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import dev.tamboui.layout.Constraint;

import java.util.Map;

/**
 * Tag handler for {@code <t:col>}.
 * Defines a column for a parent {@code <t:table>} element.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code header} - The column header text (required)</li>
 *   <li>{@code width} - The column width in cells (optional, defaults to fill)</li>
 * </ul>
 *
 * <pre>{@code
 * <t:col header="Name" width="20" />
 * <t:col header="Age" />
 * }</pre>
 */
public class ColTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "col";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String header = attributes.get("header");
        if (header == null) {
            header = "";
        }

        String widthStr = attributes.get("width");
        Constraint constraint;
        if (widthStr != null) {
            try {
                int width = Integer.parseInt(widthStr.trim());
                constraint = Constraint.length(width);
            } catch (NumberFormatException e) {
                // If width is not a valid number, fall back to fill
                constraint = Constraint.fill();
            }
        } else {
            constraint = Constraint.fill();
        }

        return new ColumnDefinition(header, constraint);
    }

    /**
     * Holds the column definition (header text and width constraint)
     * for use by the {@link TableTagHandler}.
     */
    public static final class ColumnDefinition {
        private final String header;
        private final Constraint constraint;

        ColumnDefinition(String header, Constraint constraint) {
            this.header = header;
            this.constraint = constraint;
        }

        /**
         * Returns the column header text.
         *
         * @return the header text
         */
        public String header() {
            return header;
        }

        /**
         * Returns the column width constraint.
         *
         * @return the width constraint
         */
        public Constraint constraint() {
            return constraint;
        }
    }
}
