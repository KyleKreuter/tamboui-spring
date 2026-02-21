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
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TemplateEngine}.
 * Tests XML parsing, SpEL evaluation, and widget tree construction.
 */
class TemplateEngineTest {

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
    // parse() tests
    // ========================================================================

    @Nested
    @DisplayName("parse()")
    class ParseTests {

        @Test
        @DisplayName("should parse a simple template with panel and text")
        void parseSimpleTemplate() {
            TemplateNode root = engine.parse("templates/simple.ttl");

            assertEquals("t:panel", root.getTagName());
            assertEquals("Hello", root.getAttributes().get("title"));
            assertEquals(1, root.getChildren().size());

            TemplateNode textNode = root.getChildren().get(0);
            assertEquals("t:text", textNode.getTagName());
            assertEquals("World", textNode.getAttributes().get("t:text"));
        }

        @Test
        @DisplayName("should parse attributes correctly")
        void parseAttributes() {
            TemplateNode root = engine.parse("templates/attributes.ttl");

            assertEquals("Styled", root.getAttributes().get("title"));
            assertEquals("ROUNDED", root.getAttributes().get("borderType"));
            assertEquals("border-rounded", root.getAttributes().get("class"));

            TemplateNode textNode = root.getChildren().get(0);
            assertEquals("text-green", textNode.getAttributes().get("class"));
        }

        @Test
        @DisplayName("should parse nested panel hierarchy")
        void parseNestedPanels() {
            TemplateNode root = engine.parse("templates/nested.ttl");

            assertEquals("t:panel", root.getTagName());
            assertEquals("Outer", root.getAttributes().get("title"));
            assertEquals(1, root.getChildren().size());

            TemplateNode innerPanel = root.getChildren().get(0);
            assertEquals("t:panel", innerPanel.getTagName());
            assertEquals("Inner", innerPanel.getAttributes().get("title"));
            assertEquals(1, innerPanel.getChildren().size());

            TemplateNode textNode = innerPanel.getChildren().get(0);
            assertEquals("t:text", textNode.getTagName());
            assertEquals("Deep", textNode.getAttributes().get("t:text"));
        }

        @Test
        @DisplayName("should parse text-only template")
        void parseTextOnly() {
            TemplateNode root = engine.parse("templates/text-only.ttl");

            assertEquals("t:text", root.getTagName());
            assertEquals("Just text", root.getAttributes().get("t:text"));
        }

        @Test
        @DisplayName("should parse SpEL expressions as raw strings")
        void parseSpelExpressions() {
            TemplateNode root = engine.parse("templates/spel.ttl");

            // SpEL should be kept as raw strings during parsing
            assertEquals("${['title']}", root.getAttributes().get("title"));

            TemplateNode textNode = root.getChildren().get(0);
            assertEquals("Status: ${['status']}", textNode.getAttributes().get("t:text"));
        }

        @Test
        @DisplayName("should throw TemplateParseException for missing template")
        void parseMissingTemplate() {
            TemplateParseException ex = assertThrows(TemplateParseException.class,
                    () -> engine.parse("templates/nonexistent.ttl"));

            assertTrue(ex.getMessage().contains("not found"));
            assertTrue(ex.getMessage().contains("nonexistent.ttl"));
        }
    }

    // ========================================================================
    // render() tests
    // ========================================================================

    @Nested
    @DisplayName("render()")
    class RenderTests {

        @Test
        @DisplayName("should render simple template to widget tree")
        void renderSimpleTemplate() {
            Object result = engine.render("simple", Map.of());

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);

            PanelWidget panel = (PanelWidget) result;
            assertNotNull(panel.block());
            assertEquals(1, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
        }

        @Test
        @DisplayName("should evaluate SpEL expressions in attributes")
        void renderWithSpel() {
            Object result = engine.render("spel", Map.of(
                    "title", "Dashboard",
                    "status", "Running"
            ));

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);

            PanelWidget panel = (PanelWidget) result;
            // The panel block is created with the resolved title
            assertNotNull(panel.block());
            // The text child should have the evaluated SpEL
            assertEquals(1, panel.children().size());
            assertInstanceOf(Paragraph.class, panel.children().get(0));
        }

        @Test
        @DisplayName("should render nested panels with children")
        void renderNestedPanels() {
            Object result = engine.render("nested", Map.of());

            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);

            PanelWidget outerPanel = (PanelWidget) result;
            assertEquals(1, outerPanel.children().size());
            assertInstanceOf(PanelWidget.class, outerPanel.children().get(0));

