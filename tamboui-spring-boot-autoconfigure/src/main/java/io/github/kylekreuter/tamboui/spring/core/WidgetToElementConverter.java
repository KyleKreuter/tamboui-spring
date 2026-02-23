package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.ContentAlignment;
import dev.tamboui.layout.Direction;
import dev.tamboui.layout.Flex;
import dev.tamboui.layout.Margin;
import dev.tamboui.layout.columns.ColumnOrder;
import dev.tamboui.style.Color;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Column;
import dev.tamboui.toolkit.elements.ColumnsElement;
import dev.tamboui.toolkit.elements.DialogElement;
import dev.tamboui.toolkit.elements.DockElement;
import dev.tamboui.toolkit.elements.FlowElement;
import dev.tamboui.toolkit.elements.FormFieldElement;
import dev.tamboui.toolkit.elements.GridElement;
import dev.tamboui.toolkit.elements.Row;
import dev.tamboui.toolkit.elements.StackElement;
import dev.tamboui.toolkit.elements.TabsElement;
import dev.tamboui.toolkit.elements.TextAreaElement;
import dev.tamboui.toolkit.elements.TextInputElement;
import dev.tamboui.toolkit.elements.TreeElement;
import dev.tamboui.widget.Widget;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.common.ScrollBarPolicy;
import dev.tamboui.widgets.form.BooleanFieldState;
import dev.tamboui.widgets.form.FieldType;
import dev.tamboui.widgets.form.FormState;
import dev.tamboui.widgets.form.SelectFieldState;
import dev.tamboui.widgets.input.TextAreaState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.list.ListItem;
import dev.tamboui.widgets.select.SelectState;
import dev.tamboui.widgets.tabs.TabsState;
import dev.tamboui.widgets.tree.GuideStyle;
import dev.tamboui.widgets.tree.TreeNode;
import dev.tamboui.widgets.tree.TreeState;

