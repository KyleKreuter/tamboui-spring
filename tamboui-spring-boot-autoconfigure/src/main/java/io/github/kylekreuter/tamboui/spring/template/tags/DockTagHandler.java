package io.github.kylekreuter.tamboui.spring.template.tags;

import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tag handler for {@code <t:dock>}.
 * Creates a wrapper representing a 5-region dock layout (top, bottom, left, right, center)
 * -- the most common TUI application structure (header + sidebar + content + footer).
 * <p>
 * Children are assigned to regions via a {@code region} attribute on each child element.
 * If no region is specified, children default to the center region.
 * <p>
 * The dock itself supports size constraints per region via top-level attributes:
 * <ul>
 *   <li>{@code top-height} - Height constraint for the top region (e.g. "3")</li>
 *   <li>{@code bottom-height} - Height constraint for the bottom region</li>
 *   <li>{@code left-width} - Width constraint for the left region (e.g. "20")</li>
 *   <li>{@code right-width} - Width constraint for the right region</li>
 *   <li>{@code class} - CSS class names for styling</li>
 *   <li>{@code id} - Element identifier</li>
 * </ul>
 * <p>
 * Children specify their dock region via the {@code region} attribute:
 * <pre>{@code
 * <t:dock top-height="3" left-width="20">
 *   <t:panel region="top" title="Header" />
 *   <t:panel region="left" title="Sidebar" />
 *   <t:text region="center" t:text="Main Content" />
 *   <t:panel region="bottom" title="Footer" />
 * </t:dock>
 * }</pre>
 *
 * @see io.github.kylekreuter.tamboui.spring.template.ParentTagHandler
 */
public class DockTagHandler implements ParentTagHandler {

