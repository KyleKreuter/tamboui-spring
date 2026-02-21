package io.github.kylekreuter.tamboui.spring.template;

import io.github.kylekreuter.tamboui.spring.autoconfigure.TamboUiProperties;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders {@code .ttl} templates into TamboUI Widget trees.
 * <p>
 * Parses XML-based templates, evaluates SpEL expressions against
 * the model, and delegates to {@link TagHandler} implementations
 * for building the widget tree.
 * <p>
 * Supports the following built-in directives (processed before TagHandler lookup):
 * <ul>
 *   <li>{@code <t:if test="${expr}">} — conditional rendering</li>
 *   <li>{@code <t:each items="${collection}" var="item" index="i">} — iteration</li>
 *   <li>{@code <t:include template="fragmentName">} — template inclusion</li>
 * </ul>
 */
public class TemplateEngine {

    private static final Pattern SPEL_PATTERN = Pattern.compile("\\$\\{(.+?)}");
    private static final String DIRECTIVE_IF = "if";
    private static final String DIRECTIVE_EACH = "each";
    private static final String DIRECTIVE_INCLUDE = "include";

    private final TemplateCache cache;
    private final TamboUiProperties properties;
    private final Map<String, TagHandler> tagHandlers;
    private final ExpressionParser expressionParser;

    public TemplateEngine(TemplateCache cache, TamboUiProperties properties) {
        this(cache, properties, List.of());
    }

    public TemplateEngine(TemplateCache cache, TamboUiProperties properties, List<TagHandler> handlers) {
        this.cache = cache;
        this.properties = properties;
        this.expressionParser = new SpelExpressionParser();
        this.tagHandlers = new LinkedHashMap<>();
        if (handlers != null) {
            for (TagHandler handler : handlers) {
                this.tagHandlers.put(handler.getTagName(), handler);
            }
        }
    }

    /**
     * Render a template by name with the given model attributes.
     *
     * @param templateName the template name (without prefix/suffix)
     * @param model        the model attributes for SpEL evaluation
     * @return the rendered widget tree root (TamboUI Widget), or {@code null} if the root tag has no handler
     */
    public Object render(String templateName, Map<String, Object> model) {
        String path = properties.getTemplatePrefix() + templateName + properties.getTemplateSuffix();
        TemplateNode root = cache.get(path);
        if (root == null) {
            root = parse(path);
            cache.put(path, root);
        }
        return renderNode(root, model);
    }

    /**
     * Render a single {@link TemplateNode} and its children recursively.
     * SpEL expressions in attributes and text content are evaluated against the model.
     * The matching {@link TagHandler} is used to create the TamboUI widget for each node.
     * <p>
     * For directive nodes ({@code t:if}, {@code t:each}, {@code t:include}) this method
     * returns the first widget produced, or {@code null} if none. Use {@link #renderNodes}
     * for the full list of widgets from directives.
     *
     * @param node  the template node to render
     * @param model the model for SpEL evaluation
     * @return the created widget, or {@code null} if no handler is registered for the tag
     */
    Object renderNode(TemplateNode node, Map<String, Object> model) {
        List<Object> widgets = renderNodes(node, model, new HashSet<>());
        return widgets.isEmpty() ? null : widgets.get(0);
    }

    /**
     * Render a single {@link TemplateNode} into a list of widgets.
     * <p>
     * For regular tags this returns a single-element list (or empty if no handler exists).
     * For directives like {@code t:if} and {@code t:each}, this returns 0..N widgets
     * produced by the directive's children.
     *
     * @param node          the template node to render
     * @param model         the model for SpEL evaluation
     * @param includeStack  set of template paths currently being included (for recursion detection)
     * @return list of rendered widgets (never null, may be empty)
     */
    List<Object> renderNodes(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        String localName = stripNamespacePrefix(node.getTagName());

        // Handle built-in directives BEFORE TagHandler lookup
        return switch (localName) {
            case DIRECTIVE_IF -> handleIf(node, model, includeStack);
            case DIRECTIVE_EACH -> handleEach(node, model, includeStack);
            case DIRECTIVE_INCLUDE -> handleInclude(node, model, includeStack);
            default -> handleRegularNode(node, model, includeStack);
        };
    }

    // ========================================================================
    // Directive: t:if
    // ========================================================================

