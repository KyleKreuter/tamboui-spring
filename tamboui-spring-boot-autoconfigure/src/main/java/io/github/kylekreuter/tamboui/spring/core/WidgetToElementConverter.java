package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.layout.Constraint;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.DockElement;
import dev.tamboui.toolkit.elements.GridElement;
import dev.tamboui.toolkit.elements.TextInputElement;
import dev.tamboui.widget.Widget;
import dev.tamboui.widgets.form.FormState;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.list.ListItem;

import io.github.kylekreuter.tamboui.spring.template.tags.DockTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.GridTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.InputTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ListTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TableTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Converts the intermediate widget tree produced by the {@link
 * io.github.kylekreuter.tamboui.spring.template.TemplateEngine TemplateEngine}
 * into a TamboUI {@link Element} tree that can be rendered by the toolkit.
 * <p>
 * Some {@link io.github.kylekreuter.tamboui.spring.template.TagHandler TagHandler}
 * implementations already produce toolkit Elements directly (Row, Column, Spacer).
 * Others produce intermediate wrapper objects (PanelWidget, DockWidget, etc.) that
 * need to be converted to their corresponding toolkit Elements.
 * <p>
 * When state bindings are provided via {@link #convert(Object, Map)}, the converter
 * connects {@code <t:input>} elements to their backing {@link TextInputState} from
 * the model. This enables interactive text input in forms and standalone inputs.
 */
public class WidgetToElementConverter {

    private static final Logger log = LoggerFactory.getLogger(WidgetToElementConverter.class);

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

        // Already an Element (Row, Column, Spacer, etc.)
        if (widget instanceof Element element) {
            return element;
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

        // Input wrapper → TextInputElement with state binding
        if (widget instanceof InputTagHandler.InputWidget inputWidget) {
            return convertInput(inputWidget, stateBindings, currentForm);
        }

        // Text wrapper with CSS classes → styled text element
        if (widget instanceof TextTagHandler.TextWidget textWidget) {
            return convertText(textWidget);
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
            log.info("DIAG convertPanel: title='{}', cssClass='{}', parsed={}, panel.cssClasses={}",
                     title, cssClass, java.util.Arrays.asList(classes), panel.cssClasses());
        }

        for (Object child : panelWidget.children()) {
            panel.add(doConvert(child, stateBindings, currentForm));
        }

        return panel;
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
            log.info("DIAG convertText: text='{}', cssClass='{}', parsed={}, textElement.cssClasses={}",
                     textWidget.text(), cssClass, java.util.Arrays.asList(classes), textElement.cssClasses());
        }
        return textElement;
    }

    private Element convertList(ListTagHandler.ListWidgetHolder listHolder) {
        List<String> itemStrings = new ArrayList<>();
        for (ListItem item : listHolder.items()) {
            itemStrings.add(item.content().rawContent());
        }
        var listElement = Toolkit.list(itemStrings);
        listElement.focusable();
        String cssClass = listHolder.cssClass();
        if (cssClass != null && !cssClass.isBlank()) {
            listElement.addClass(cssClass.trim().split("\\s+"));
        }
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

        // Add rows — each Row contains Cells
        for (var row : tableHolder.rows()) {
            // Convert table Row to string array for TableElement
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

        String placeholder = inputWidget.placeholder();
        if (placeholder != null) {
            element.placeholder(placeholder);
        }

        return element;
    }

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
}