            PanelWidget innerPanel = (PanelWidget) outerPanel.children().get(0);
            assertEquals(1, innerPanel.children().size());
            assertInstanceOf(Paragraph.class, innerPanel.children().get(0));
        }

        @Test
        @DisplayName("should render text-only template")
        void renderTextOnly() {
            Object result = engine.render("text-only", Map.of());

            assertNotNull(result);
            assertInstanceOf(Paragraph.class, result);
        }

        @Test
        @DisplayName("should use cache on second render")
        void renderCachesTemplate() {
            engine.render("simple", Map.of());
            assertNotNull(cache.get("templates/simple.ttl"));

            // Second render uses cache
            Object result = engine.render("simple", Map.of());
            assertNotNull(result);
        }

        @Test
        @DisplayName("should throw TemplateParseException for missing template")
        void renderMissingTemplate() {
            assertThrows(TemplateParseException.class,
                    () -> engine.render("nonexistent", Map.of()));
        }
    }

    // ========================================================================
    // SpEL evaluation tests
    // ========================================================================

    @Nested
    @DisplayName("SpEL evaluation")
    class SpelTests {

        @Test
        @DisplayName("should resolve simple variable from model via map access")
        void resolveSimpleVariable() {
            StandardEvaluationContext context = new StandardEvaluationContext();
            Map<String, Object> model = Map.of("name", "TamboUI");
            context.setRootObject(model);

            String result = engine.evaluateSpel("Hello ${['name']}", context);
            assertEquals("Hello TamboUI", result);
        }

        @Test
        @DisplayName("should resolve multiple SpEL expressions")
        void resolveMultipleExpressions() {
            StandardEvaluationContext context = new StandardEvaluationContext();
            Map<String, Object> model = Map.of("first", "Hello", "second", "World");
            context.setRootObject(model);

            String result = engine.evaluateSpel("${['first']} ${['second']}", context);
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("should return original string without SpEL markers")
        void noSpelExpressions() {
            StandardEvaluationContext context = new StandardEvaluationContext();
            String result = engine.evaluateSpel("No expressions here", context);
            assertEquals("No expressions here", result);
        }

        @Test
        @DisplayName("should handle null input")
        void nullInput() {
            StandardEvaluationContext context = new StandardEvaluationContext();
            String result = engine.evaluateSpel(null, context);
            assertNull(result);
        }

        @Test
        @DisplayName("should throw TemplateRenderException for invalid SpEL")
        void invalidSpelExpression() {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(Map.of());

            assertThrows(TemplateRenderException.class,
                    () -> engine.evaluateSpel("${nonExistentMethod()}", context));
        }
    }

    // ========================================================================
    // TemplateNode tests
    // ========================================================================

    @Nested
    @DisplayName("TemplateNode")
    class TemplateNodeTests {

        @Test
        @DisplayName("should store tag name")
        void tagName() {
            TemplateNode node = new TemplateNode("t:panel");
            assertEquals("t:panel", node.getTagName());
        }

        @Test
        @DisplayName("should store and retrieve attributes")
        void attributes() {
            TemplateNode node = new TemplateNode("t:panel");
            node.setAttribute("title", "Test");
            node.setAttribute("class", "border-rounded");

            assertEquals("Test", node.getAttributes().get("title"));
            assertEquals("border-rounded", node.getAttributes().get("class"));
        }

        @Test
        @DisplayName("should add and retrieve children")
        void children() {
            TemplateNode parent = new TemplateNode("t:panel");
            TemplateNode child1 = new TemplateNode("t:text");
            TemplateNode child2 = new TemplateNode("t:text");

            parent.addChild(child1);
            parent.addChild(child2);

            assertEquals(2, parent.getChildren().size());
            assertSame(child1, parent.getChildren().get(0));
            assertSame(child2, parent.getChildren().get(1));
        }

        @Test
        @DisplayName("should store text content")
        void textContent() {
            TemplateNode node = new TemplateNode("t:text");
            assertNull(node.getTextContent());

            node.setTextContent("Hello World");
            assertEquals("Hello World", node.getTextContent());
        }
    }

    // ========================================================================
    // TemplateCache tests
    // ========================================================================

    @Nested
    @DisplayName("TemplateCache")
    class TemplateCacheTests {

        @Test
        @DisplayName("should return null for missing entry")
        void getMissing() {
            assertNull(cache.get("nonexistent"));
        }

        @Test
        @DisplayName("should store and retrieve entries")
        void putAndGet() {
            TemplateNode node = new TemplateNode("root");
            cache.put("test-key", node);
            assertSame(node, cache.get("test-key"));
        }

        @Test
        @DisplayName("should clear all entries")
        void clear() {
            cache.put("key1", new TemplateNode("a"));
            cache.put("key2", new TemplateNode("b"));

            cache.clear();

            assertNull(cache.get("key1"));
            assertNull(cache.get("key2"));
        }
    }

    // ========================================================================
    // Edge cases
    // ========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should return null for unknown tag with no handler")
        void unknownTag() {
            TemplateNode node = new TemplateNode("t:unknown");
            Object result = engine.renderNode(node, Map.of());
            assertNull(result);
        }

        @Test
        @DisplayName("should handle empty model")
        void emptyModel() {
            Object result = engine.render("simple", Map.of());
            assertNotNull(result);
        }

        @Test
        @DisplayName("should handle null model in renderNode")
        void nullModel() {
            TemplateNode node = new TemplateNode("t:text");
            node.setAttribute("t:text", "Static text");
            Object result = engine.renderNode(node, null);
            assertInstanceOf(Paragraph.class, result);
        }

        @Test
        @DisplayName("should render template with attributes on panel")
        void renderWithAttributes() {
            Object result = engine.render("attributes", Map.of());
            assertNotNull(result);
            assertInstanceOf(PanelWidget.class, result);
        }
    }
}
