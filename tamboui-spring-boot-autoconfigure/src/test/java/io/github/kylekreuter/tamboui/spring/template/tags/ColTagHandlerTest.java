package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Constraint;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ColTagHandler}.
 */
class ColTagHandlerTest {

    private ColTagHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ColTagHandler();
    }

    @Test
    @DisplayName("getTagName returns 'col'")
    void getTagName() {
        assertThat(handler.getTagName()).isEqualTo("col");
    }

    @Test
    @DisplayName("implements TagHandler but not ParentTagHandler")
    void implementsTagHandler() {
        assertThat(handler).isInstanceOf(TagHandler.class);
        assertThat(handler).isNotInstanceOf(io.github.kylekreuter.tamboui.spring.template.ParentTagHandler.class);
    }

    @Nested
    @DisplayName("createElement")
    class CreateElement {

        @Test
        @DisplayName("creates ColumnDefinition with header attribute")
        void withHeaderAttribute() {
            Object result = handler.createElement(Map.of("header", "Name"));

            assertThat(result).isInstanceOf(ColTagHandler.ColumnDefinition.class);
            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.header()).isEqualTo("Name");
        }

        @Test
        @DisplayName("creates ColumnDefinition with header and width attributes")
        void withHeaderAndWidth() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("header", "Age");
            attrs.put("width", "10");

            Object result = handler.createElement(attrs);

            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.header()).isEqualTo("Age");
            assertThat(colDef.constraint()).isEqualTo(Constraint.length(10));
        }

        @Test
        @DisplayName("defaults to fill constraint when width is not specified")
        void defaultsToFillWithoutWidth() {
            Object result = handler.createElement(Map.of("header", "Test"));

            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.constraint()).isEqualTo(Constraint.fill());
        }

        @Test
        @DisplayName("defaults to fill constraint when width is not a number")
        void defaultsToFillWithInvalidWidth() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("header", "Test");
            attrs.put("width", "invalid");

            Object result = handler.createElement(attrs);

            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.constraint()).isEqualTo(Constraint.fill());
        }

        @Test
        @DisplayName("header defaults to empty string when not provided")
        void headerDefaultsToEmpty() {
            Object result = handler.createElement(Map.of());

            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.header()).isEmpty();
        }

        @Test
        @DisplayName("trims whitespace from width attribute")
        void trimsWidthWhitespace() {
            Map<String, String> attrs = new HashMap<>();
            attrs.put("header", "Test");
            attrs.put("width", "  15  ");

            Object result = handler.createElement(attrs);

            ColTagHandler.ColumnDefinition colDef = (ColTagHandler.ColumnDefinition) result;
            assertThat(colDef.constraint()).isEqualTo(Constraint.length(15));
        }

        @Test
        @DisplayName("returns non-null element always")
        void neverReturnsNull() {
            Object result = handler.createElement(new HashMap<>());
            assertThat(result).isNotNull();
        }
    }
}
