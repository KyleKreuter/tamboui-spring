package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:grid>}.
 * Creates a wrapper representing a CSS Grid-inspired layout container that arranges
 * children into rows and columns with configurable sizing and gutter spacing.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code grid-size} - Grid dimensions: "3" (3 columns, auto rows) or "3 4" (3 columns, 4 rows)</li>
 *   <li>{@code grid-columns} - Space-separated column constraints, e.g. "fill fill(2) 20"</li>
 *   <li>{@code grid-rows} - Space-separated row constraints, e.g. "2 3"</li>
 *   <li>{@code gutter} - Gutter spacing: "2" (uniform) or "1 2" (horizontal vertical)</li>
 *   <li>{@code grid-template-areas} - Named area definitions, e.g. "header header; nav main"</li>
 *   <li>{@code class} - CSS class names for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class GridTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "grid";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        GridWidget widget = new GridWidget();

        String gridSize = attributes.get("grid-size");
        if (gridSize != null && !gridSize.isBlank()) {
            widget.setGridSize(gridSize.trim());
        }

        String gridColumns = attributes.get("grid-columns");
        if (gridColumns != null && !gridColumns.isBlank()) {
            widget.setGridColumns(gridColumns.trim());
        }

        String gridRows = attributes.get("grid-rows");
        if (gridRows != null && !gridRows.isBlank()) {
            widget.setGridRows(gridRows.trim());
        }

        String gutter = attributes.get("gutter");
        if (gutter != null && !gutter.isBlank()) {
            widget.setGutter(gutter.trim());
        }

        String gridTemplateAreas = attributes.get("grid-template-areas");
        if (gridTemplateAreas != null && !gridTemplateAreas.isBlank()) {
            widget.setGridTemplateAreas(gridTemplateAreas.trim());
        }

        String cssClass = attributes.get("class");
        if (cssClass != null && !cssClass.isBlank()) {
            widget.setCssClass(cssClass.trim());
        }

        String id = attributes.get("id");
        if (id != null && !id.isBlank()) {
            widget.setId(id.trim());
        }

        return widget;
    }

    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof GridWidget gridWidget) {
            gridWidget.children().addAll(children);
        }
    }

    /**
     * Wrapper that holds grid layout configuration and its child widgets.
     * This intermediate representation carries all grid attributes through the
     * rendering pipeline before being converted to TamboUI's {@code GridElement}.
     */
    public static final class GridWidget {
        private String gridSize;
        private String gridColumns;
        private String gridRows;
        private String gutter;
        private String gridTemplateAreas;
        private String cssClass;
        private String id;
        private final List<Object> children = new ArrayList<>();

        /**
         * Returns the grid-size value (e.g. "3" or "3 4").
         *
         * @return the grid size or {@code null}
         */
        public String gridSize() {
            return gridSize;
        }

        /**
         * Sets the grid-size value.
         *
         * @param gridSize the grid size string
         */
        public void setGridSize(String gridSize) {
            this.gridSize = gridSize;
        }

        /**
         * Returns the grid-columns constraint string.
         *
         * @return the grid columns or {@code null}
         */
        public String gridColumns() {
            return gridColumns;
        }

        /**
         * Sets the grid-columns constraint string.
         *
         * @param gridColumns the grid columns string
         */
        public void setGridColumns(String gridColumns) {
            this.gridColumns = gridColumns;
        }

        /**
         * Returns the grid-rows constraint string.
         *
         * @return the grid rows or {@code null}
         */
        public String gridRows() {
            return gridRows;
        }

        /**
         * Sets the grid-rows constraint string.
         *
         * @param gridRows the grid rows string
         */
        public void setGridRows(String gridRows) {
            this.gridRows = gridRows;
        }

        /**
         * Returns the gutter spacing value.
         *
         * @return the gutter value or {@code null}
         */
        public String gutter() {
            return gutter;
        }

        /**
         * Sets the gutter spacing value.
         *
         * @param gutter the gutter string
         */
        public void setGutter(String gutter) {
            this.gutter = gutter;
        }

        /**
         * Returns the grid-template-areas definition.
         *
         * @return the grid template areas or {@code null}
         */
        public String gridTemplateAreas() {
            return gridTemplateAreas;
        }

        /**
         * Sets the grid-template-areas definition.
         *
         * @param gridTemplateAreas the grid template areas string
         */
        public void setGridTemplateAreas(String gridTemplateAreas) {
            this.gridTemplateAreas = gridTemplateAreas;
        }

        /**
         * Returns the CSS class names.
         *
         * @return the CSS class or {@code null}
         */
        public String cssClass() {
            return cssClass;
        }

        /**
         * Sets the CSS class names.
         *
         * @param cssClass the CSS class string
         */
        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }

        /**
         * Returns the element identifier.
         *
         * @return the id or {@code null}
         */
        public String id() {
            return id;
        }

        /**
         * Sets the element identifier.
         *
         * @param id the element id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Returns the mutable list of child widgets.
         *
         * @return the children list
         */
        public List<Object> children() {
            return children;
        }
    }
}