    /**
     * Handle the {@code <t:if test="${expr}">} directive.
     * Evaluates the SpEL boolean expression in the {@code test} attribute.
     * If true, renders all children directly (no wrapper widget).
     * If false, returns an empty list.
     *
     * @param node         the t:if node
     * @param model        the model for SpEL evaluation
     * @param includeStack include recursion guard
     * @return list of child widgets if condition is true, empty list otherwise
     * @throws TemplateRenderException if the test attribute is missing or does not evaluate to boolean
     */
    private List<Object> handleIf(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        String testExpr = node.getAttributes().get("test");
        if (testExpr == null || testExpr.isBlank()) {
            throw new TemplateRenderException(
                    "<t:if> requires a 'test' attribute with a SpEL expression");
        }

        EvaluationContext context = createEvaluationContext(model);
        boolean conditionResult = evaluateSpelAsBoolean(testExpr, context);

        if (!conditionResult) {
            return List.of();
        }

        // Condition is true: render all children and flatten their results
        return renderChildrenFlat(node, model, includeStack);
    }

    // ========================================================================
    // Directive: t:each
    // ========================================================================

    /**
     * Handle the {@code <t:each items="${collection}" var="item" index="i">} directive.
     * Iterates over the collection resolved from the {@code items} SpEL expression.
     * For each element, sets the loop variable and optional index variable in the model,
     * then renders all children. Results are flattened into a single list.
     *
     * @param node         the t:each node
     * @param model        the model for SpEL evaluation
     * @param includeStack include recursion guard
     * @return list of widgets produced by iterating over the collection
     * @throws TemplateRenderException if required attributes are missing or items is not iterable
     */
    private List<Object> handleEach(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        String itemsExpr = node.getAttributes().get("items");
        if (itemsExpr == null || itemsExpr.isBlank()) {
            throw new TemplateRenderException(
                    "<t:each> requires an 'items' attribute with a SpEL expression");
        }

        String varName = node.getAttributes().get("var");
        if (varName == null || varName.isBlank()) {
            throw new TemplateRenderException(
                    "<t:each> requires a 'var' attribute to name the loop variable");
        }

        String indexName = node.getAttributes().get("index");

        EvaluationContext context = createEvaluationContext(model);
        Iterable<?> items = evaluateSpelAsIterable(itemsExpr, context);

        List<Object> result = new ArrayList<>();
        int index = 0;
        for (Object item : items) {
            // Create a new model with the loop variables added
            Map<String, Object> loopModel = new LinkedHashMap<>(model);
            loopModel.put(varName, item);
            if (indexName != null && !indexName.isBlank()) {
                loopModel.put(indexName, index);
            }

            // Render all children in the loop context
            result.addAll(renderChildrenFlat(node, loopModel, includeStack));
            index++;
        }
        return result;
    }

    // ========================================================================
    // Directive: t:include
    // ========================================================================

    /**
     * Handle the {@code <t:include template="fragmentName">} directive.
     * Loads and renders the referenced template in the current SpEL context.
     * Detects recursive includes and throws an error.
     *
     * @param node         the t:include node
     * @param model        the model for SpEL evaluation
     * @param includeStack set of template paths currently being included
     * @return list containing the rendered root widget of the included template
     * @throws TemplateRenderException if template attribute is missing or recursive include is detected
     */
    private List<Object> handleInclude(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        String templateAttr = node.getAttributes().get("template");
        if (templateAttr == null || templateAttr.isBlank()) {
            throw new TemplateRenderException(
                    "<t:include> requires a 'template' attribute specifying the template name");
        }

        // Resolve any SpEL expressions in the template name
        EvaluationContext context = createEvaluationContext(model);
        String templateName = evaluateSpel(templateAttr, context);

        String path = properties.getTemplatePrefix() + templateName + properties.getTemplateSuffix();

        // Check for recursive includes
        if (includeStack.contains(path)) {
            throw new TemplateRenderException(
                    "Recursive template include detected: " + path
                            + " (include chain: " + includeStack + ")");
        }

        // Load and parse the template
        TemplateNode includedRoot = cache.get(path);
        if (includedRoot == null) {
            includedRoot = parse(path);
            cache.put(path, includedRoot);
        }

        // Add to include stack for recursion detection
        Set<String> newIncludeStack = new HashSet<>(includeStack);
        newIncludeStack.add(path);

        // Render the included template's root node in the current model context
        return renderNodes(includedRoot, model, newIncludeStack);
    }

    // ========================================================================
    // Regular node rendering
    // ========================================================================

