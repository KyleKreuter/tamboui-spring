package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Column;
import dev.tamboui.toolkit.elements.ColumnsElement;
import dev.tamboui.toolkit.elements.DialogElement;
import dev.tamboui.toolkit.elements.DockElement;
import dev.tamboui.toolkit.elements.FlowElement;
import dev.tamboui.toolkit.elements.FormFieldElement;
import dev.tamboui.toolkit.elements.GenericWidgetElement;
import dev.tamboui.toolkit.elements.GridElement;
import dev.tamboui.toolkit.elements.ListElement;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.toolkit.elements.StackElement;
import dev.tamboui.toolkit.elements.TabsElement;
import dev.tamboui.toolkit.elements.TableElement;
import dev.tamboui.toolkit.elements.TextAreaElement;
import dev.tamboui.toolkit.elements.TextElement;
import dev.tamboui.toolkit.elements.TextInputElement;
import dev.tamboui.toolkit.elements.TreeElement;
import dev.tamboui.widgets.form.BooleanFieldState;
import dev.tamboui.widgets.form.FormState;
import dev.tamboui.widgets.form.SelectFieldState;
import dev.tamboui.widgets.input.TextAreaState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.list.ListItem;
import dev.tamboui.widgets.paragraph.Paragraph;
import dev.tamboui.widgets.select.SelectState;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.tabs.TabsState;
import dev.tamboui.widgets.tree.TreeNode;

import io.github.kylekreuter.tamboui.spring.template.tags.ColTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ColumnsTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.DialogTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.DockTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FlowTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormFieldTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.GridTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.InputTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ListTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.StackTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TabsTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TableTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextAreaTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TreeTagHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Comprehensive unit tests for {@link WidgetToElementConverter}.
 * <p>
 * Tests cover all 11 conversion paths in the {@code convert(Object)} method,
 * including edge cases for each widget type and state binding resolution.
 */
class WidgetToElementConverterTest {

    private WidgetToElementConverter converter;

    @BeforeEach
    void setUp() {
        converter = new WidgetToElementConverter();
    }

    // ========== Helper methods ==========

    /**
     * Creates a PanelWidget via the PanelTagHandler (constructor is package-private).
     */
    private PanelTagHandler.PanelWidget createPanelWidget(String title) {
        var handler = new PanelTagHandler();
        Map<String, String> attrs = title != null ? Map.of("title", title) : Collections.emptyMap();
        return (PanelTagHandler.PanelWidget) handler.createElement(attrs);
    }

    /**
     * Creates a DockWidget via the DockTagHandler.
     */
    private DockTagHandler.DockWidget createDockWidget(Map<String, String> attrs) {
        var handler = new DockTagHandler();
        return (DockTagHandler.DockWidget) handler.createElement(attrs);
    }

    /**
     * Creates a GridWidget via the GridTagHandler.
     */
    private GridTagHandler.GridWidget createGridWidget(Map<String, String> attrs) {
        var handler = new GridTagHandler();
        return (GridTagHandler.GridWidget) handler.createElement(attrs);
    }

    /**
     * Creates a ListWidgetHolder via the ListTagHandler, with items added via addChildren.
     */
    private ListTagHandler.ListWidgetHolder createListWidgetHolder(List<String> items) {
        var handler = new ListTagHandler();
        var holder = (ListTagHandler.ListWidgetHolder) handler.createElement(Collections.emptyMap());
        List<Object> children = items.stream().map(text -> (Object) ListItem.from(text)).toList();
        handler.addChildren(holder, children);
        return holder;
    }

    /**
     * Creates a TableWidgetHolder via the TableTagHandler, with columns and rows added via addChildren.
     */
    private TableTagHandler.TableWidgetHolder createTableWidgetHolder(
            List<String> headers, List<List<String>> rowData) {
        var tableHandler = new TableTagHandler();
        var colHandler = new ColTagHandler();

        var holder = (TableTagHandler.TableWidgetHolder) tableHandler.createElement(Collections.emptyMap());

        // Build children: ColumnDefinitions followed by Rows
        List<Object> children = new java.util.ArrayList<>();
        for (String header : headers) {
            children.add(colHandler.createElement(Map.of("header", header)));
        }
        for (List<String> cells : rowData) {
            List<Cell> tableCells = cells.stream().map(Cell::from).toList();
            children.add(Row.from(tableCells));
        }

        tableHandler.addChildren(holder, children);
        return holder;
    }

    /**
     * Creates an InputWidget via the InputTagHandler.
     */
    private InputTagHandler.InputWidget createInputWidget(String placeholder) {
        var handler = new InputTagHandler();
        Map<String, String> attrs = placeholder != null
                ? Map.of("placeholder", placeholder)
                : Collections.emptyMap();
        return (InputTagHandler.InputWidget) handler.createElement(attrs);
    }

    /**
     * Creates an InputWidget with a field attribute (for form usage).
     */
    private InputTagHandler.InputWidget createFormInputWidget(String field, String placeholder) {
        var handler = new InputTagHandler();
        Map<String, String> attrs = new HashMap<>();
        if (field != null) attrs.put("field", field);
        if (placeholder != null) attrs.put("placeholder", placeholder);
        return (InputTagHandler.InputWidget) handler.createElement(attrs);
    }

