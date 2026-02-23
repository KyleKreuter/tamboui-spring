package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import dev.tamboui.layout.Constraint;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tag handler for {@code <t:table>}.
 * Creates a TamboUI {@link Table} with columns defined by {@code <t:col>} children.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code class} - CSS class for styling</li>
 *   <li>{@code id} - Element identifier</li>
 *   <li>{@code highlight-color} - Color for the selected row highlight</li>
 * </ul>
 * <p>
 * Columns are defined via child {@code <t:col header="..." width="...">} tags.
 * Rows can be added dynamically via the {@code t:each} attribute with a SpEL
 * expression, handled by the {@link io.github.kylekreuter.tamboui.spring.template.TemplateEngine}.
 *
 * <pre>{@code
 * <t:table id="users" highlight-color="BLUE">
 *   <t:col header="Name" width="20" />
 *   <t:col header="Age" width="10" />
 * </t:table>
 * }</pre>
 */
public class TableTagHandler implements ParentTagHandler {

    private static final Map<String, Color> COLOR_MAP = Map.ofEntries(
            Map.entry("black", Color.BLACK),
            Map.entry("red", Color.RED),
            Map.entry("green", Color.GREEN),
            Map.entry("yellow", Color.YELLOW),
            Map.entry("blue", Color.BLUE),
            Map.entry("magenta", Color.MAGENTA),
            Map.entry("cyan", Color.CYAN),
            Map.entry("white", Color.WHITE),
            Map.entry("gray", Color.GRAY),
            Map.entry("dark_gray", Color.DARK_GRAY),
            Map.entry("dark-gray", Color.DARK_GRAY),
            Map.entry("light_red", Color.LIGHT_RED),
            Map.entry("light-red", Color.LIGHT_RED),
            Map.entry("light_green", Color.LIGHT_GREEN),
            Map.entry("light-green", Color.LIGHT_GREEN),
            Map.entry("light_yellow", Color.LIGHT_YELLOW),
            Map.entry("light-yellow", Color.LIGHT_YELLOW),
            Map.entry("light_blue", Color.LIGHT_BLUE),
            Map.entry("light-blue", Color.LIGHT_BLUE),
            Map.entry("light_magenta", Color.LIGHT_MAGENTA),
            Map.entry("light-magenta", Color.LIGHT_MAGENTA),
            Map.entry("light_cyan", Color.LIGHT_CYAN),
            Map.entry("light-cyan", Color.LIGHT_CYAN),
            Map.entry("bright_white", Color.BRIGHT_WHITE),
            Map.entry("bright-white", Color.BRIGHT_WHITE)
    );

    /**
     * Resolves a color name string to a TamboUI {@link Color} constant.
     * Supports named ANSI colors (case-insensitive) and hex color codes
     * prefixed with {@code #}.
     *
     * @param name the color name or hex code
     * @return the resolved Color, or {@code null} if the name is not recognized
     */
    public static Color resolveColor(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        if (name.startsWith("#")) {
            try {
                return Color.hex(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return COLOR_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public String getTagName() {
        return "table";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String id = attributes.get("id");
        String cssClass = attributes.get("class");
        String highlightColor = attributes.get("highlight-color");

        return new TableWidgetHolder(id, cssClass, highlightColor);
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (!(parent instanceof TableWidgetHolder holder)) {
            return;
        }

        List<ColTagHandler.ColumnDefinition> columns = new ArrayList<>();
        List<Row> rows = new ArrayList<>();

        for (Object child : children) {
            if (child instanceof ColTagHandler.ColumnDefinition colDef) {
                columns.add(colDef);
            } else if (child instanceof Row row) {
                rows.add(row);
            }
        }

        holder.setColumns(columns);
        holder.setRows(rows);
    }

    /**
     * Holder that carries the table configuration through the rendering pipeline.
     * The final {@link Table} is built lazily via {@link #build()} after all
     * children (columns and rows) have been added.
     */
    public static final class TableWidgetHolder {
        private final String id;
        private final String cssClass;
        private final String highlightColor;
        private List<ColTagHandler.ColumnDefinition> columns = new ArrayList<>();
        private List<Row> rows = new ArrayList<>();

        TableWidgetHolder(String id, String cssClass, String highlightColor) {
            this.id = id;
            this.cssClass = cssClass;
            this.highlightColor = highlightColor;
        }

        void setColumns(List<ColTagHandler.ColumnDefinition> columns) {
            this.columns = columns;
        }

        void setRows(List<Row> rows) {
            this.rows = rows;
        }

        /**
         * Returns the element identifier, or {@code null} if not set.
         *
         * @return the element id
         */
        public String id() {
            return id;
        }

        /**
         * Returns the CSS class, or {@code null} if not set.
         *
         * @return the CSS class
         */
        public String cssClass() {
            return cssClass;
        }

        /**
         * Returns the highlight color string, or {@code null} if not set.
         *
         * @return the highlight color
         */
        public String highlightColor() {
            return highlightColor;
        }

        /**
         * Returns the column definitions.
         *
         * @return the column definitions
         */
        public List<ColTagHandler.ColumnDefinition> columns() {
            return columns;
        }

        /**
         * Returns the data rows.
         *
         * @return the data rows
         */
        public List<Row> rows() {
            return rows;
        }

        /**
         * Builds the final {@link Table} from the collected columns and rows.
         *
         * @return the built Table
         */
        public Table build() {
            Table.Builder builder = Table.builder();

            // Build header row and width constraints from column definitions
            if (!columns.isEmpty()) {
                List<Cell> headerCells = new ArrayList<>();
                List<Constraint> widths = new ArrayList<>();

                for (ColTagHandler.ColumnDefinition col : columns) {
                    headerCells.add(Cell.from(col.header()));
                    widths.add(col.constraint());
                }

                builder.header(Row.from(headerCells).style(Style.EMPTY.bold()));
                builder.widths(widths);
            }

            // Add data rows
            builder.rows(rows);

            // Apply highlight color if specified
            if (highlightColor != null && !highlightColor.isBlank()) {
                Color color = resolveColor(highlightColor);
                if (color != null) {
                    builder.highlightColor(color);
                }
            }

            return builder.build();
        }
    }
}