    /**
     * Render a regular (non-directive) template node via its {@link TagHandler}.
     *
     * @param node         the template node
     * @param model        the model for SpEL evaluation
     * @param includeStack include recursion guard
     * @return single-element list with the widget, or empty list if no handler found
     */
    private List<Object> handleRegularNode(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        EvaluationContext context = createEvaluationContext(model);

        // Resolve SpEL in attributes
        Map<String, String> resolvedAttributes = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            resolvedAttributes.put(entry.getKey(), evaluateSpel(entry.getValue(), context));
        }

        // Resolve SpEL in text content
        String resolvedText = null;
        if (node.getTextContent() != null) {
            resolvedText = evaluateSpel(node.getTextContent(), context);
        }

        // If there is resolved text and no explicit t:text attribute, set it
        if (resolvedText != null && !resolvedText.isBlank() && !resolvedAttributes.containsKey("t:text")) {
            resolvedAttributes.put("t:text", resolvedText);
        }

        // Render children recursively (flattening directive results)
        List<Object> childWidgets = new ArrayList<>();
        for (TemplateNode child : node.getChildren()) {
            childWidgets.addAll(renderNodes(child, model, includeStack));
        }

        // Store children count in attributes for the tag handler to access
        if (!childWidgets.isEmpty()) {
            resolvedAttributes.put("__children__", String.valueOf(childWidgets.size()));
        }

        // Look up the tag handler by the local tag name (without namespace prefix)
        String localName = stripNamespacePrefix(node.getTagName());
        TagHandler handler = tagHandlers.get(localName);
        if (handler == null) {
            return List.of();
        }

        Object widget = handler.createElement(resolvedAttributes);

        // If the handler supports children, let it handle them
        if (handler instanceof ParentTagHandler parentHandler && !childWidgets.isEmpty()) {
            parentHandler.addChildren(widget, childWidgets);
        }

        // Wrap widget as RegionChild if a 'region' attribute is present
        // (used by DockTagHandler to assign children to dock regions)
        String region = resolvedAttributes.get("region");
        if (region != null && !region.isBlank()) {
            return List.of(
                    new io.github.kylekreuter.tamboui.spring.template.tags.DockTagHandler.RegionChild(region, widget));
        }