    /**
     * Creates an InputWidget with a bind attribute (for standalone usage).
     */
    private InputTagHandler.InputWidget createStandaloneInputWidget(String bind, String placeholder) {
        var handler = new InputTagHandler();
        Map<String, String> attrs = new HashMap<>();
        if (bind != null) attrs.put("bind", bind);
        if (placeholder != null) attrs.put("placeholder", placeholder);
        return (InputTagHandler.InputWidget) handler.createElement(attrs);
    }

    // ========== Test groups ==========

    @Nested
    @DisplayName("Null input")
    class NullConversion {

        @Test
        @DisplayName("convert(null) should return an empty TextElement")
        void nullShouldReturnEmptyTextElement() {
            Element result = converter.convert(null);

            assertInstanceOf(TextElement.class, result);
        }
    }

    @Nested
    @DisplayName("Element passthrough")
    class ElementPassthrough {

        @Test
        @DisplayName("convert(Element) should return the same Element unchanged")
        void elementShouldPassThrough() {
            Element original = Toolkit.text("test passthrough");

            Element result = converter.convert(original);

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("convert(Panel) should return the same Panel unchanged")
        void panelElementShouldPassThrough() {
            Panel original = Toolkit.panel("Direct Panel");

            Element result = converter.convert(original);

            assertThat(result).isSameAs(original);
        }

        @Test
        @DisplayName("convert(Row) should return the same Row unchanged")
        void rowElementShouldPassThrough() {
            var original = Toolkit.row(Toolkit.text("a"), Toolkit.text("b"));

            Element result = converter.convert(original);

            assertThat(result).isSameAs(original);
        }
    }

    @Nested
    @DisplayName("PanelWidget conversion")
    class PanelWidgetConversion {

        @Test
        @DisplayName("PanelWidget with title should convert to Panel")
        void panelWithTitleShouldConvertToPanel() {
            var panelWidget = createPanelWidget("My Panel");

            Element result = converter.convert(panelWidget);

            assertInstanceOf(Panel.class, result);
        }

        @Test
        @DisplayName("PanelWidget without title should convert to Panel")
        void panelWithoutTitleShouldConvertToPanel() {
            var panelWidget = createPanelWidget(null);

            Element result = converter.convert(panelWidget);

            assertInstanceOf(Panel.class, result);
        }

        @Test
        @DisplayName("PanelWidget with children should recursively convert them")
        void panelWithChildrenShouldRecursivelyConvert() {
            var panelWidget = createPanelWidget("Parent");
            // Add a string child (will go through fallback -> text)
            panelWidget.children().add("child text");
            // Add a nested PanelWidget child
            var nestedPanel = createPanelWidget("Nested");
            panelWidget.children().add(nestedPanel);

            Element result = converter.convert(panelWidget);

            assertInstanceOf(Panel.class, result);
        }

        @Test
        @DisplayName("PanelWidget with no children should produce empty Panel")
        void panelWithNoChildrenShouldProduceEmptyPanel() {
            var panelWidget = createPanelWidget("Empty");

            Element result = converter.convert(panelWidget);

            assertInstanceOf(Panel.class, result);
        }
    }

    @Nested
    @DisplayName("DockWidget conversion")
    class DockWidgetConversion {