import io.github.kylekreuter.tamboui.spring.template.tags.ColumnTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ColumnsTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.DialogTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.DockTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FlowTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormFieldTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.GridTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.InputTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ListTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.SelectTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.RowTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.StackTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TabsTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TableTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextAreaTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TreeTagHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Converts the intermediate widget tree produced by the {@link
 * io.github.kylekreuter.tamboui.spring.template.TemplateEngine TemplateEngine}
 * into a TamboUI {@link Element} tree that can be rendered by the toolkit.
 * <p>
 * All {@link io.github.kylekreuter.tamboui.spring.template.TagHandler TagHandler}
 * implementations produce intermediate wrapper objects that this converter transforms
 * into toolkit Elements. Only simple elements like Spacer may pass through directly.
 * <p>
 * When state bindings are provided via {@link #convert(Object, Map)}, the converter
 * connects {@code <t:input>} elements to their backing {@link TextInputState} and
 * {@code <t:select>} elements to their backing {@link SelectState} or
 * {@link SelectFieldState} from the model. This enables interactive text input
 * and select widgets in forms and standalone usage.
 */
public class WidgetToElementConverter {

    private static final Logger log = LoggerFactory.getLogger(WidgetToElementConverter.class);

    // Cache for stateful elements that must survive across render frames.
    // TreeElement holds an internal TreeState (selection, scroll) that is lost
    // when a new instance is created. By caching the element, we preserve
    // keyboard navigation state across frames.
    private final Map<String, Element> elementCache = new java.util.HashMap<>();

    /**
     * Converts a template engine output object to a toolkit Element without state bindings.
     * <p>
     * Input elements will not be connected to any state and cannot receive user input.
     * Use {@link #convert(Object, Map)} to enable interactive inputs.
     *
     * @param widget the object produced by the template engine (may be an Element
     *               or an intermediate wrapper)
     * @return the corresponding toolkit Element, or a text placeholder if the
     *         object type is not recognized
     */
    public Element convert(Object widget) {
        return doConvert(widget, Collections.emptyMap(), null);
    }

    /**
     * Converts a template engine output object to a toolkit Element with state bindings.
     * <p>
     * State bindings allow {@code <t:input>} elements to be connected to their backing
     * {@link TextInputState}:
     * <ul>
     *   <li>Form inputs ({@code field} attribute) are resolved from the parent
     *       {@link FormState} looked up via the form's {@code bind} key</li>
     *   <li>Standalone inputs ({@code bind} attribute) are resolved directly
     *       from the state bindings map as {@link TextInputState}</li>
     * </ul>
     *
     * @param widget        the object produced by the template engine
     * @param stateBindings the state bindings from the {@link TemplateModel}
     * @return the corresponding toolkit Element
     */
    public Element convert(Object widget, Map<String, Object> stateBindings) {
        return doConvert(widget, stateBindings != null ? stateBindings : Collections.emptyMap(), null);
    }

    /**
     * Internal recursive conversion with state context.
     *
     * @param widget        the widget to convert
     * @param stateBindings the state bindings from the model
     * @param currentForm   the enclosing FormState when inside a {@code <t:form>}, or null
     */
    private Element doConvert(Object widget, Map<String, Object> stateBindings, FormState currentForm) {
        if (widget == null) {
            return Toolkit.text("");
        }

        // Already an Element (Spacer, etc.)
        if (widget instanceof Element element) {
            return element;
        }

        // Row wrapper → Row element
        if (widget instanceof RowTagHandler.RowWidget rowWidget) {
            return convertRow(rowWidget, stateBindings, currentForm);
        }

        // Column wrapper → Column element
        if (widget instanceof ColumnTagHandler.ColumnWidget columnWidget) {
            return convertColumn(columnWidget, stateBindings, currentForm);
        }

        // Panel wrapper → Panel element
        if (widget instanceof PanelTagHandler.PanelWidget panelWidget) {
            return convertPanel(panelWidget, stateBindings, currentForm);
        }

        // Dock wrapper → DockElement
        if (widget instanceof DockTagHandler.DockWidget dockWidget) {
            return convertDock(dockWidget, stateBindings, currentForm);
        }

        // Grid wrapper → GridElement
        if (widget instanceof GridTagHandler.GridWidget gridWidget) {
            return convertGrid(gridWidget, stateBindings, currentForm);
        }

        // List wrapper → ListElement with item strings
        if (widget instanceof ListTagHandler.ListWidgetHolder listHolder) {
            return convertList(listHolder);
        }

        // Table wrapper → TableElement
        if (widget instanceof TableTagHandler.TableWidgetHolder tableHolder) {
            return convertTable(tableHolder);
        }

        // Form wrapper → Column with converted children, establishing form context
        if (widget instanceof FormTagHandler.FormWidget formWidget) {
            return convertForm(formWidget, stateBindings);
        }

        // Select wrapper → FormFieldElement with select state binding
        if (widget instanceof SelectTagHandler.SelectWidget selectWidget) {
            return convertSelect(selectWidget, stateBindings, currentForm);
        }

        // Input wrapper → TextInputElement with state binding
        if (widget instanceof InputTagHandler.InputWidget inputWidget) {
            return convertInput(inputWidget, stateBindings, currentForm);
        }

        // Text wrapper with CSS classes → styled text element
        if (widget instanceof TextTagHandler.TextWidget textWidget) {
            return convertText(textWidget);
        }

        // Tabs wrapper → TabsElement
        if (widget instanceof TabsTagHandler.TabsWidget tabsWidget) {
            return convertTabs(tabsWidget, stateBindings);
        }

        // TextArea wrapper → TextAreaElement
        if (widget instanceof TextAreaTagHandler.TextAreaWidget textAreaWidget) {
            return convertTextArea(textAreaWidget, stateBindings);
        }

        // Dialog wrapper → DialogElement (container)
        if (widget instanceof DialogTagHandler.DialogWidget dialogWidget) {
            return convertDialog(dialogWidget, stateBindings, currentForm);
        }

        // Tree wrapper → TreeElement
        if (widget instanceof TreeTagHandler.TreeWidget treeWidget) {
            return convertTree(treeWidget, stateBindings);
        }

        // FormField wrapper → FormFieldElement
        if (widget instanceof FormFieldTagHandler.FormFieldWidget formFieldWidget) {
            return convertFormField(formFieldWidget, stateBindings, currentForm);
        }

        // Columns wrapper → ColumnsElement (container)
        if (widget instanceof ColumnsTagHandler.ColumnsWidget columnsWidget) {
            return convertColumns(columnsWidget, stateBindings, currentForm);
        }

        // Stack wrapper → StackElement (container)
        if (widget instanceof StackTagHandler.StackWidget stackWidget) {
            return convertStack(stackWidget, stateBindings, currentForm);
        }

        // Flow wrapper → FlowElement (container)
        if (widget instanceof FlowTagHandler.FlowWidget flowWidget) {
            return convertFlow(flowWidget, stateBindings, currentForm);
        }

        // Low-level Widget (Paragraph, etc.) → wrap with GenericWidgetElement
        if (widget instanceof Widget w) {
            return Toolkit.widget(w);
        }

        // Fallback: render as text
        log.debug("Unknown widget type '{}', rendering as text", widget.getClass().getSimpleName());
        return Toolkit.text(widget.toString());
    }

    private Element convertPanel(PanelTagHandler.PanelWidget panelWidget,
                                 Map<String, Object> stateBindings, FormState currentForm) {
        String title = panelWidget.title();
        var panel = title != null ? Toolkit.panel(title) : Toolkit.panel();

        String cssClass = panelWidget.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            String[] classes = cssClass.trim().split("\\s+");
            panel.addClass(classes);
        }

        for (Object child : panelWidget.children()) {
            panel.add(doConvert(child, stateBindings, currentForm));
        }

        return panel;
    }

    private Element convertRow(RowTagHandler.RowWidget rowWidget,
                                Map<String, Object> stateBindings, FormState currentForm) {
        Row row = Toolkit.row();
        applyRowAttributes(row, rowWidget.spacing(), rowWidget.flex(),
                           rowWidget.margin(), rowWidget.id(), rowWidget.cssClass());
        for (Object child : rowWidget.children()) {
            row.add(doConvert(child, stateBindings, currentForm));
        }
        return row;
    }

    private Element convertColumn(ColumnTagHandler.ColumnWidget columnWidget,
                                   Map<String, Object> stateBindings, FormState currentForm) {
        Column column = Toolkit.column();
        applyColumnAttributes(column, columnWidget.spacing(), columnWidget.flex(),
                              columnWidget.margin(), columnWidget.id(), columnWidget.cssClass());
        for (Object child : columnWidget.children()) {
            column.add(doConvert(child, stateBindings, currentForm));
        }
        return column;
    }

    private void applyRowAttributes(Row row, String spacing, String flex,
                                     String margin, String id, String cssClass) {
        if (spacing != null) {
            try { row.spacing(Integer.parseInt(spacing.trim())); } catch (NumberFormatException ignored) {}
        }
        if (flex != null) {
            try { row.flex(Flex.valueOf(flex.trim().toUpperCase())); } catch (IllegalArgumentException ignored) {}
        }
        if (margin != null) {
            try { row.margin(Integer.parseInt(margin.trim())); } catch (NumberFormatException ignored) {}
        }
        if (id != null && !id.isBlank()) { row.id(id.trim()); }
        if (cssClass != null && !cssClass.isBlank()) { row.addClass(cssClass.trim().split("\\s+")); }
    }

    private void applyColumnAttributes(Column column, String spacing, String flex,
                                        String margin, String id, String cssClass) {
        if (spacing != null) {
            try { column.spacing(Integer.parseInt(spacing.trim())); } catch (NumberFormatException ignored) {}
        }
        if (flex != null) {
            try { column.flex(Flex.valueOf(flex.trim().toUpperCase())); } catch (IllegalArgumentException ignored) {}
        }
        if (margin != null) {
            try { column.margin(Integer.parseInt(margin.trim())); } catch (NumberFormatException ignored) {}
        }
        if (id != null && !id.isBlank()) { column.id(id.trim()); }
        if (cssClass != null && !cssClass.isBlank()) { column.addClass(cssClass.trim().split("\\s+")); }
    }

    private Element convertDock(DockTagHandler.DockWidget dockWidget,
                                Map<String, Object> stateBindings, FormState currentForm) {
        DockElement dock = Toolkit.dock();

        if (dockWidget.top() != null) {
            Constraint height = parseConstraint(dockWidget.topHeight());
            if (height != null) {
                dock.top(doConvert(dockWidget.top(), stateBindings, currentForm), height);
            } else {
                dock.top(doConvert(dockWidget.top(), stateBindings, currentForm));
            }
        }

        if (dockWidget.bottom() != null) {
            Constraint height = parseConstraint(dockWidget.bottomHeight());
            if (height != null) {
                dock.bottom(doConvert(dockWidget.bottom(), stateBindings, currentForm), height);
            } else {
                dock.bottom(doConvert(dockWidget.bottom(), stateBindings, currentForm));
            }
        }

        if (dockWidget.left() != null) {
            Constraint width = parseConstraint(dockWidget.leftWidth());
            if (width != null) {
                dock.left(doConvert(dockWidget.left(), stateBindings, currentForm), width);
            } else {
                dock.left(doConvert(dockWidget.left(), stateBindings, currentForm));
            }
        }

        if (dockWidget.right() != null) {
            Constraint width = parseConstraint(dockWidget.rightWidth());
            if (width != null) {
                dock.right(doConvert(dockWidget.right(), stateBindings, currentForm), width);
            } else {
                dock.right(doConvert(dockWidget.right(), stateBindings, currentForm));
            }
        }

        if (dockWidget.center() != null) {
            dock.center(doConvert(dockWidget.center(), stateBindings, currentForm));
        }

        return dock;
    }

    private Element convertGrid(GridTagHandler.GridWidget gridWidget,
                                Map<String, Object> stateBindings, FormState currentForm) {
        GridElement grid = Toolkit.grid();

        // Apply grid-columns/grid-rows as grid size
        String gridColumns = gridWidget.gridColumns();
        String gridRows = gridWidget.gridRows();
        if (gridColumns != null && gridRows != null) {
            try {
                grid.gridSize(Integer.parseInt(gridColumns), Integer.parseInt(gridRows));
            } catch (NumberFormatException ignored) {
                // Fall through to gridSize attribute
            }
        } else if (gridColumns != null) {
            try {
                grid.gridSize(Integer.parseInt(gridColumns));
            } catch (NumberFormatException ignored) {
                // Ignore
            }
        }

        // grid-size attribute overrides if present
        String gridSize = gridWidget.gridSize();
        if (gridSize != null) {
            String[] parts = gridSize.split("\\s+");
            try {
                if (parts.length == 2) {
                    grid.gridSize(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } else if (parts.length == 1) {
                    grid.gridSize(Integer.parseInt(parts[0]));
                }
            } catch (NumberFormatException ignored) {
                // Ignore
            }
        }

        // Apply gutter
        String gutter = gridWidget.gutter();
        if (gutter != null) {
            String[] parts = gutter.split("\\s+");
            try {
                if (parts.length == 2) {
                    grid.gutter(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } else if (parts.length == 1) {
                    grid.gutter(Integer.parseInt(parts[0]));
                }
            } catch (NumberFormatException ignored) {
                // Ignore
            }
        }

        // Add children
        for (Object child : gridWidget.children()) {
            grid.add(doConvert(child, stateBindings, currentForm));
        }

        return grid;
    }

    private Element convertText(TextTagHandler.TextWidget textWidget) {
        var textElement = Toolkit.text(textWidget.text());
        String cssClass = textWidget.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            String[] classes = cssClass.trim().split("\\s+");
            textElement.addClass(classes);
        }
        return textElement;
    }

    private Element convertList(ListTagHandler.ListWidgetHolder listHolder) {
        // Determine a stable cache key so the element (and its internal ListState)
        // survives across render frames, preserving selection and scroll position.
        // The id attribute provides a stable key; without it, we derive one from item content.
        String id = listHolder.id();
        String cacheKey;
        if (id != null) {
            cacheKey = "list-" + id;
        } else {
            // Derive stable key from item content so it doesn't change across frames
            StringBuilder sb = new StringBuilder("list-items-");
            for (ListItem item : listHolder.items()) {
                sb.append(item.content().rawContent()).append('\0');
            }
            cacheKey = sb.toString();
            log.debug("List has no id attribute; using content-derived cache key");
        }

        Element cached = elementCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<String> itemStrings = new ArrayList<>();
        for (ListItem item : listHolder.items()) {
            itemStrings.add(item.content().rawContent());
        }
        var listElement = Toolkit.list(itemStrings);
        listElement.id(cacheKey);
        listElement.focusable();
        listElement.rounded();
        listElement.focusedBorderColor(Color.CYAN);

        String highlightSymbol = listHolder.highlightSymbol();
        if (highlightSymbol != null) {
            listElement.highlightSymbol(highlightSymbol);
        }
        String cssClass = listHolder.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            listElement.addClass(cssClass.trim().split("\\s+"));
        }

        elementCache.put(cacheKey, listElement);
        return listElement;
    }

    private Element convertTable(TableTagHandler.TableWidgetHolder tableHolder) {
        var table = Toolkit.table();

        // Add header
        List<String> headers = new ArrayList<>();
        for (var col : tableHolder.columns()) {
            headers.add(col.header());
        }
        if (!headers.isEmpty()) {
            table.header(headers.toArray(new String[0]));
        }

        // Add column width constraints
        List<Constraint> widths = new ArrayList<>();
        for (var col : tableHolder.columns()) {
            widths.add(col.constraint());
        }
        if (!widths.isEmpty()) {
            table.widths(widths);
        }

        // Apply highlight color
        String highlightColor = tableHolder.highlightColor();
        if (highlightColor != null && !highlightColor.isBlank()) {
            Color color = TableTagHandler.resolveColor(highlightColor);
            if (color != null) {
                table.highlightColor(color);
            }
        }

        // Add rows — each Row contains Cells
        for (var row : tableHolder.rows()) {
            List<String> cells = new ArrayList<>();
            for (var cell : row.cells()) {
                cells.add(cell.content().toString());
            }
            table.row(cells.toArray(new String[0]));
        }

        return table;
    }

    /**
     * Converts a form widget, establishing the form context for child inputs.
     * <p>
     * Looks up the {@link FormState} from state bindings using the form's {@code bind}
     * attribute and passes it as context to child conversions.
     */
    private Element convertForm(FormTagHandler.FormWidget formWidget, Map<String, Object> stateBindings) {
        FormState formState = null;
        String bind = formWidget.bind();
        if (bind != null) {
            Object state = stateBindings.get(bind);
            if (state instanceof FormState fs) {
                formState = fs;
            } else if (state != null) {
                log.warn("State binding '{}' is not a FormState but {}", bind, state.getClass().getSimpleName());
            } else {
                log.warn("No state binding found for form bind='{}'", bind);
            }
        }

        var column = Toolkit.column();
        for (Object child : formWidget.children()) {
            column.add(doConvert(child, stateBindings, formState));
        }
        return column;
    }

    /**
     * Converts an input widget to a {@link TextInputElement}, connecting it to
     * the appropriate {@link TextInputState}.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>If the input has a {@code field} attribute and a parent form context exists,
     *       the {@link TextInputState} is retrieved from the {@link FormState}</li>
     *   <li>If the input has a {@code bind} attribute, the {@link TextInputState}
     *       is looked up directly from the state bindings</li>
     *   <li>Otherwise, a new empty {@link TextInputState} is created (input will
     *       be interactive but state is not persisted across frames)</li>
     * </ol>
     */
    private Element convertInput(InputTagHandler.InputWidget inputWidget,
                                 Map<String, Object> stateBindings, FormState currentForm) {
        TextInputState inputState = null;

        // Form field mode: get state from enclosing FormState
        String field = inputWidget.field();
        if (field != null && currentForm != null) {
            try {
                inputState = currentForm.textField(field);
            } catch (IllegalArgumentException e) {
                log.warn("No text field '{}' in FormState", field);
            }
        }

        // Standalone mode: get state directly from state bindings
        if (inputState == null) {
            String bind = inputWidget.bind();
            if (bind != null) {
                Object state = stateBindings.get(bind);
                if (state instanceof TextInputState tis) {
                    inputState = tis;
                } else if (state != null) {
                    log.warn("State binding '{}' is not a TextInputState but {}",
                             bind, state.getClass().getSimpleName());
                }
            }
        }

        TextInputElement element = inputState != null
                ? Toolkit.textInput(inputState)
                : Toolkit.textInput();

        // Assign stable ID so FocusManager can track focus across frames
        if (field != null) {
            element.id("input-" + field);
        } else if (inputWidget.bind() != null) {
            element.id("input-" + inputWidget.bind());
        }

        String placeholder = inputWidget.placeholder();
        if (placeholder != null) {
            element.placeholder(placeholder);
        }

        return element;
    }

    /**
     * Converts a select widget to a {@link dev.tamboui.toolkit.elements.FormFieldElement},
     * connecting it to the appropriate {@link SelectFieldState} or {@link SelectState}.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>If the select has a {@code field} attribute and a parent form context exists,
     *       the {@link SelectFieldState} is retrieved from the {@link FormState} and
     *       wrapped with {@link Toolkit#formField(String, SelectFieldState)}</li>
     *   <li>If the select has a {@code bind} attribute, the {@link SelectState}
     *       is looked up directly from the state bindings and converted to a
     *       {@link SelectFieldState}</li>
     *   <li>If the select has an {@code options} attribute, a new {@link SelectState}
     *       is created from the comma-separated values</li>
     *   <li>Otherwise, a text placeholder is returned</li>
     * </ol>
     */
    private Element convertSelect(SelectTagHandler.SelectWidget selectWidget,
                                  Map<String, Object> stateBindings, FormState currentForm) {
        // Form field mode: get SelectFieldState from enclosing FormState
        String field = selectWidget.field();
        if (field != null && currentForm != null) {
            try {
                SelectFieldState selectFieldState = currentForm.selectField(field);
                var element = Toolkit.formField(field, selectFieldState);
                element.id("select-" + field);
                return element;
            } catch (IllegalArgumentException e) {
                log.warn("No select field '{}' in FormState", field);
            }
        }

        // Standalone mode: get SelectState directly from state bindings
        String bind = selectWidget.bind();
        if (bind != null) {
            Object state = stateBindings.get(bind);
            if (state instanceof SelectFieldState sfs) {
                var element = Toolkit.formField("", sfs);
                element.id("select-" + bind);
                return element;
            } else if (state != null) {
                log.warn("State binding '{}' is not a SelectState but {}",
                         bind, state.getClass().getSimpleName());
            }
        }

        // Fallback: create SelectState from comma-separated options attribute
        String options = selectWidget.options();
        if (options != null && !options.isBlank()) {
            String[] optionValues = options.split(",");
            for (int i = 0; i < optionValues.length; i++) {
                optionValues[i] = optionValues[i].trim();
            }
            SelectFieldState selectFieldState = new SelectFieldState(
                    Arrays.asList(optionValues));
            var element = Toolkit.formField("", selectFieldState);
            if (bind != null) {
                element.id("select-" + bind);
            } else if (field != null) {
                element.id("select-" + field);
            }
            return element;
        }

        log.warn("Select widget has no field, bind, or options attribute — rendering placeholder");
        return Toolkit.text("<select>");
    }

    // ========== New widget conversion methods ==========

    /**
     * Converts a tabs widget to a {@link TabsElement}, connecting it to the
     * appropriate {@link TabsState} from state bindings.
     */
    private Element convertTabs(TabsTagHandler.TabsWidget tabsWidget,
                                Map<String, Object> stateBindings) {
        TabsElement tabs = new TabsElement();

        // Parse comma-separated titles
        String titlesStr = tabsWidget.titles();
        if (titlesStr != null && !titlesStr.isBlank()) {
            String[] titleArr = titlesStr.split(",");
            for (int i = 0; i < titleArr.length; i++) {
                titleArr[i] = titleArr[i].trim();
            }
            tabs.titles(titleArr);
        }

        // State binding
        String bind = tabsWidget.bind();
        if (bind != null) {
            Object state = stateBindings.get(bind);
            if (state instanceof TabsState ts) {
                tabs.state(ts);
            } else if (state != null) {
                log.warn("State binding '{}' is not a TabsState but {}", bind, state.getClass().getSimpleName());
            }
        }

        // Divider
        String divider = tabsWidget.divider();
        if (divider != null) {
            tabs.divider(divider);
        }

        // Highlight color
        Color highlightColor = parseColor(tabsWidget.highlightColor());
        if (highlightColor != null) {
            tabs.highlightColor(highlightColor);
        }

        // Padding
        String paddingLeft = tabsWidget.paddingLeft();
        String paddingRight = tabsWidget.paddingRight();
        if (paddingLeft != null || paddingRight != null) {
            tabs.padding(
                    paddingLeft != null ? paddingLeft : "",
                    paddingRight != null ? paddingRight : ""
            );
        }

        // Title
        String title = tabsWidget.title();
        if (title != null && !title.isBlank()) {
            tabs.title(title);
        }

        // Border type
        applyBorderType(tabsWidget.borderType(), type -> {
            if (type == BorderType.ROUNDED) tabs.rounded();
        });

        // Border color
        Color borderColor = parseColor(tabsWidget.borderColor());
        if (borderColor != null) {
            tabs.borderColor(borderColor);
        }

        return tabs;
    }

    /**
     * Converts a text area widget to a {@link TextAreaElement}, connecting it to
     * the appropriate {@link TextAreaState} from state bindings.
     */
    private Element convertTextArea(TextAreaTagHandler.TextAreaWidget textAreaWidget,
                                    Map<String, Object> stateBindings) {
        TextAreaElement textArea = new TextAreaElement();

        // State binding
        String bind = textAreaWidget.bind();
        if (bind != null) {
            Object state = stateBindings.get(bind);
            if (state instanceof TextAreaState tas) {
                textArea.state(tas);
            } else if (state != null) {
                log.warn("State binding '{}' is not a TextAreaState but {}", bind, state.getClass().getSimpleName());
            }
        }

        // Placeholder
        String placeholder = textAreaWidget.placeholder();
        if (placeholder != null) {
            textArea.placeholder(placeholder);
        }

        // Title
        String title = textAreaWidget.title();
        if (title != null && !title.isBlank()) {
            textArea.title(title);
        }

        // Border type
        applyBorderType(textAreaWidget.borderType(), type -> {
            if (type == BorderType.ROUNDED) textArea.rounded();
        });

        // Border color
        Color borderColor = parseColor(textAreaWidget.borderColor());
        if (borderColor != null) {
            textArea.borderColor(borderColor);
        }

        // Focused border color
        Color focusedBorderColor = parseColor(textAreaWidget.focusedBorderColor());
        if (focusedBorderColor != null) {
            textArea.focusedBorderColor(focusedBorderColor);
        }

        // Show line numbers
        if (textAreaWidget.showLineNumbers()) {
            textArea.showLineNumbers();
        }

        // Stable ID for focus tracking
        if (bind != null) {
            textArea.id("textarea-" + bind);
        }

        return textArea;
    }

    /**
     * Converts a dialog widget to a {@link DialogElement}, recursively
     * converting child widgets.
     */
    private Element convertDialog(DialogTagHandler.DialogWidget dialogWidget,
                                  Map<String, Object> stateBindings, FormState currentForm) {
        DialogElement dialog = new DialogElement();

        // Title
        String title = dialogWidget.title();
        if (title != null && !title.isBlank()) {
            dialog.title(title);
        }

        // Border type
        BorderType borderType = parseBorderType(dialogWidget.borderType());
        if (borderType != null) {
            dialog.borderType(borderType);
        }

        // Border color
        Color borderColor = parseColor(dialogWidget.borderColor());
        if (borderColor != null) {
            dialog.borderColor(borderColor);
        }

        // Width
        String width = dialogWidget.width();
        if (width != null) {
            try { dialog.width(Integer.parseInt(width.trim())); } catch (NumberFormatException ignored) {}
        }

        // Height
        String height = dialogWidget.height();
        if (height != null) {
            try { dialog.length(Integer.parseInt(height.trim())); } catch (NumberFormatException ignored) {}
        }

        // Min width
        String minWidth = dialogWidget.minWidth();
        if (minWidth != null) {
            try { dialog.minWidth(Integer.parseInt(minWidth.trim())); } catch (NumberFormatException ignored) {}
        }

        // Padding
        String padding = dialogWidget.padding();
        if (padding != null) {
            try { dialog.padding(Integer.parseInt(padding.trim())); } catch (NumberFormatException ignored) {}
        }

        // Direction
        String direction = dialogWidget.direction();
        if (direction != null) {
            try {
                String normalized = direction.trim().toUpperCase().replace("-", "_");
                dialog.direction(Direction.valueOf(normalized));
            } catch (IllegalArgumentException ignored) {}
        }

        // Flex
        String flex = dialogWidget.flex();
        if (flex != null) {
            try { dialog.flex(Flex.valueOf(flex.trim().toUpperCase())); } catch (IllegalArgumentException ignored) {}
        }

        // Spacing
        String spacing = dialogWidget.spacing();
        if (spacing != null) {
            try { dialog.spacing(Integer.parseInt(spacing.trim())); } catch (NumberFormatException ignored) {}
        }

        // Recursively convert children
        for (Object child : dialogWidget.children()) {
            dialog.add(doConvert(child, stateBindings, currentForm));
        }

        return dialog;
    }

    /**
     * Converts a tree widget to a {@link TreeElement}, connecting it to
     * tree roots or state from the model.
     */
    @SuppressWarnings("unchecked")
    private Element convertTree(TreeTagHandler.TreeWidget treeWidget,
                                Map<String, Object> stateBindings) {
        String bind = treeWidget.bind();

        // Return cached instance to preserve internal TreeState (selection, scroll)
        // across render frames. TreeElement holds a private final TreeState that
        // would be lost if a new instance were created each frame.
        if (bind != null) {
            String cacheKey = "tree-" + bind;
            Element cached = elementCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        TreeElement<Object> tree = new TreeElement<>();

        // State/roots binding
        if (bind != null) {
            Object state = stateBindings.get(bind);
            if (state instanceof TreeNode<?>[]) {
                for (TreeNode<?> root : (TreeNode<?>[]) state) {
                    tree.add((TreeNode<Object>) root);
                }
            } else if (state instanceof java.util.Collection<?> coll) {
                for (Object item : coll) {
                    if (item instanceof TreeNode<?>) {
                        tree.add((TreeNode<Object>) item);
                    }
                }
            } else if (state instanceof TreeNode<?> singleRoot) {
                tree.add((TreeNode<Object>) singleRoot);
            } else if (state != null) {
                log.warn("State binding '{}' is not TreeNode/Collection but {}", bind, state.getClass().getSimpleName());
            }
        }

        // Title
        String title = treeWidget.title();
        if (title != null && !title.isBlank()) {
            tree.title(title);
        }

        // Border type
        applyBorderType(treeWidget.borderType(), type -> {
            if (type == BorderType.ROUNDED) tree.rounded();
        });

        // Border color
        Color borderColor = parseColor(treeWidget.borderColor());
        if (borderColor != null) {
            tree.borderColor(borderColor);
        }

        // Guide style
        String guideStyle = treeWidget.guideStyle();
        if (guideStyle != null) {
            try {
                tree.guideStyle(GuideStyle.valueOf(guideStyle.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        // Highlight color
        Color highlightColor = parseColor(treeWidget.highlightColor());
        if (highlightColor != null) {
            tree.highlightColor(highlightColor);
        }

        // Highlight symbol
        String highlightSymbol = treeWidget.highlightSymbol();
        if (highlightSymbol != null) {
            tree.highlightSymbol(highlightSymbol);
        }

        // Scrollbar policy
        String scrollbarPolicy = treeWidget.scrollbarPolicy();
        if (scrollbarPolicy != null) {
            try {
                String normalized = scrollbarPolicy.trim().toUpperCase().replace("-", "_");
                tree.scrollbar(ScrollBarPolicy.valueOf(normalized));
            } catch (IllegalArgumentException ignored) {}
        }

        // Indent width
        String indentWidth = treeWidget.indentWidth();
        if (indentWidth != null) {
            try { tree.indentWidth(Integer.parseInt(indentWidth.trim())); } catch (NumberFormatException ignored) {}
        }

        // Make tree focusable so it participates in TAB navigation
        // and can receive keyboard events (up/down/left/right/enter)
        tree.focusable();
        if (bind != null) {
            tree.id("tree-" + bind);
            elementCache.put("tree-" + bind, tree);
        }

        return tree;
    }

    /**
     * Converts a form field widget to a {@link FormFieldElement}, resolving
     * the field state from the enclosing form or state bindings.
     */
    private Element convertFormField(FormFieldTagHandler.FormFieldWidget formFieldWidget,
                                     Map<String, Object> stateBindings, FormState currentForm) {
        String label = formFieldWidget.label();
        String field = formFieldWidget.field();
        String bind = formFieldWidget.bind();
        String type = formFieldWidget.type();

        // Determine field type
        FieldType fieldType = FieldType.TEXT;
        if (type != null) {
            try {
                fieldType = FieldType.valueOf(type.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        FormFieldElement formField;

        // Resolve state from form context or standalone binding
        if (field != null && currentForm != null) {
            // Form field mode
            switch (fieldType) {
                case CHECKBOX, TOGGLE -> {
                    try {
                        BooleanFieldState boolState = currentForm.booleanField(field);
                        formField = new FormFieldElement(label != null ? label : "", boolState, fieldType);
                    } catch (Exception e) {
                        log.warn("No boolean field '{}' in FormState", field);
                        formField = new FormFieldElement(label != null ? label : "");
                    }
                }
                case SELECT -> {
                    try {
                        SelectFieldState selectState = currentForm.selectField(field);
                        formField = new FormFieldElement(label != null ? label : "", selectState);
                    } catch (Exception e) {
                        log.warn("No select field '{}' in FormState", field);
                        formField = new FormFieldElement(label != null ? label : "");
                    }
                }
                default -> {
                    try {
                        TextInputState textState = currentForm.textField(field);
                        formField = new FormFieldElement(label != null ? label : "", textState);
                    } catch (Exception e) {
                        log.warn("No text field '{}' in FormState", field);
                        formField = new FormFieldElement(label != null ? label : "");
                    }
                }
            }
        } else if (bind != null) {
            // Standalone mode
            Object state = stateBindings.get(bind);
            if (state instanceof TextInputState tis) {
                formField = new FormFieldElement(label != null ? label : "", tis);
            } else if (state instanceof BooleanFieldState bfs) {
                formField = new FormFieldElement(label != null ? label : "", bfs, fieldType);
            } else if (state instanceof SelectFieldState sfs) {
                formField = new FormFieldElement(label != null ? label : "", sfs);
            } else if (state instanceof SelectState ss) {
                // Convert SelectState to SelectFieldState for FormFieldElement compatibility
                SelectFieldState sfs = new SelectFieldState(ss.options(), ss.selectedIndex());
                formField = new FormFieldElement(label != null ? label : "", sfs);
            } else {
                if (state != null) {
                    log.warn("State binding '{}' is not a recognized form field state but {}",
                             bind, state.getClass().getSimpleName());
                }
                formField = new FormFieldElement(label != null ? label : "");
            }
        } else {
            formField = new FormFieldElement(label != null ? label : "");
        }

        // Label width
        String labelWidth = formFieldWidget.labelWidth();
        if (labelWidth != null) {
            try { formField.labelWidth(Integer.parseInt(labelWidth.trim())); } catch (NumberFormatException ignored) {}
        }

        // Placeholder
        String placeholder = formFieldWidget.placeholder();
        if (placeholder != null) {
            formField.placeholder(placeholder);
        }

        // Border type
        String btStr = formFieldWidget.borderType();
        if (btStr != null && !btStr.isBlank()) {
            try {
                BorderType bt = BorderType.valueOf(btStr.trim().toUpperCase());
                if (bt == BorderType.ROUNDED) formField.rounded();
            } catch (IllegalArgumentException ignored) {}
        }

        // Border color
        Color borderColor = parseColor(formFieldWidget.borderColor());
        if (borderColor != null) {
            formField.borderColor(borderColor);
        }

        // Focused border color
        Color focusedBorderColor = parseColor(formFieldWidget.focusedBorderColor());
        if (focusedBorderColor != null) {
            formField.focusedBorderColor(focusedBorderColor);
        }

        // Stable ID for focus tracking
        if (field != null) {
            formField.id("formfield-" + field);
        } else if (bind != null) {
            formField.id("formfield-" + bind);
        }

        return formField;
    }

    /**
     * Converts a columns widget to a {@link ColumnsElement}, recursively
     * converting child widgets.
     */
    private Element convertColumns(ColumnsTagHandler.ColumnsWidget columnsWidget,
                                   Map<String, Object> stateBindings, FormState currentForm) {
        ColumnsElement columns = new ColumnsElement();

        // Spacing
        String spacing = columnsWidget.spacing();
        if (spacing != null) {
            try { columns.spacing(Integer.parseInt(spacing.trim())); } catch (NumberFormatException ignored) {}
        }

        // Flex
        String flex = columnsWidget.flex();
        if (flex != null) {
            try { columns.flex(Flex.valueOf(flex.trim().toUpperCase())); } catch (IllegalArgumentException ignored) {}
        }

        // Margin
        String margin = columnsWidget.margin();
        if (margin != null) {
            try { columns.margin(Integer.parseInt(margin.trim())); } catch (NumberFormatException ignored) {}
        }

        // Column count
        String columnCount = columnsWidget.columnCount();
        if (columnCount != null) {
            try { columns.columnCount(Integer.parseInt(columnCount.trim())); } catch (NumberFormatException ignored) {}
        }

        // Column order
        String columnOrder = columnsWidget.columnOrder();
        if (columnOrder != null) {
            try {
                String normalized = columnOrder.trim().toUpperCase().replace("-", "_");
                columns.order(ColumnOrder.valueOf(normalized));
            } catch (IllegalArgumentException ignored) {}
        }

        // CSS class
        String cssClass = columnsWidget.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            columns.addClass(cssClass.trim().split("\\s+"));
        }

        // ID
        String id = columnsWidget.id();
        if (id != null && !id.isBlank()) {
            columns.id(id.trim());
        }

        // Recursively convert children
        for (Object child : columnsWidget.children()) {
            columns.add(doConvert(child, stateBindings, currentForm));
        }

        return columns;
    }

    /**
     * Converts a stack widget to a {@link StackElement}, recursively
     * converting child widgets.
     */
    private Element convertStack(StackTagHandler.StackWidget stackWidget,
                                 Map<String, Object> stateBindings, FormState currentForm) {
        StackElement stack = new StackElement();

        // Alignment
        String alignment = stackWidget.alignment();
        if (alignment != null) {
            try {
                String normalized = alignment.trim().toUpperCase().replace("-", "_");
                stack.alignment(ContentAlignment.valueOf(normalized));
            } catch (IllegalArgumentException ignored) {}
        }

        // Margin
        String margin = stackWidget.margin();
        if (margin != null) {
            try { stack.margin(Integer.parseInt(margin.trim())); } catch (NumberFormatException ignored) {}
        }

        // CSS class
        String cssClass = stackWidget.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            stack.addClass(cssClass.trim().split("\\s+"));
        }

        // ID
        String id = stackWidget.id();
        if (id != null && !id.isBlank()) {
            stack.id(id.trim());
        }

        // Recursively convert children
        for (Object child : stackWidget.children()) {
            stack.add(doConvert(child, stateBindings, currentForm));
        }

        return stack;
    }

    /**
     * Converts a flow widget to a {@link FlowElement}, recursively
     * converting child widgets.
     */
    private Element convertFlow(FlowTagHandler.FlowWidget flowWidget,
                                Map<String, Object> stateBindings, FormState currentForm) {
        FlowElement flow = new FlowElement();

        // Spacing
        String spacing = flowWidget.spacing();
        if (spacing != null) {
            try { flow.spacing(Integer.parseInt(spacing.trim())); } catch (NumberFormatException ignored) {}
        }

        // Row spacing
        String rowSpacing = flowWidget.rowSpacing();
        if (rowSpacing != null) {
            try { flow.rowSpacing(Integer.parseInt(rowSpacing.trim())); } catch (NumberFormatException ignored) {}
        }

        // Margin
        String margin = flowWidget.margin();
        if (margin != null) {
            try { flow.margin(Integer.parseInt(margin.trim())); } catch (NumberFormatException ignored) {}
        }

        // CSS class
        String cssClass = flowWidget.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            flow.addClass(cssClass.trim().split("\\s+"));
        }

        // ID
        String id = flowWidget.id();
        if (id != null && !id.isBlank()) {
            flow.id(id.trim());
        }

        // Recursively convert children
        for (Object child : flowWidget.children()) {
            flow.add(doConvert(child, stateBindings, currentForm));
        }

        return flow;
    }

    // ========== Helper methods ==========

    /**
     * Parses a string constraint value (e.g. "3", "20") to a length constraint.
     *
     * @param value the string value
     * @return a length constraint, or {@code null} if the value is null or invalid
     */
    private Constraint parseConstraint(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Constraint.length(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a named color string (e.g. "RED", "cyan", "LIGHT_BLUE") to a TamboUI {@link Color}.
     * Returns {@code null} if the string is null, blank, or not recognized.
     */
    private Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.isBlank()) {
            return null;
        }
        String normalized = colorStr.trim().toUpperCase().replace("-", "_");
        switch (normalized) {
            case "BLACK":         return Color.BLACK;
            case "RED":           return Color.RED;
            case "GREEN":         return Color.GREEN;
            case "YELLOW":        return Color.YELLOW;
            case "BLUE":          return Color.BLUE;
            case "MAGENTA":       return Color.MAGENTA;
            case "CYAN":          return Color.CYAN;
            case "WHITE":         return Color.WHITE;
            case "GRAY":          return Color.GRAY;
            case "DARK_GRAY":     return Color.DARK_GRAY;
            case "LIGHT_RED":     return Color.LIGHT_RED;
            case "LIGHT_GREEN":   return Color.LIGHT_GREEN;
            case "LIGHT_YELLOW":  return Color.LIGHT_YELLOW;
            case "LIGHT_BLUE":    return Color.LIGHT_BLUE;
            case "LIGHT_MAGENTA": return Color.LIGHT_MAGENTA;
            case "LIGHT_CYAN":    return Color.LIGHT_CYAN;
            case "BRIGHT_WHITE":  return Color.BRIGHT_WHITE;
            default:
                log.debug("Unknown color '{}', ignoring", colorStr);
                return null;
        }
    }

    /**
     * Parses a border type string (e.g. "rounded", "PLAIN", "double") to a {@link BorderType}.
     * Returns {@code null} if the string is null, blank, or not recognized.
     */
    private BorderType parseBorderType(String borderTypeStr) {
        if (borderTypeStr == null || borderTypeStr.isBlank()) {
            return null;
        }
        try {
            return BorderType.valueOf(borderTypeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.debug("Unknown border type '{}', ignoring", borderTypeStr);
            return null;
        }
    }

    /**
     * Applies a parsed border type using the given consumer. This is a convenience
     * method for elements that only support {@code rounded()} instead of {@code borderType()}.
     */
    private void applyBorderType(String borderTypeStr, java.util.function.Consumer<BorderType> applier) {
        BorderType type = parseBorderType(borderTypeStr);
        if (type != null) {
            applier.accept(type);
        }
    }
}
