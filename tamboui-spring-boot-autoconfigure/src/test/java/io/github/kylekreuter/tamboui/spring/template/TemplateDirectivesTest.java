package io.github.kylekreuter.tamboui.spring.template;

import io.github.kylekreuter.tamboui.spring.autoconfigure.TamboUiProperties;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler.PanelWidget;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;

import dev.tamboui.widgets.paragraph.Paragraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for template directives: {@code <t:if>}, {@code <t:each>}, and {@code <t:include>}.
 * These directives are handled directly in {@link TemplateEngine#renderNodes} before TagHandler lookup.
 */
class TemplateDirectivesTest {

    private TemplateEngine engine;
    private TamboUiProperties properties;
    private TemplateCache cache;

    @BeforeEach
    void setUp() {
        properties = new TamboUiProperties();
        cache = new TemplateCache();
        engine = new TemplateEngine(cache, properties, List.of(
                new PanelTagHandler(),
                new TextTagHandler()
        ));
    }

    // ========================================================================
    // t:if directive
    // ========================================================================

    @Nested
    @DisplayName("<t:if> directive")
    class IfDirectiveTests {

        @Test
        @DisplayName("should render children when condition is true")
        void renderWhenTrue() {
            Object result = engine.render("if-true", Map.of("visible", true));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(1, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
        }

        @Test
        @DisplayName("should skip children when condition is false")
        void skipWhenFalse() {
            Object result = engine.render("if-true", Map.of("visible", false));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(0, panel.children().size());
        }

        @Test
        @DisplayName("should render multiple children when condition is true")
        void renderMultipleChildren() {
            Object result = engine.render("if-multiple-children", Map.of("show", true));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(2, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
            assertInstanceOf(Paragraph.class, panel.children().get(1));
        }

        @Test
        @DisplayName("should skip multiple children when condition is false")
        void skipMultipleChildren() {
            Object result = engine.render("if-multiple-children", Map.of("show", false));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(0, panel.children().size());
        }

        @Test
        @DisplayName("should handle nested t:if with both conditions true")
        void nestedBothTrue() {
            Object result = engine.render("if-nested", Map.of("outer", true, "inner", true));

            assertNotNull(result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(1, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
        }

        @Test
        @DisplayName("should handle nested t:if with outer true, inner false")
        void nestedOuterTrueInnerFalse() {
            Object result = engine.render("if-nested", Map.of("outer", true, "inner", false));

            assertNotNull(result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(0, panel.children().size());
        }

        @Test
        @DisplayName("should handle nested t:if with outer false")
        void nestedOuterFalse() {
            Object result = engine.render("if-nested", Map.of("outer", false, "inner", true));

            assertNotNull(result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(0, panel.children().size());
        }

        @Test
        @DisplayName("should throw TemplateRenderException when test attribute is missing")
        void missingTestAttribute() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.addChild(new TemplateNode("t:text"));

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(ifNode, Map.of()));

            assertTrue(ex.getMessage().contains("test"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException when test attribute is blank")
        void blankTestAttribute() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "   ");

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(ifNode, Map.of()));

            assertTrue(ex.getMessage().contains("test"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException for invalid SpEL in test")
        void invalidSpelInTest() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${nonExistentMethod()}");

            assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(ifNode, Map.of()));
        }

        @Test
        @DisplayName("should treat null as false in boolean evaluation")
        void nullIsFalse() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#nullVar}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Hello");
            ifNode.addChild(child);

            Map<String, Object> model = new LinkedHashMap<>();
            model.put("nullVar", null);

            Object result = engine.renderNode(ifNode, model);
            // t:if with false => no widgets => renderNode returns null
            assertNull(result);
        }

        @Test
        @DisplayName("should treat non-zero number as true")
        void nonZeroNumberIsTrue() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#count}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Exists");
            ifNode.addChild(child);

            Object result = engine.renderNode(ifNode, Map.of("count", 42));
            assertNotNull(result);
            assertInstanceOf(Paragraph.class, result);
        }

        @Test
        @DisplayName("should treat zero as false")
        void zeroIsFalse() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#count}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Exists");
            ifNode.addChild(child);

            Object result = engine.renderNode(ifNode, Map.of("count", 0));
            assertNull(result);
        }

        @Test
        @DisplayName("should treat non-empty string as true")
        void nonEmptyStringIsTrue() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#val}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Present");
            ifNode.addChild(child);

            Object result = engine.renderNode(ifNode, Map.of("val", "hello"));
            assertNotNull(result);
        }

        @Test
        @DisplayName("should treat empty string as false")
        void emptyStringIsFalse() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#val}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Present");
            ifNode.addChild(child);

            Object result = engine.renderNode(ifNode, Map.of("val", ""));
            assertNull(result);
        }

        @Test
        @DisplayName("should evaluate SpEL comparison expressions")
        void spelComparisonExpression() {
            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#count > 5}");
            TemplateNode child = new TemplateNode("t:text");
            child.setAttribute("t:text", "Many");
            ifNode.addChild(child);

            // count = 10 > 5 => true
            Object result = engine.renderNode(ifNode, Map.of("count", 10));
            assertNotNull(result);

            // count = 3 > 5 => false
            Object resultFalse = engine.renderNode(ifNode, Map.of("count", 3));
            assertNull(resultFalse);
        }

        @Test
        @DisplayName("should work with t:if using map-access SpEL syntax")
        void mapAccessSyntax() {
            Object result = engine.render("if-true", Map.of("visible", true));
            assertNotNull(result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(1, panel.children().size());
        }
    }

    // ========================================================================
    // t:each directive
    // ========================================================================

    @Nested
    @DisplayName("<t:each> directive")
    class EachDirectiveTests {

        @Test
        @DisplayName("should render children for each item in list")
        void renderForEachItem() {
            Object result = engine.render("each-simple",
                    Map.of("items", List.of("Alpha", "Beta", "Gamma")));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(3, panel.children().size());
            for (Object child : panel.children()) {
                assertInstanceOf(Paragraph.class, child);
            }
        }

        @Test
        @DisplayName("should render nothing for empty collection")
        void emptyCollection() {
            Object result = engine.render("each-simple",
                    Map.of("items", List.of()));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(0, panel.children().size());
        }

        @Test
        @DisplayName("should provide index variable when specified")
        void indexVariable() {
            Object result = engine.render("each-with-index",
                    Map.of("names", List.of("Alice", "Bob")));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(2, panel.children().size());
        }

        @Test
        @DisplayName("should iterate over arrays")
        void iterateOverArray() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Array");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#arr}");
            each.setAttribute("var", "item");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            each.addChild(text);
            panel.addChild(each);

            Object result = engine.renderNode(panel, Map.of("arr", new String[]{"X", "Y", "Z"}));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget pw = (PanelWidget) result;
            assertEquals(3, pw.children().size());
        }

        @Test
        @DisplayName("should iterate over primitive int arrays")
        void iterateOverPrimitiveArray() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "IntArray");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#nums}");
            each.setAttribute("var", "n");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#n}");
            each.addChild(text);
            panel.addChild(each);

            Object result = engine.renderNode(panel, Map.of("nums", new int[]{1, 2, 3}));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget pw = (PanelWidget) result;
            assertEquals(3, pw.children().size());
        }

        @Test
        @DisplayName("should handle nested t:each with different var names")
        void nestedEach() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Nested");

            TemplateNode outerEach = new TemplateNode("t:each");
            outerEach.setAttribute("items", "${#groups}");
            outerEach.setAttribute("var", "group");

            TemplateNode innerEach = new TemplateNode("t:each");
            innerEach.setAttribute("items", "${#group}");
            innerEach.setAttribute("var", "item");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            innerEach.addChild(text);
            outerEach.addChild(innerEach);
            panel.addChild(outerEach);

            List<List<String>> groups = List.of(
                    List.of("A", "B"),
                    List.of("C", "D", "E")
            );

            Object result = engine.renderNode(panel, Map.of("groups", groups));

            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            // 2 + 3 = 5 items total
            assertEquals(5, pw.children().size());
        }

        @Test
        @DisplayName("should throw TemplateRenderException when items attribute is missing")
        void missingItemsAttribute() {
            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("var", "item");

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(each, Map.of()));

            assertTrue(ex.getMessage().contains("items"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException when var attribute is missing")
        void missingVarAttribute() {
            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#list}");

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(each, Map.of("list", List.of())));

            assertTrue(ex.getMessage().contains("var"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException for non-iterable items")
        void nonIterableItems() {
            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#val}");
            each.setAttribute("var", "item");

            assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(each, Map.of("val", 42)));
        }

        @Test
        @DisplayName("should treat null items as empty collection")
        void nullItemsEmpty() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Null");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#items}");
            each.setAttribute("var", "item");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            each.addChild(text);
            panel.addChild(each);

            Map<String, Object> model = new LinkedHashMap<>();
            model.put("items", null);

            Object result = engine.renderNode(panel, model);
            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            assertEquals(0, pw.children().size());
        }

        @Test
        @DisplayName("should render multiple children per iteration")
        void multipleChildrenPerIteration() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Multi");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#items}");
            each.setAttribute("var", "item");

            TemplateNode text1 = new TemplateNode("t:text");
            text1.setAttribute("t:text", "Label:");
            TemplateNode text2 = new TemplateNode("t:text");
            text2.setAttribute("t:text", "${#item}");
            each.addChild(text1);
            each.addChild(text2);
            panel.addChild(each);

            Object result = engine.renderNode(panel, Map.of("items", List.of("A", "B")));

            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            // 2 items * 2 children each = 4 widgets
            assertEquals(4, pw.children().size());
        }
    }

    // ========================================================================
    // t:include directive
    // ========================================================================

    @Nested
    @DisplayName("<t:include> directive")
    class IncludeDirectiveTests {

        @Test
        @DisplayName("should include and render another template in current context")
        void includeTemplate() {
            Object result = engine.render("include-parent", Map.of("message", "Hello World"));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(1, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
        }

        @Test
        @DisplayName("should detect recursive includes and throw error")
        void recursiveIncludeDetection() {
            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.render("include-recursive-a", Map.of()));

            assertTrue(ex.getMessage().contains("Recursive"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException when template attribute is missing")
        void missingTemplateAttribute() {
            TemplateNode include = new TemplateNode("t:include");

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(include, Map.of()));

            assertTrue(ex.getMessage().contains("template"));
        }

        @Test
        @DisplayName("should throw TemplateRenderException when template attribute is blank")
        void blankTemplateAttribute() {
            TemplateNode include = new TemplateNode("t:include");
            include.setAttribute("template", "   ");

            TemplateRenderException ex = assertThrows(TemplateRenderException.class,
                    () -> engine.renderNode(include, Map.of()));

            assertTrue(ex.getMessage().contains("template"));
        }

        @Test
        @DisplayName("should throw TemplateParseException for non-existent included template")
        void nonExistentInclude() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Test");
            TemplateNode include = new TemplateNode("t:include");
            include.setAttribute("template", "does-not-exist");
            panel.addChild(include);

            assertThrows(TemplateParseException.class,
                    () -> engine.renderNode(panel, Map.of()));
        }

        @Test
        @DisplayName("should pass model context to included template")
        void includeSharesModelContext() {
            // The include-parent template includes include-fragment
            // which uses ${['message']} from the model
            Object result = engine.render("include-parent", Map.of("message", "TestValue"));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
            PanelWidget panel = (PanelWidget) result;
            assertEquals(1, panel.children().size());
        }

        @Test
        @DisplayName("should include programmatically created template node")
        void includeViaNode() {
            // Pre-populate the cache with a fragment
            TemplateNode fragment = new TemplateNode("t:text");
            fragment.setAttribute("t:text", "Cached Fragment");
            cache.put("templates/cached-fragment.ttl", fragment);

            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Test");
            TemplateNode include = new TemplateNode("t:include");
            include.setAttribute("template", "cached-fragment");
            panel.addChild(include);

            Object result = engine.renderNode(panel, Map.of());

            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            assertEquals(1, pw.children().size());
            assertInstanceOf(Paragraph.class, pw.children().get(0));
        }
    }

    // ========================================================================
    // Combined directive tests
    // ========================================================================

    @Nested
    @DisplayName("Combined directives")
    class CombinedDirectiveTests {

        @Test
        @DisplayName("should combine t:if inside t:each")
        void ifInsideEach() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Combined");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#items}");
            each.setAttribute("var", "item");
            each.setAttribute("index", "i");

            TemplateNode ifNode = new TemplateNode("t:if");
            // Only render items at even indices
            ifNode.setAttribute("test", "${#i % 2 == 0}");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            ifNode.addChild(text);
            each.addChild(ifNode);
            panel.addChild(each);

            Object result = engine.renderNode(panel, Map.of("items", List.of("A", "B", "C", "D")));

            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            // Indices 0 and 2 are even => 2 items rendered
            assertEquals(2, pw.children().size());
        }

        @Test
        @DisplayName("should combine t:each inside t:if")
        void eachInsideIf() {
            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Combined");

            TemplateNode ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#showList}");

            TemplateNode each = new TemplateNode("t:each");
            each.setAttribute("items", "${#items}");
            each.setAttribute("var", "item");

            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            each.addChild(text);
            ifNode.addChild(each);
            panel.addChild(ifNode);

            // showList = true: should render all items
            Object resultTrue = engine.renderNode(panel,
                    Map.of("showList", true, "items", List.of("X", "Y")));
            assertNotNull(resultTrue);
            PanelWidget pwTrue = (PanelWidget) resultTrue;
            assertEquals(2, pwTrue.children().size());

            // showList = false: should render nothing
            panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Combined");
            ifNode = new TemplateNode("t:if");
            ifNode.setAttribute("test", "${#showList}");
            each = new TemplateNode("t:each");
            each.setAttribute("items", "${#items}");
            each.setAttribute("var", "item");
            text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            each.addChild(text);
            ifNode.addChild(each);
            panel.addChild(ifNode);

            Object resultFalse = engine.renderNode(panel,
                    Map.of("showList", false, "items", List.of("X", "Y")));
            assertNotNull(resultFalse);
            PanelWidget pwFalse = (PanelWidget) resultFalse;
            assertEquals(0, pwFalse.children().size());
        }

        @Test
        @DisplayName("should include template with t:each inside")
        void includeWithEach() {
            // Pre-populate cache with a fragment that uses t:each
            TemplateNode fragment = new TemplateNode("t:each");
            fragment.setAttribute("items", "${#items}");
            fragment.setAttribute("var", "item");
            TemplateNode text = new TemplateNode("t:text");
            text.setAttribute("t:text", "${#item}");
            fragment.addChild(text);
            cache.put("templates/each-fragment.ttl", fragment);

            TemplateNode panel = new TemplateNode("t:panel");
            panel.setAttribute("title", "Include+Each");
            TemplateNode include = new TemplateNode("t:include");
            include.setAttribute("template", "each-fragment");
            panel.addChild(include);

            Object result = engine.renderNode(panel, Map.of("items", List.of("A", "B", "C")));

            assertNotNull(result);
            PanelWidget pw = (PanelWidget) result;
            assertEquals(3, pw.children().size());
        }
    }

    // ========================================================================
    // Backward compatibility tests
    // ========================================================================

    @Nested
    @DisplayName("Backward compatibility")
    class BackwardCompatibilityTests {

        @Test
        @DisplayName("existing renderNode should still return single widget")
        void renderNodeBackwardCompat() {
            TemplateNode node = new TemplateNode("t:text");
            node.setAttribute("t:text", "Hello");

            Object result = engine.renderNode(node, Map.of());
            assertNotNull(result);
            assertInstanceOf(Paragraph.class, result);
        }

        @Test
        @DisplayName("existing render should still work for simple templates")
        void renderBackwardCompat() {
            Object result = engine.render("simple", Map.of());
            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
        }

        @Test
        @DisplayName("unknown tags should return null in renderNode")
        void unknownTagReturnsNull() {
            TemplateNode node = new TemplateNode("t:unknown");
            Object result = engine.renderNode(node, Map.of());
            assertNull(result);
        }
    }
}
