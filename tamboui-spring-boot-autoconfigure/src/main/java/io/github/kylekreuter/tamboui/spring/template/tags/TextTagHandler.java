package io.github.kylekreuter.tamboui.spring.template.tags;

import dev.tamboui.layout.Alignment;
import dev.tamboui.style.Overflow;
import dev.tamboui.toolkit.elements.TextElement;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;

import java.util.Locale;
import java.util.Map;

/**
 * Tag handler for {@code <t:text>}.
 * Creates a TamboUI {@link TextElement} with the resolved text content.
 * <p>
 * Supported attributes:
 * <ul>
 *   <li>{@code content} or {@code value} — the text content to display</li>
 *   <li>{@code overflow} — text overflow mode: clip, ellipsis, ellipsis-start, ellipsis-middle, wrap-word, wrap-character</li>
 *   <li>{@code alignment} or {@code text-align} — text alignment: left, center, right</li>
 * </ul>
 */
public class TextTagHandler implements TagHandler {

    @Override
    public String getTagName() {
        return "text";
    }

    @Override
    public Object createElement(Map<String, String> attributes) {
        String content = resolveContent(attributes);
        TextElement element = new TextElement(content);

        String overflow = attributes.get("overflow");
        if (overflow != null) {
            element.overflow(parseOverflow(overflow));
        }

        String alignment = attributes.getOrDefault("alignment", attributes.get("text-align"));
        if (alignment != null) {
            element.alignment(parseAlignment(alignment));
        }

        return element;
    }

    /**
     * Resolves the text content from attributes.
     * Checks {@code content} first, then {@code value}, defaulting to an empty string.
     */
    private String resolveContent(Map<String, String> attributes) {
        String content = attributes.get("content");
        if (content != null) {
            return content;
        }
        String value = attributes.get("value");
        return value != null ? value : "";
    }

    /**
     * Parses the overflow attribute string to a TamboUI {@link Overflow} enum value.
     */
    static Overflow parseOverflow(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "ellipsis" -> Overflow.ELLIPSIS;
            case "ellipsis-start" -> Overflow.ELLIPSIS_START;
            case "ellipsis-middle" -> Overflow.ELLIPSIS_MIDDLE;
            case "wrap-word" -> Overflow.WRAP_WORD;
            case "wrap-character" -> Overflow.WRAP_CHARACTER;
            default -> Overflow.CLIP;
        };
    }

    /**
     * Parses the alignment attribute string to a TamboUI {@link Alignment} enum value.
     */
    static Alignment parseAlignment(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "center" -> Alignment.CENTER;
            case "right" -> Alignment.RIGHT;
            default -> Alignment.LEFT;
        };
    }
}