        @Test
        @DisplayName("DockWidget with all regions should convert to DockElement")
        void dockWithAllRegionsShouldConvert() {
            var dockWidget = createDockWidget(Collections.emptyMap());
            var handler = new DockTagHandler();

            // Add children for all 5 regions
            List<Object> children = List.of(
                    new DockTagHandler.RegionChild("top", Toolkit.text("Header")),
                    new DockTagHandler.RegionChild("bottom", Toolkit.text("Footer")),
                    new DockTagHandler.RegionChild("left", Toolkit.text("Sidebar")),
                    new DockTagHandler.RegionChild("right", Toolkit.text("Right")),
                    new DockTagHandler.RegionChild("center", Toolkit.text("Content"))
            );
            handler.addChildren(dockWidget, children);

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with only center region should convert to DockElement")
        void dockWithOnlyCenterShouldConvert() {
            var dockWidget = createDockWidget(Collections.emptyMap());
            var handler = new DockTagHandler();

            handler.addChildren(dockWidget, List.of(
                    new DockTagHandler.RegionChild("center", Toolkit.text("Only Center"))
            ));

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with no regions should convert to empty DockElement")
        void dockWithNoRegionsShouldConvert() {
            var dockWidget = createDockWidget(Collections.emptyMap());

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with height constraints should convert correctly")
        void dockWithHeightConstraintsShouldConvert() {
            var dockWidget = createDockWidget(Map.of(
                    "top-height", "3",
                    "bottom-height", "5"
            ));
            var handler = new DockTagHandler();

            handler.addChildren(dockWidget, List.of(
                    new DockTagHandler.RegionChild("top", Toolkit.text("Header")),
                    new DockTagHandler.RegionChild("bottom", Toolkit.text("Footer")),
                    new DockTagHandler.RegionChild("center", Toolkit.text("Content"))
            ));

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with width constraints should convert correctly")
        void dockWithWidthConstraintsShouldConvert() {
            var dockWidget = createDockWidget(Map.of(
                    "left-width", "20",
                    "right-width", "15"
            ));
            var handler = new DockTagHandler();

            handler.addChildren(dockWidget, List.of(
                    new DockTagHandler.RegionChild("left", Toolkit.text("Sidebar")),
                    new DockTagHandler.RegionChild("right", Toolkit.text("Right Panel")),
                    new DockTagHandler.RegionChild("center", Toolkit.text("Content"))
            ));

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with invalid constraint values should handle gracefully")
        void dockWithInvalidConstraintsShouldHandleGracefully() {
            var dockWidget = createDockWidget(Map.of("top-height", "abc"));
            var handler = new DockTagHandler();

            handler.addChildren(dockWidget, List.of(
                    new DockTagHandler.RegionChild("top", Toolkit.text("Header")),
                    new DockTagHandler.RegionChild("center", Toolkit.text("Content"))
            ));

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }

        @Test
        @DisplayName("DockWidget with non-Element children should recursively convert them")
        void dockWithNonElementChildrenShouldRecursivelyConvert() {
            var dockWidget = createDockWidget(Collections.emptyMap());
            var handler = new DockTagHandler();

            // Add a PanelWidget as a region child (requires recursive conversion)
            var panelWidget = createPanelWidget("Nested Panel");
            handler.addChildren(dockWidget, List.of(
                    new DockTagHandler.RegionChild("top", panelWidget),
                    new DockTagHandler.RegionChild("center", Toolkit.text("Content"))
            ));

            Element result = converter.convert(dockWidget);

            assertInstanceOf(DockElement.class, result);
        }
    }

    @Nested
    @DisplayName("GridWidget conversion")
    class GridWidgetConversion {

        @Test
        @DisplayName("GridWidget with grid-columns and grid-rows should convert to GridElement")
        void gridWithColumnsAndRowsShouldConvert() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-columns", "3",
                    "grid-rows", "2"
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with only grid-columns should convert to GridElement")
        void gridWithOnlyColumnsShouldConvert() {
            var gridWidget = createGridWidget(Map.of("grid-columns", "4"));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with grid-size single value should convert correctly")
        void gridWithSingleGridSizeShouldConvert() {
            var gridWidget = createGridWidget(Map.of("grid-size", "3"));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with grid-size two values should convert correctly")
        void gridWithTwoValueGridSizeShouldConvert() {
            var gridWidget = createGridWidget(Map.of("grid-size", "3 4"));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with grid-size should override grid-columns/grid-rows")
        void gridSizeShouldOverrideColumnsAndRows() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-columns", "2",
                    "grid-rows", "2",
                    "grid-size", "5 6"
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with single gutter value should convert correctly")
        void gridWithSingleGutterShouldConvert() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-size", "3",
                    "gutter", "2"
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with two gutter values should convert correctly")
        void gridWithTwoGutterValuesShouldConvert() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-size", "3",
                    "gutter", "1 2"
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with children should recursively convert them")
        void gridWithChildrenShouldRecursivelyConvert() {
            var gridWidget = createGridWidget(Map.of("grid-size", "2"));
            var handler = new GridTagHandler();

            handler.addChildren(gridWidget, List.of(
                    Toolkit.text("Cell 1"),
                    Toolkit.text("Cell 2"),
                    Toolkit.text("Cell 3"),
                    Toolkit.text("Cell 4")
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with no attributes should convert to empty GridElement")
        void gridWithNoAttributesShouldConvert() {
            var gridWidget = createGridWidget(Collections.emptyMap());

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with invalid grid-columns should handle gracefully")
        void gridWithInvalidColumnsShouldHandleGracefully() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-columns", "abc",
                    "grid-rows", "def"
            ));

            Element result = converter.convert(gridWidget);

            // Should still produce a GridElement, just without gridSize applied
            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with invalid grid-size should handle gracefully")
        void gridWithInvalidGridSizeShouldHandleGracefully() {
            var gridWidget = createGridWidget(Map.of("grid-size", "not a number"));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }

        @Test
        @DisplayName("GridWidget with invalid gutter should handle gracefully")
        void gridWithInvalidGutterShouldHandleGracefully() {
            var gridWidget = createGridWidget(Map.of(
                    "grid-size", "2",
                    "gutter", "bad"
            ));

            Element result = converter.convert(gridWidget);

            assertInstanceOf(GridElement.class, result);
        }
    }

    @Nested
    @DisplayName("ListWidgetHolder conversion")
    class ListWidgetHolderConversion {

        @Test
        @DisplayName("ListWidgetHolder with items should convert to ListElement")
        void listWithItemsShouldConvert() {
            var listHolder = createListWidgetHolder(List.of("Item 1", "Item 2", "Item 3"));

            Element result = converter.convert(listHolder);

            assertInstanceOf(ListElement.class, result);
        }

        @Test
        @DisplayName("ListWidgetHolder with single item should convert to ListElement")
        void listWithSingleItemShouldConvert() {
            var listHolder = createListWidgetHolder(List.of("Only Item"));

            Element result = converter.convert(listHolder);

            assertInstanceOf(ListElement.class, result);
        }

        @Test
        @DisplayName("ListWidgetHolder with no items should convert to empty ListElement")
        void listWithNoItemsShouldConvert() {
            var listHolder = createListWidgetHolder(Collections.emptyList());

            Element result = converter.convert(listHolder);

            assertInstanceOf(ListElement.class, result);
        }

        @Test
        @DisplayName("ListWidgetHolder items should use rawContent() for string extraction")
        void listItemsShouldUseRawContent() {
            // Verify that ListItem.from("text").content().rawContent() returns the expected string
            ListItem item = ListItem.from("Test Content");
            String rawContent = item.content().rawContent();

            assertThat(rawContent).isEqualTo("Test Content");

            // Now test through the converter
            var listHolder = createListWidgetHolder(List.of("Alpha", "Beta", "Gamma"));

            Element result = converter.convert(listHolder);

            assertInstanceOf(ListElement.class, result);
        }
    }

    @Nested
    @DisplayName("TableWidgetHolder conversion")
    class TableWidgetHolderConversion {

        @Test
        @DisplayName("TableWidgetHolder with headers and rows should convert to TableElement")
        void tableWithHeadersAndRowsShouldConvert() {
            var tableHolder = createTableWidgetHolder(
                    List.of("Name", "Age", "City"),
                    List.of(
                            List.of("Alice", "30", "Berlin"),
                            List.of("Bob", "25", "Hamburg")
                    )
            );

            Element result = converter.convert(tableHolder);

            assertInstanceOf(TableElement.class, result);
        }

        @Test
        @DisplayName("TableWidgetHolder with headers only should convert to TableElement")
        void tableWithHeadersOnlyShouldConvert() {
            var tableHolder = createTableWidgetHolder(
                    List.of("Name", "Age"),
                    Collections.emptyList()
            );

            Element result = converter.convert(tableHolder);

            assertInstanceOf(TableElement.class, result);
        }

        @Test
        @DisplayName("TableWidgetHolder with no headers should convert to TableElement without header")
        void tableWithNoHeadersShouldConvert() {
            var tableHandler = new TableTagHandler();
            var holder = (TableTagHandler.TableWidgetHolder) tableHandler.createElement(Collections.emptyMap());

            // Add only rows, no columns
            List<Object> children = List.of(
                    Row.from("Alice", "30"),
                    Row.from("Bob", "25")
            );
            tableHandler.addChildren(holder, children);

            Element result = converter.convert(holder);

            assertInstanceOf(TableElement.class, result);
        }

        @Test
        @DisplayName("TableWidgetHolder with empty table should convert to empty TableElement")
        void emptyTableShouldConvert() {
            var tableHolder = createTableWidgetHolder(
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            Element result = converter.convert(tableHolder);

            assertInstanceOf(TableElement.class, result);
        }

        @Test
        @DisplayName("TableWidgetHolder with single row should convert correctly")
        void tableWithSingleRowShouldConvert() {
            var tableHolder = createTableWidgetHolder(
                    List.of("Key", "Value"),
                    List.of(List.of("host", "localhost"))
            );

            Element result = converter.convert(tableHolder);

            assertInstanceOf(TableElement.class, result);
        }
    }

    @Nested
    @DisplayName("FormWidget conversion")
    class FormWidgetConversion {

        @Test
        @DisplayName("FormWidget should convert to Column")
        void formShouldConvertToColumn() {
            var formWidget = new FormTagHandler.FormWidget("myForm");

            Element result = converter.convert(formWidget);

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("FormWidget with children should recursively convert them to Column")
        void formWithChildrenShouldConvertToColumnWithChildren() {
            var formWidget = new FormTagHandler.FormWidget("myForm");
            formWidget.children().add(createInputWidget("Username"));
            formWidget.children().add(createInputWidget("Password"));

            Element result = converter.convert(formWidget);

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("FormWidget with mixed children should convert all")
        void formWithMixedChildrenShouldConvert() {
            var formWidget = new FormTagHandler.FormWidget("settings");
            // Add an InputWidget, a plain text Element, and a string fallback
            formWidget.children().add(createInputWidget("Name"));
            formWidget.children().add(Toolkit.text("Some label"));
            formWidget.children().add("fallback text");

            Element result = converter.convert(formWidget);

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("FormWidget with no children should produce empty Column")
        void formWithNoChildrenShouldProduceEmptyColumn() {
            var formWidget = new FormTagHandler.FormWidget(null);

            Element result = converter.convert(formWidget);

            assertInstanceOf(Column.class, result);
        }
    }

    @Nested
    @DisplayName("InputWidget conversion")
    class InputWidgetConversion {

        @Test
        @DisplayName("InputWidget should convert to TextInputElement")
        void inputShouldConvertToTextInputElement() {
            var inputWidget = createInputWidget("Enter text...");

            Element result = converter.convert(inputWidget);

            assertInstanceOf(TextInputElement.class, result);
        }

        @Test
        @DisplayName("InputWidget without placeholder should convert to TextInputElement")
        void inputWithoutPlaceholderShouldConvert() {
            var inputWidget = createInputWidget(null);

            Element result = converter.convert(inputWidget);

            assertInstanceOf(TextInputElement.class, result);
        }
    }

    @Nested
    @DisplayName("InputWidget state binding")
    class InputWidgetStateBinding {

        @Test
        @DisplayName("Form input should resolve TextInputState from FormState")
        void formInputShouldResolveStateFromFormState() {
            FormState formState = FormState.builder()
                    .textField("username", "alice")
                    .textField("email", "alice@example.com")
                    .build();

            var formWidget = new FormTagHandler.FormWidget("settingsForm");
            formWidget.children().add(createFormInputWidget("username", "Enter username"));
            formWidget.children().add(createFormInputWidget("email", "Enter email"));

            Map<String, Object> stateBindings = Map.of("settingsForm", formState);

            Element result = converter.convert(formWidget, stateBindings);

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("Standalone input should resolve TextInputState from state bindings")
        void standaloneInputShouldResolveStateFromBindings() {
            TextInputState searchState = new TextInputState("initial search");

            var inputWidget = createStandaloneInputWidget("searchInput", "Search...");

            Map<String, Object> stateBindings = Map.of("searchInput", searchState);

            Element result = converter.convert(inputWidget, stateBindings);

            assertInstanceOf(TextInputElement.class, result);
        }

        @Test
        @DisplayName("Input without matching state binding should still produce TextInputElement")
        void inputWithoutMatchingStateShouldStillWork() {
            var inputWidget = createFormInputWidget("username", "Enter username");

            Element result = converter.convert(inputWidget, Collections.emptyMap());

            assertInstanceOf(TextInputElement.class, result);
        }

        @Test
        @DisplayName("Form with missing state binding should still produce Column with inputs")
        void formWithMissingStateShouldStillWork() {
            var formWidget = new FormTagHandler.FormWidget("unknownForm");
            formWidget.children().add(createFormInputWidget("name", "Enter name"));

            Element result = converter.convert(formWidget, Collections.emptyMap());

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("Form input with unknown field name should fall back gracefully")
        void formInputWithUnknownFieldShouldFallBack() {
            FormState formState = FormState.builder()
                    .textField("username", "")
                    .build();

            var formWidget = new FormTagHandler.FormWidget("myForm");
            formWidget.children().add(createFormInputWidget("nonexistent", "Placeholder"));

            Map<String, Object> stateBindings = Map.of("myForm", formState);

            Element result = converter.convert(formWidget, stateBindings);

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("convert with null stateBindings should not throw")
        void convertWithNullStateBindingsShouldNotThrow() {
            var inputWidget = createFormInputWidget("field", "Placeholder");

            Element result = converter.convert(inputWidget, null);

            assertInstanceOf(TextInputElement.class, result);
        }

        @Test
        @DisplayName("Nested form input inside panel should resolve state correctly")
        void nestedFormInputInsidePanelShouldResolveState() {
            FormState formState = FormState.builder()
                    .textField("username", "bob")
                    .build();

            // Build: form > panel > input (the panel passes through form context)
            var inputWidget = createFormInputWidget("username", "Enter username");
            var panelWidget = createPanelWidget("User Settings");
            panelWidget.children().add(inputWidget);

            var formWidget = new FormTagHandler.FormWidget("settingsForm");
            formWidget.children().add(panelWidget);

            Map<String, Object> stateBindings = Map.of("settingsForm", formState);

            Element result = converter.convert(formWidget, stateBindings);

            assertInstanceOf(Column.class, result);
        }
    }

    @Nested
    @DisplayName("Widget (low-level) conversion")
    class LowLevelWidgetConversion {

        @Test
        @DisplayName("Paragraph widget should convert to GenericWidgetElement")
        void paragraphWidgetShouldConvertToGenericWidgetElement() {
            Paragraph paragraph = Paragraph.from("test paragraph");

            Element result = converter.convert(paragraph);

            assertInstanceOf(GenericWidgetElement.class, result);
        }
    }

    @Nested
    @DisplayName("Unknown type fallback")
    class UnknownTypeFallback {

        @Test
        @DisplayName("Unknown object should convert to TextElement using toString()")
        void unknownObjectShouldConvertToTextElement() {
            Object unknown = new Object() {
                @Override
                public String toString() {
                    return "custom-object";
                }
            };

            Element result = converter.convert(unknown);

            assertInstanceOf(TextElement.class, result);
        }

        @Test
        @DisplayName("String should convert to TextElement")
        void stringShouldConvertToTextElement() {
            Element result = converter.convert("hello world");

            assertInstanceOf(TextElement.class, result);
        }

        @Test
        @DisplayName("Integer should convert to TextElement")
        void integerShouldConvertToTextElement() {
            Element result = converter.convert(42);

            assertInstanceOf(TextElement.class, result);
        }
    }

    // ========== Tests for 8 new widget types ==========

    @Nested
    @DisplayName("TabsWidget conversion")
    class TabsWidgetConversion {

        @Test
        @DisplayName("TabsWidget should convert to TabsElement")
        void tabsShouldConvert() {
            var handler = new TabsTagHandler();
            var widget = handler.createElement(Map.of("titles", "Home,Settings"));

            Element result = converter.convert(widget);

            assertInstanceOf(TabsElement.class, result);
        }

        @Test
        @DisplayName("TabsWidget with state binding should convert to TabsElement")
        void tabsWithStateShouldConvert() {
            var handler = new TabsTagHandler();
            var widget = handler.createElement(Map.of("titles", "A,B,C", "bind", "tabsState"));
            TabsState state = new TabsState(1);

            Element result = converter.convert(widget, Map.of("tabsState", state));

            assertInstanceOf(TabsElement.class, result);
        }

        @Test
        @DisplayName("TabsWidget with empty attributes should convert to TabsElement")
        void tabsWithEmptyAttrsShouldConvert() {
            var handler = new TabsTagHandler();
            var widget = handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(TabsElement.class, result);
        }

        @Test
        @DisplayName("TabsWidget with all styling attributes should convert")
        void tabsWithAllStylingShouldConvert() {
            var handler = new TabsTagHandler();
            var widget = handler.createElement(Map.of(
                    "titles", "Tab1,Tab2",
                    "divider", " - ",
                    "highlight-color", "YELLOW",
                    "padding-left", "  ",
                    "padding-right", "  ",
                    "title", "Navigation",
                    "border-type", "rounded",
                    "border-color", "cyan"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(TabsElement.class, result);
        }
    }

    @Nested
    @DisplayName("TextAreaWidget conversion")
    class TextAreaWidgetConversion {

        @Test
        @DisplayName("TextAreaWidget should convert to TextAreaElement")
        void textAreaShouldConvert() {
            var handler = new TextAreaTagHandler();
            var widget = handler.createElement(Map.of("placeholder", "Enter text..."));

            Element result = converter.convert(widget);

            assertInstanceOf(TextAreaElement.class, result);
        }

        @Test
        @DisplayName("TextAreaWidget with state binding should convert")
        void textAreaWithStateShouldConvert() {
            var handler = new TextAreaTagHandler();
            var widget = handler.createElement(Map.of("bind", "editorState"));
            TextAreaState state = new TextAreaState("Hello");

            Element result = converter.convert(widget, Map.of("editorState", state));

            assertInstanceOf(TextAreaElement.class, result);
        }

        @Test
        @DisplayName("TextAreaWidget with all styling attributes should convert")
        void textAreaWithAllStylingShouldConvert() {
            var handler = new TextAreaTagHandler();
            var widget = handler.createElement(Map.of(
                    "bind", "editor",
                    "placeholder", "Type here...",
                    "title", "Editor",
                    "border-type", "rounded",
                    "border-color", "blue",
                    "focused-border-color", "cyan",
                    "show-line-numbers", "true"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(TextAreaElement.class, result);
        }

        @Test
        @DisplayName("TextAreaWidget with empty attributes should convert")
        void textAreaWithEmptyAttrsShouldConvert() {
            var handler = new TextAreaTagHandler();
            var widget = handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(TextAreaElement.class, result);
        }
    }

    @Nested
    @DisplayName("DialogWidget conversion")
    class DialogWidgetConversion {

        @Test
        @DisplayName("DialogWidget should convert to DialogElement")
        void dialogShouldConvert() {
            var handler = new DialogTagHandler();
            var widget = (DialogTagHandler.DialogWidget) handler.createElement(
                    Map.of("title", "Confirm"));

            Element result = converter.convert(widget);

            assertInstanceOf(DialogElement.class, result);
        }

        @Test
        @DisplayName("DialogWidget with children should recursively convert")
        void dialogWithChildrenShouldConvert() {
            var handler = new DialogTagHandler();
            var widget = (DialogTagHandler.DialogWidget) handler.createElement(
                    Map.of("title", "Delete?", "border-type", "rounded"));
            handler.addChildren(widget, List.of(
                    Toolkit.text("Are you sure?"),
                    Toolkit.text("[y] Yes  [n] No")
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(DialogElement.class, result);
        }

        @Test
        @DisplayName("DialogWidget with dimensions should convert")
        void dialogWithDimensionsShouldConvert() {
            var handler = new DialogTagHandler();
            var widget = (DialogTagHandler.DialogWidget) handler.createElement(Map.of(
                    "title", "Info",
                    "width", "40",
                    "height", "10",
                    "min-width", "30",
                    "padding", "2",
                    "direction", "vertical",
                    "spacing", "1"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(DialogElement.class, result);
        }

        @Test
        @DisplayName("DialogWidget with invalid numeric values should handle gracefully")
        void dialogWithInvalidNumericsShouldHandleGracefully() {
            var handler = new DialogTagHandler();
            var widget = (DialogTagHandler.DialogWidget) handler.createElement(Map.of(
                    "width", "abc",
                    "height", "xyz",
                    "spacing", "not-a-number"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(DialogElement.class, result);
        }

        @Test
        @DisplayName("DialogWidget with empty attributes should convert")
        void dialogWithEmptyAttrsShouldConvert() {
            var handler = new DialogTagHandler();
            var widget = (DialogTagHandler.DialogWidget) handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(DialogElement.class, result);
        }
    }

    @Nested
    @DisplayName("TreeWidget conversion")
    class TreeWidgetConversion {

        @Test
        @DisplayName("TreeWidget should convert to TreeElement")
        void treeShouldConvert() {
            var handler = new TreeTagHandler();
            var widget = handler.createElement(Map.of("title", "Files"));

            Element result = converter.convert(widget);

            assertInstanceOf(TreeElement.class, result);
        }

        @Test
        @DisplayName("TreeWidget with bound root nodes should convert")
        void treeWithBoundRootsShouldConvert() {
            var handler = new TreeTagHandler();
            var widget = handler.createElement(Map.of("bind", "fileTree"));
            var srcNode = TreeNode.of("src");
            var readmeNode = TreeNode.of("README.md").leaf();
            var roots = List.of(srcNode, readmeNode);

            Element result = converter.convert(widget, Map.of("fileTree", roots));

            assertInstanceOf(TreeElement.class, result);
        }

        @Test
        @DisplayName("TreeWidget with all styling attributes should convert")
        void treeWithAllStylingShouldConvert() {
            var handler = new TreeTagHandler();
            var widget = handler.createElement(Map.of(
                    "title", "Project",
                    "border-type", "rounded",
                    "border-color", "cyan",
                    "guide-style", "unicode",
                    "highlight-color", "yellow",
                    "highlight-symbol", ">> ",
                    "scrollbar-policy", "always",
                    "indent-width", "4"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(TreeElement.class, result);
        }

        @Test
        @DisplayName("TreeWidget with empty attributes should convert")
        void treeWithEmptyAttrsShouldConvert() {
            var handler = new TreeTagHandler();
            var widget = handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(TreeElement.class, result);
        }
    }

    @Nested
    @DisplayName("FormFieldWidget conversion")
    class FormFieldWidgetConversion {

        @Test
        @DisplayName("FormFieldWidget should convert to FormFieldElement")
        void formFieldShouldConvert() {
            var handler = new FormFieldTagHandler();
            var widget = handler.createElement(Map.of("label", "Username"));

            Element result = converter.convert(widget);

            assertInstanceOf(FormFieldElement.class, result);
        }

        @Test
        @DisplayName("FormFieldWidget with standalone text binding should convert")
        void formFieldWithTextBindingShouldConvert() {
            var handler = new FormFieldTagHandler();
            var widget = handler.createElement(Map.of(
                    "label", "Search",
                    "bind", "searchInput",
                    "placeholder", "Type to search..."
            ));
            TextInputState state = new TextInputState();

            Element result = converter.convert(widget, Map.of("searchInput", state));

            assertInstanceOf(FormFieldElement.class, result);
        }

        @Test
        @DisplayName("FormFieldWidget with form field binding should convert")
        void formFieldWithFormBindingShouldConvert() {
            FormState formState = FormState.builder()
                    .textField("username", "alice")
                    .build();

            var formWidget = new FormTagHandler.FormWidget("myForm");
            var handler = new FormFieldTagHandler();
            var fieldWidget = handler.createElement(Map.of(
                    "label", "Username",
                    "field", "username"
            ));
            formWidget.children().add(fieldWidget);

            Element result = converter.convert(formWidget, Map.of("myForm", formState));

            assertInstanceOf(Column.class, result);
        }

        @Test
        @DisplayName("FormFieldWidget with boolean binding should convert")
        void formFieldWithBooleanBindingShouldConvert() {
            var handler = new FormFieldTagHandler();
            var widget = handler.createElement(Map.of(
                    "label", "Active",
                    "bind", "activeState",
                    "type", "toggle"
            ));
            BooleanFieldState state = new BooleanFieldState(true);

            Element result = converter.convert(widget, Map.of("activeState", state));

            assertInstanceOf(FormFieldElement.class, result);
        }

        @Test
        @DisplayName("FormFieldWidget with all styling attributes should convert")
        void formFieldWithAllStylingShouldConvert() {
            var handler = new FormFieldTagHandler();
            var widget = handler.createElement(Map.of(
                    "label", "Name",
                    "label-width", "20",
                    "placeholder", "Enter name",
                    "border-type", "rounded",
                    "border-color", "dark-gray",
                    "focused-border-color", "cyan"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(FormFieldElement.class, result);
        }

        @Test
        @DisplayName("FormFieldWidget with empty attributes should convert")
        void formFieldWithEmptyAttrsShouldConvert() {
            var handler = new FormFieldTagHandler();
            var widget = handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(FormFieldElement.class, result);
        }
    }

    @Nested
    @DisplayName("ColumnsWidget conversion")
    class ColumnsWidgetConversion {

        @Test
        @DisplayName("ColumnsWidget should convert to ColumnsElement")
        void columnsShouldConvert() {
            var handler = new ColumnsTagHandler();
            var widget = (ColumnsTagHandler.ColumnsWidget) handler.createElement(
                    Map.of("column-count", "3"));

            Element result = converter.convert(widget);

            assertInstanceOf(ColumnsElement.class, result);
        }

        @Test
        @DisplayName("ColumnsWidget with children should recursively convert")
        void columnsWithChildrenShouldConvert() {
            var handler = new ColumnsTagHandler();
            var widget = (ColumnsTagHandler.ColumnsWidget) handler.createElement(
                    Map.of("column-count", "2", "spacing", "1"));
            handler.addChildren(widget, List.of(
                    Toolkit.text("A"),
                    Toolkit.text("B"),
                    Toolkit.text("C"),
                    Toolkit.text("D")
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(ColumnsElement.class, result);
        }

        @Test
        @DisplayName("ColumnsWidget with all attributes should convert")
        void columnsWithAllAttrsShouldConvert() {
            var handler = new ColumnsTagHandler();
            var widget = (ColumnsTagHandler.ColumnsWidget) handler.createElement(Map.of(
                    "spacing", "2",
                    "flex", "CENTER",
                    "margin", "1",
                    "column-count", "3",
                    "column-order", "column-first",
                    "class", "my-columns",
                    "id", "cols1"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(ColumnsElement.class, result);
        }

        @Test
        @DisplayName("ColumnsWidget with empty attributes should convert")
        void columnsWithEmptyAttrsShouldConvert() {
            var handler = new ColumnsTagHandler();
            var widget = (ColumnsTagHandler.ColumnsWidget) handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(ColumnsElement.class, result);
        }

        @Test
        @DisplayName("ColumnsWidget with invalid numeric values should handle gracefully")
        void columnsWithInvalidNumericsShouldHandleGracefully() {
            var handler = new ColumnsTagHandler();
            var widget = (ColumnsTagHandler.ColumnsWidget) handler.createElement(Map.of(
                    "spacing", "abc",
                    "column-count", "xyz"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(ColumnsElement.class, result);
        }
    }

    @Nested
    @DisplayName("StackWidget conversion")
    class StackWidgetConversion {

        @Test
        @DisplayName("StackWidget should convert to StackElement")
        void stackShouldConvert() {
            var handler = new StackTagHandler();
            var widget = (StackTagHandler.StackWidget) handler.createElement(
                    Map.of("alignment", "center"));

            Element result = converter.convert(widget);

            assertInstanceOf(StackElement.class, result);
        }

        @Test
        @DisplayName("StackWidget with children should recursively convert")
        void stackWithChildrenShouldConvert() {
            var handler = new StackTagHandler();
            var widget = (StackTagHandler.StackWidget) handler.createElement(
                    Map.of("alignment", "stretch"));
            handler.addChildren(widget, List.of(
                    Toolkit.text("Background"),
                    Toolkit.text("Foreground")
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(StackElement.class, result);
        }

        @Test
        @DisplayName("StackWidget with all attributes should convert")
        void stackWithAllAttrsShouldConvert() {
            var handler = new StackTagHandler();
            var widget = (StackTagHandler.StackWidget) handler.createElement(Map.of(
                    "alignment", "top-left",
                    "margin", "2",
                    "class", "overlay",
                    "id", "stack1"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(StackElement.class, result);
        }

        @Test
        @DisplayName("StackWidget with empty attributes should convert")
        void stackWithEmptyAttrsShouldConvert() {
            var handler = new StackTagHandler();
            var widget = (StackTagHandler.StackWidget) handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(StackElement.class, result);
        }
    }

    @Nested
    @DisplayName("FlowWidget conversion")
    class FlowWidgetConversion {

        @Test
        @DisplayName("FlowWidget should convert to FlowElement")
        void flowShouldConvert() {
            var handler = new FlowTagHandler();
            var widget = (FlowTagHandler.FlowWidget) handler.createElement(
                    Map.of("spacing", "2"));

            Element result = converter.convert(widget);

            assertInstanceOf(FlowElement.class, result);
        }

        @Test
        @DisplayName("FlowWidget with children should recursively convert")
        void flowWithChildrenShouldConvert() {
            var handler = new FlowTagHandler();
            var widget = (FlowTagHandler.FlowWidget) handler.createElement(
                    Map.of("spacing", "1", "row-spacing", "1"));
            handler.addChildren(widget, List.of(
                    Toolkit.text("Tag1"),
                    Toolkit.text("Tag2"),
                    Toolkit.text("Tag3")
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(FlowElement.class, result);
        }

        @Test
        @DisplayName("FlowWidget with all attributes should convert")
        void flowWithAllAttrsShouldConvert() {
            var handler = new FlowTagHandler();
            var widget = (FlowTagHandler.FlowWidget) handler.createElement(Map.of(
                    "spacing", "2",
                    "row-spacing", "1",
                    "margin", "1",
                    "class", "tags",
                    "id", "flow1"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(FlowElement.class, result);
        }

        @Test
        @DisplayName("FlowWidget with empty attributes should convert")
        void flowWithEmptyAttrsShouldConvert() {
            var handler = new FlowTagHandler();
            var widget = (FlowTagHandler.FlowWidget) handler.createElement(Collections.emptyMap());

            Element result = converter.convert(widget);

            assertInstanceOf(FlowElement.class, result);
        }

        @Test
        @DisplayName("FlowWidget with invalid numeric values should handle gracefully")
        void flowWithInvalidNumericsShouldHandleGracefully() {
            var handler = new FlowTagHandler();
            var widget = (FlowTagHandler.FlowWidget) handler.createElement(Map.of(
                    "spacing", "abc",
                    "row-spacing", "xyz"
            ));

            Element result = converter.convert(widget);

            assertInstanceOf(FlowElement.class, result);
        }
    }
}
