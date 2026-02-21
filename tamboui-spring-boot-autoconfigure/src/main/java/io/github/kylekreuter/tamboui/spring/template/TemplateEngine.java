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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders {@code .ttl} templates into TamboUI Widget trees.
 * <p>
 * Parses XML-based templates, evaluates SpEL expressions against
 * the model, and delegates to {@link TagHandler} implementations
 * for building the widget tree.
 */
public class TemplateEngine {

    private static final Pattern SPEL_PATTERN = Pattern.compile("\\$\\{(.+?)}");

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
     *
     * @param node  the template node to render
     * @param model the model for SpEL evaluation
     * @return the created widget, or {@code null} if no handler is registered for the tag
     */
    Object renderNode(TemplateNode node, Map<String, Object> model) {
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

        // Render children recursively
        List<Object> childWidgets = new ArrayList<>();
        for (TemplateNode child : node.getChildren()) {
            Object childWidget = renderNode(child, model);
            if (childWidget != null) {
                childWidgets.add(childWidget);
            }
        }

        // Store children in attributes for the tag handler to access
        if (!childWidgets.isEmpty()) {
            resolvedAttributes.put("__children__", String.valueOf(childWidgets.size()));
        }

        // Look up the tag handler by the local tag name (without namespace prefix)
        String localName = stripNamespacePrefix(node.getTagName());
        TagHandler handler = tagHandlers.get(localName);
        if (handler == null) {
            return null;
        }

        Object widget = handler.createElement(resolvedAttributes);

        // If the handler supports children, let it handle them
        if (handler instanceof ParentTagHandler parentHandler && !childWidgets.isEmpty()) {
            parentHandler.addChildren(widget, childWidgets);
        }

        return widget;
    }

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
            // Also set a root object so that simple property names like ${title}
            // work without the # prefix
            context.setRootObject(model);
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