        return List.of(widget);
    }

    // ========================================================================
    // Helper methods for directives
    // ========================================================================

    /**
     * Render all children of a node and flatten the results into a single list.
     * This is used by directives that produce their children's widgets directly.
     */
    private List<Object> renderChildrenFlat(TemplateNode node, Map<String, Object> model, Set<String> includeStack) {
        List<Object> result = new ArrayList<>();
        for (TemplateNode child : node.getChildren()) {
            result.addAll(renderNodes(child, model, includeStack));
        }
        return result;
    }

    /**
     * Evaluate a SpEL expression (potentially wrapped in {@code ${...}}) to a boolean value.
     *
     * @param testExpr the expression string (e.g. {@code "${isLoggedIn}"} or {@code "${items.size() > 0}"})
     * @param context  the evaluation context
     * @return the boolean result
     * @throws TemplateRenderException if the expression does not evaluate to a boolean
     */
    private boolean evaluateSpelAsBoolean(String testExpr, EvaluationContext context) {
        // Extract the raw SpEL expression from ${...} wrapper if present
        String rawExpr = extractSpelExpression(testExpr);

        try {
            Expression expression = expressionParser.parseExpression(rawExpr);
            Object result = expression.getValue(context);

            if (result instanceof Boolean boolResult) {
                return boolResult;
            }

            // Try to coerce common truthy/falsy values
            if (result == null) {
                return false;
            }
            if (result instanceof Number number) {
                return number.doubleValue() != 0;
            }
            if (result instanceof String str) {
                return !str.isEmpty();
            }
            if (result instanceof Collection<?> coll) {
                return !coll.isEmpty();
            }

            throw new TemplateRenderException(
                    "<t:if> test expression must evaluate to a boolean, got: "
                            + result.getClass().getSimpleName() + " for expression: " + testExpr);
        } catch (TemplateRenderException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateRenderException(
                    "Failed to evaluate <t:if> test expression: " + testExpr, e);
        }
    }

    /**
     * Evaluate a SpEL expression to an {@link Iterable}.
     * Supports {@link Collection}, arrays, and {@link Iterable}.
     *
     * @param itemsExpr the expression string (e.g. {@code "${users}"})
     * @param context   the evaluation context
     * @return the iterable result
     * @throws TemplateRenderException if the expression does not evaluate to an iterable type
     */
    @SuppressWarnings("unchecked")
    private Iterable<?> evaluateSpelAsIterable(String itemsExpr, EvaluationContext context) {
        String rawExpr = extractSpelExpression(itemsExpr);

        try {
            Expression expression = expressionParser.parseExpression(rawExpr);
            Object result = expression.getValue(context);

            if (result == null) {
                return List.of();
            }
            if (result instanceof Iterable<?> iterable) {
                return iterable;
            }
            if (result.getClass().isArray()) {
                return arrayToList(result);
            }

            throw new TemplateRenderException(
                    "<t:each> items expression must evaluate to a Collection, Array, or Iterable, got: "
                            + result.getClass().getSimpleName() + " for expression: " + itemsExpr);
        } catch (TemplateRenderException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateRenderException(
                    "Failed to evaluate <t:each> items expression: " + itemsExpr, e);
        }
    }

    /**
     * Convert an array (possibly primitive) to a List.
     */
    private List<?> arrayToList(Object array) {
        if (array instanceof Object[] objArray) {
            return List.of(objArray);
        }
        // Handle primitive arrays
        int length = java.lang.reflect.Array.getLength(array);
        List<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(java.lang.reflect.Array.get(array, i));
        }
        return list;
    }

    /**
     * Extract the raw SpEL expression from a {@code ${...}} wrapper.
     * If the value is already a plain expression (no wrapper), returns it as-is.
     *
     * @param value the expression string, possibly wrapped in {@code ${...}}
     * @return the raw SpEL expression
     */
    private String extractSpelExpression(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            return trimmed.substring(2, trimmed.length() - 1);
        }
        return trimmed;
    }

    // ========================================================================
    // Template parsing
    // ========================================================================

    /**
     * Parse an XML template from the classpath into a {@link TemplateNode} tree.
     *
     * @param resourcePath the classpath resource path
     * @return the root template node
     * @throws TemplateParseException if the template cannot be found or parsed
     */
    TemplateNode parse(String resourcePath) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new TemplateParseException(
                    "Template not found on classpath: " + resourcePath);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Disable external entities for security
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            return elementToNode(rootElement);
        } catch (ParserConfigurationException | SAXException e) {
            throw new TemplateParseException(
                    "Failed to parse template: " + resourcePath, e);
        } catch (IOException e) {
            throw new TemplateParseException(
                    "Failed to read template: " + resourcePath, e);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
                // closing silently
            }
        }
    }

    /**
     * Recursively convert a DOM {@link Element} into a {@link TemplateNode}.
     */
    private TemplateNode elementToNode(Element element) {
        TemplateNode node = new TemplateNode(element.getTagName());

        // Copy attributes
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            node.setAttribute(attr.getNodeName(), attr.getNodeValue());
        }

        // Process child nodes
        StringBuilder textContent = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                node.addChild(elementToNode((Element) child));
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent();
                if (text != null) {
                    textContent.append(text);
                }
            }
        }

        String trimmedText = textContent.toString().trim();
        if (!trimmedText.isEmpty()) {
            node.setTextContent(trimmedText);
        }

        return node;
    }

    // ========================================================================
    // SpEL evaluation
    // ========================================================================

    /**
     * Evaluate SpEL expressions embedded in a string value.
     * Expressions are delimited by {@code ${...}} and are replaced with their
     * evaluated result converted to a String.
     *
     * @param value   the value potentially containing SpEL expressions
     * @param context the evaluation context
     * @return the value with all expressions resolved
     */
    String evaluateSpel(String value, EvaluationContext context) {
        if (value == null || !value.contains("${")) {
            return value;
        }

        Matcher matcher = SPEL_PATTERN.matcher(value);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String expressionString = matcher.group(1);
            try {
                Expression expression = expressionParser.parseExpression(expressionString);
                Object evaluated = expression.getValue(context);
                String replacement = evaluated != null ? evaluated.toString() : "";
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } catch (Exception e) {
                throw new TemplateRenderException(
                        "Failed to evaluate SpEL expression: ${" + expressionString + "}", e);
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Create a SpEL evaluation context with the model entries as variables.
     */
    private EvaluationContext createEvaluationContext(Map<String, Object> model) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (model != null) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
            // Set the model map as root object and register MapAccessor so that
            // simple property names like ${title} resolve to map key lookups
            context.setRootObject(model);
            context.addPropertyAccessor(new org.springframework.context.expression.MapAccessor());
        }
        return context;
    }

    /**
     * Strip the namespace prefix from a tag name.
     * For example, {@code "t:panel"} becomes {@code "panel"}.
     */
    private String stripNamespacePrefix(String tagName) {
        int colonIndex = tagName.indexOf(':');
        return colonIndex >= 0 ? tagName.substring(colonIndex + 1) : tagName;
    }
}