    @Override
    public String getTagName() {
        return "dock";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        DockWidget widget = new DockWidget();

        String topHeight = attributes.get("top-height");
        if (topHeight != null && !topHeight.isBlank()) {
            widget.setTopHeight(topHeight.trim());
        }

        String bottomHeight = attributes.get("bottom-height");
        if (bottomHeight != null && !bottomHeight.isBlank()) {
            widget.setBottomHeight(bottomHeight.trim());
        }

        String leftWidth = attributes.get("left-width");
        if (leftWidth != null && !leftWidth.isBlank()) {
            widget.setLeftWidth(leftWidth.trim());
        }

        String rightWidth = attributes.get("right-width");
        if (rightWidth != null && !rightWidth.isBlank()) {
            widget.setRightWidth(rightWidth.trim());
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

    /**
     * Assigns children to dock regions based on their {@code region} attribute.
     * <p>
     * Since the template engine resolves attributes before calling this method,
     * child objects are paired with their region metadata. Children without a
     * {@code region} attribute default to the center region.
     * <p>
     * This method accepts generic child objects and stores them keyed by region
     * name. The downstream rendering pipeline is responsible for creating the
     * actual TamboUI {@code Dock} widget from this intermediate representation.
     *
     * @param parent   the {@link DockWidget} returned by {@link #createElement}
     * @param children the rendered child widgets (region assignment happens via
     *                 {@link RegionChild} wrappers or defaults to center)
     */
    @Override
    public void addChildren(Object parent, List<Object> children) {
        if (parent instanceof DockWidget dockWidget) {
            for (Object child : children) {
                if (child instanceof RegionChild regionChild) {
                    dockWidget.setRegion(regionChild.region(), regionChild.child());
                } else {
                    // Default to center if no region specified
                    dockWidget.setRegion("center", child);
                }
            }
        }
    }

    /**
     * Wrapper that pairs a child widget with its target dock region.
     * <p>
     * Template processing creates these wrappers when a child element has a
     * {@code region} attribute, allowing the {@code DockTagHandler} to route
     * children to the correct dock region.
     *
     * @param region the dock region name (top, bottom, left, right, center)
     * @param child  the child widget to place in that region
     */
    public record RegionChild(String region, Object child) {

        /**
         * Creates a new RegionChild.
         *
         * @param region the dock region name
         * @param child  the child widget
         */
        public RegionChild {
            if (region == null || region.isBlank()) {
                throw new IllegalArgumentException("Region must not be null or blank");
            }
            if (child == null) {
                throw new IllegalArgumentException("Child must not be null");
            }
            region = region.trim().toLowerCase();
        }

        /**
         * Checks if the region name is a valid dock region.
         *
         * @return {@code true} if the region is one of top, bottom, left, right, center
         */
        public boolean isValidRegion() {
            return "top".equals(region) || "bottom".equals(region)
                    || "left".equals(region) || "right".equals(region)
                    || "center".equals(region);
        }
    }

    /**
     * Wrapper that holds dock layout configuration and region-assigned child widgets.
     * This intermediate representation carries all dock attributes through the
     * rendering pipeline before being converted to TamboUI's {@code DockElement}.
     */
    public static final class DockWidget {
        private String topHeight;
        private String bottomHeight;
        private String leftWidth;
        private String rightWidth;
        private String cssClass;
        private String id;
        private final Map<String, Object> regions = new LinkedHashMap<>();

        /**
         * Returns the height constraint for the top region.
         *
         * @return the top height or {@code null}
         */
        public String topHeight() {
            return topHeight;
        }

        /**
         * Sets the height constraint for the top region.
         *
         * @param topHeight the top height constraint string
         */
        public void setTopHeight(String topHeight) {
            this.topHeight = topHeight;
        }

        /**
         * Returns the height constraint for the bottom region.
         *
         * @return the bottom height or {@code null}
         */
        public String bottomHeight() {
            return bottomHeight;
        }

        /**
         * Sets the height constraint for the bottom region.
         *
         * @param bottomHeight the bottom height constraint string
         */
        public void setBottomHeight(String bottomHeight) {
            this.bottomHeight = bottomHeight;
        }

        /**
         * Returns the width constraint for the left region.
         *
         * @return the left width or {@code null}
         */
        public String leftWidth() {
            return leftWidth;
        }

        /**
         * Sets the width constraint for the left region.
         *
         * @param leftWidth the left width constraint string
         */
        public void setLeftWidth(String leftWidth) {
            this.leftWidth = leftWidth;
        }

        /**
         * Returns the width constraint for the right region.
         *
         * @return the right width or {@code null}
         */
        public String rightWidth() {
            return rightWidth;
        }

        /**
         * Sets the width constraint for the right region.
         *
         * @param rightWidth the right width constraint string
         */
        public void setRightWidth(String rightWidth) {
            this.rightWidth = rightWidth;
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
         * Sets a child widget for a specific dock region.
         * If a widget already exists in the region, it is replaced.
         *
         * @param region the region name (top, bottom, left, right, center)
         * @param child  the child widget
         */
        public void setRegion(String region, Object child) {
            regions.put(region.toLowerCase(), child);
        }

        /**
         * Returns the child widget assigned to a specific region.
         *
         * @param region the region name
         * @return the child widget or {@code null}
         */
        public Object getRegion(String region) {
            return regions.get(region.toLowerCase());
        }

        /**
         * Returns the map of all region assignments.
         *
         * @return unmodifiable view of region-to-child mappings
         */
        public Map<String, Object> regions() {
            return Map.copyOf(regions);
        }

        /**
         * Returns the child widget assigned to the top region.
         *
         * @return the top child or {@code null}
         */
        public Object top() {
            return regions.get("top");
        }

        /**
         * Returns the child widget assigned to the bottom region.
         *
         * @return the bottom child or {@code null}
         */
        public Object bottom() {
            return regions.get("bottom");
        }

        /**
         * Returns the child widget assigned to the left region.
         *
         * @return the left child or {@code null}
         */
        public Object left() {
            return regions.get("left");
        }

        /**
         * Returns the child widget assigned to the right region.
         *
         * @return the right child or {@code null}
         */
        public Object right() {
            return regions.get("right");
        }

        /**
         * Returns the child widget assigned to the center region.
         *
         * @return the center child or {@code null}
         */
        public Object center() {
            return regions.get("center");
        }
    }
}
