package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Constraint;
import dev.tamboui.style.Color;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import io.github.kylekreuter.tamboui.spring.template.ParentTagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TableTagHandler}.
 */
class TableTagHandlerTest {

    private TableTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TableTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'table'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("table");
    }

    @Test
    @DisplayName("implements ParentTagHandler")
    void implementsParentTagHandler() {
        assertThat(handler).isInstanceOf(ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates TableWidgetHolder with no attributes")
        void withNoAttributes() {
            Object result = handler.createElement(Map.of());

            assertThat(result).isInstanceOf(TableTagHandler.TableWidgetHolder.class);
        }

        @Test
        @DisplayName("creates TableWidgetHolder with id attribute")
        void withIdAttribute() {
            Object result = handler.createElement(Map.of("id", "myTable"));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) result;
            assertThat(holder.id()).isEqualTo("myTable");
        }

        @Test
        @DisplayName("creates TableWidgetHolder with class attribute")
        void withClassAttribute() {
            Object result = handler.createElement(Map.of("class", "styled-table"));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) result;
            assertThat(holder.cssClass()).isEqualTo("styled-table");
        }

        @Test
        @DisplayName("creates TableWidgetHolder with highlight-color attribute")
        void withHighlightColor() {
            Object result = handler.createElement(Map.of("highlight-color", "BLUE"));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) result;
            assertThat(holder.highlightColor()).isEqualTo("BLUE");
        }

        @Test
        @DisplayName("id, class, and highlightColor are null when not provided")
        void nullAttributes() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(Map.of());

            assertThat(holder.id()).isNull();
            assertThat(holder.cssClass()).isNull();
            assertThat(holder.highlightColor()).isNull();
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("addChildren")
    class AddChildren {

        @Test
        @DisplayName("adds ColumnDefinition children to holder")
        void addColumnDefinitions() {
            Object parent = handler.createElement(Map.of());
            ColTagHandler.ColumnDefinition col1 =
                    new ColTagHandler.ColumnDefinition("Name", Constraint.length(20));
            ColTagHandler.ColumnDefinition col2 =
                    new ColTagHandler.ColumnDefinition("Age", Constraint.length(10));

            handler.addChildren(parent, List.of(col1, col2));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) parent;
            assertThat(holder.columns()).containsExactly(col1, col2);
        }

        @Test
        @DisplayName("adds Row children to holder")
        void addRows() {
            Object parent = handler.createElement(Map.of());
            Row row1 = Row.from("Alice", "30");
            Row row2 = Row.from("Bob", "25");

            handler.addChildren(parent, List.of(row1, row2));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) parent;
            assertThat(holder.rows()).containsExactly(row1, row2);
        }

        @Test
        @DisplayName("separates column definitions and rows correctly")
        void separatesColumnsAndRows() {
            Object parent = handler.createElement(Map.of());
            ColTagHandler.ColumnDefinition col = new ColTagHandler.ColumnDefinition("Name", Constraint.fill());
            Row row = Row.from("Alice");

            handler.addChildren(parent, List.of(col, row));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) parent;
            assertThat(holder.columns()).hasSize(1);
            assertThat(holder.rows()).hasSize(1);
        }

        @Test
        @DisplayName("handles empty children list")
        void emptyChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of());

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) parent;
            assertThat(holder.columns()).isEmpty();
            assertThat(holder.rows()).isEmpty();
        }

        @Test
        @DisplayName("ignores non-TableWidgetHolder parent")
        void nonHolderParent() {
            // Should not throw when parent is not TableWidgetHolder
            handler.addChildren("not a holder", List.of(Row.from("data")));
        }

        @Test
        @DisplayName("ignores unsupported child types")
        void ignoresUnsupportedChildren() {
            Object parent = handler.createElement(Map.of());

            handler.addChildren(parent, List.of("string", 42));

            TableTagHandler.TableWidgetHolder holder = (TableTagHandler.TableWidgetHolder) parent;
            assertThat(holder.columns()).isEmpty();
            assertThat(holder.rows()).isEmpty();
        }
    }

    @Nested
    @DisplayName("TableWidgetHolder")
    class TableWidgetHolderTests {

        @Test
        @DisplayName("columns and rows start empty")
        void startsEmpty() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(Map.of());

            assertThat(holder.columns()).isEmpty();
            assertThat(holder.rows()).isEmpty();
        }

        @Test
        @DisplayName("build creates a Table with columns and rows")
        void buildCreatesTable() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(Map.of());

            ColTagHandler.ColumnDefinition col1 =
                    new ColTagHandler.ColumnDefinition("Name", Constraint.length(20));
            ColTagHandler.ColumnDefinition col2 =
                    new ColTagHandler.ColumnDefinition("Age", Constraint.length(10));

            handler.addChildren(holder, List.of(col1, col2, Row.from("Alice", "30")));

            Object widget = holder.build();

            assertThat(widget).isNotNull();
            assertThat(widget).isInstanceOf(Table.class);
            Table table = (Table) widget;
            assertThat(table.rows()).hasSize(1);
        }

        @Test
        @DisplayName("build with no columns creates empty Table")
        void buildEmptyCreatesTable() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(Map.of());

            Object widget = holder.build();

            assertThat(widget).isNotNull();
            assertThat(widget).isInstanceOf(Table.class);
        }

        @Test
        @DisplayName("build with highlight-color applies color")
        void buildWithHighlightColor() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(
                            Map.of("highlight-color", "blue"));

            // Should not throw - the color is applied during build
            Object widget = holder.build();
            assertThat(widget).isInstanceOf(Table.class);
        }

        @Test
        @DisplayName("build with invalid highlight-color ignores it gracefully")
        void buildWithInvalidHighlightColor() {
            TableTagHandler.TableWidgetHolder holder =
                    (TableTagHandler.TableWidgetHolder) handler.createElement(
                            Map.of("highlight-color", "invalid_color"));

            // Should not throw
            Object widget = holder.build();
            assertThat(widget).isInstanceOf(Table.class);
        }
    }

    @Nested
    @DisplayName("resolveColor")
    class ResolveColorTests {

        @Test
        @DisplayName("resolves named colors case-insensitively")
        void resolvesNamedColors() {
            assertThat(TableTagHandler.resolveColor("blue")).isEqualTo(Color.BLUE);
            assertThat(TableTagHandler.resolveColor("BLUE")).isEqualTo(Color.BLUE);
            assertThat(TableTagHandler.resolveColor("Blue")).isEqualTo(Color.BLUE);
            assertThat(TableTagHandler.resolveColor("red")).isEqualTo(Color.RED);
            assertThat(TableTagHandler.resolveColor("green")).isEqualTo(Color.GREEN);
        }

        @Test
        @DisplayName("resolves compound named colors")
        void resolvesCompoundColors() {
            assertThat(TableTagHandler.resolveColor("dark-gray")).isEqualTo(Color.DARK_GRAY);
            assertThat(TableTagHandler.resolveColor("dark_gray")).isEqualTo(Color.DARK_GRAY);
            assertThat(TableTagHandler.resolveColor("light-blue")).isEqualTo(Color.LIGHT_BLUE);
            assertThat(TableTagHandler.resolveColor("light_blue")).isEqualTo(Color.LIGHT_BLUE);
        }

        @Test
        @DisplayName("resolves hex colors")
        void resolvesHexColors() {
            Color color = TableTagHandler.resolveColor("#FF0000");
            assertThat(color).isNotNull();
            assertThat(color).isInstanceOf(Color.Rgb.class);
        }

        @Test
        @DisplayName("returns null for unknown color names")
        void returnsNullForUnknown() {
            assertThat(TableTagHandler.resolveColor("not_a_color")).isNull();
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertThat(TableTagHandler.resolveColor(null)).isNull();
        }

        @Test
        @DisplayName("returns null for blank input")
        void returnsNullForBlank() {
            assertThat(TableTagHandler.resolveColor("")).isNull();
            assertThat(TableTagHandler.resolveColor("   ")).isNull();
        }

        @Test
        @DisplayName("returns null for invalid hex color")
        void returnsNullForInvalidHex() {
            assertThat(TableTagHandler.resolveColor("#GGGGGG")).isNull();
            assertThat(TableTagHandler.resolveColor("#12")).isNull();
        }
    }
}
