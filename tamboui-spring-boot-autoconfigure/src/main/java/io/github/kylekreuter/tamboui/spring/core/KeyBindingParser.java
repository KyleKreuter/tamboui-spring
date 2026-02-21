package io.github.kylekreuter.tamboui.spring.core;

import java.util.Locale;
import java.util.Map;

import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.KeyModifiers;

/**
 * Parses {@code @OnKey} annotation values into structured key bindings.
 * <p>
 * Supports the following formats:
 * <ul>
 *   <li>Simple characters: {@code "q"}, {@code "a"}, {@code "1"}</li>
 *   <li>Modifier combinations: {@code "ctrl+c"}, {@code "alt+x"}, {@code "shift+a"}</li>
 *   <li>Multiple modifiers: {@code "ctrl+shift+x"}</li>
 *   <li>Special keys: {@code "esc"}, {@code "enter"}, {@code "tab"}, {@code "f1"}-{@code "f12"}, etc.</li>
 *   <li>Modifier + special: {@code "ctrl+esc"}, {@code "shift+up"}, etc.</li>
 * </ul>
 */
public final class KeyBindingParser {

    private static final Map<String, KeyCode> SPECIAL_KEYS = Map.ofEntries(
            Map.entry("esc", KeyCode.ESCAPE),
            Map.entry("escape", KeyCode.ESCAPE),
            Map.entry("enter", KeyCode.ENTER),
            Map.entry("tab", KeyCode.TAB),
            Map.entry("backspace", KeyCode.BACKSPACE),
            Map.entry("delete", KeyCode.DELETE),
            Map.entry("insert", KeyCode.INSERT),
            Map.entry("up", KeyCode.UP),
            Map.entry("down", KeyCode.DOWN),
            Map.entry("left", KeyCode.LEFT),
            Map.entry("right", KeyCode.RIGHT),
            Map.entry("home", KeyCode.HOME),
            Map.entry("end", KeyCode.END),
            Map.entry("pageup", KeyCode.PAGE_UP),
            Map.entry("page_up", KeyCode.PAGE_UP),
            Map.entry("pagedown", KeyCode.PAGE_DOWN),
            Map.entry("page_down", KeyCode.PAGE_DOWN),
            Map.entry("f1", KeyCode.F1),
            Map.entry("f2", KeyCode.F2),
            Map.entry("f3", KeyCode.F3),
            Map.entry("f4", KeyCode.F4),
            Map.entry("f5", KeyCode.F5),
            Map.entry("f6", KeyCode.F6),
            Map.entry("f7", KeyCode.F7),
            Map.entry("f8", KeyCode.F8),
            Map.entry("f9", KeyCode.F9),
            Map.entry("f10", KeyCode.F10),
            Map.entry("f11", KeyCode.F11),
            Map.entry("f12", KeyCode.F12)
    );

    private KeyBindingParser() {
        // Utility class
    }

    /**
     * Parses a key binding string into a {@link ParsedBinding}.
     *
     * @param binding the key binding string (e.g. {@code "ctrl+c"}, {@code "q"}, {@code "esc"})
     * @return the parsed binding
     * @throws IllegalArgumentException if the binding string is invalid
     */
    public static ParsedBinding parse(String binding) {
        if (binding == null || binding.isBlank()) {
            throw new IllegalArgumentException("Key binding must not be null or blank");
        }

        String normalized = binding.strip().toLowerCase(Locale.ROOT);
        String[] parts = normalized.split("\\+");

        boolean ctrl = false;
        boolean alt = false;
        boolean shift = false;
        String keyPart = null;

        for (String part : parts) {
            switch (part) {
                case "ctrl" -> ctrl = true;
                case "alt" -> alt = true;
                case "shift" -> shift = true;
                default -> {
                    if (keyPart != null) {
                        throw new IllegalArgumentException(
                                "Invalid key binding '%s': multiple non-modifier keys found ('%s' and '%s')"
                                        .formatted(binding, keyPart, part));
                    }
                    keyPart = part;
                }
            }
        }

        if (keyPart == null) {
            throw new IllegalArgumentException(
                    "Invalid key binding '%s': no key specified (only modifiers found)".formatted(binding));
        }

        KeyModifiers modifiers = KeyModifiers.of(ctrl, alt, shift);

        // Check for special key names
        KeyCode specialKey = SPECIAL_KEYS.get(keyPart);
        if (specialKey != null) {
            return new ParsedBinding(specialKey, modifiers, '\0');
        }

        // Must be a single character
        if (keyPart.length() != 1) {
            throw new IllegalArgumentException(
                    "Invalid key binding '%s': unknown key '%s'. Expected a single character or a special key name "
                            + "(esc, enter, tab, backspace, delete, insert, up, down, left, right, home, end, "
                            + "pageup, pagedown, f1-f12)".formatted(binding, keyPart));
        }

        return new ParsedBinding(KeyCode.CHAR, modifiers, keyPart.charAt(0));
    }

    /**
     * Represents a parsed key binding with key code, modifiers, and character.
     *
     * @param code      the key code ({@link KeyCode#CHAR} for printable characters, otherwise the special key)
     * @param modifiers the modifier keys (ctrl, alt, shift)
     * @param character the character for {@link KeyCode#CHAR} bindings, {@code '\0'} for special keys
     */
    public record ParsedBinding(KeyCode code, KeyModifiers modifiers, char character) {

        /**
         * Checks whether the given {@link KeyEvent} matches this binding.
         *
         * @param event the key event to check
         * @return {@code true} if the event matches this binding
         */
        public boolean matches(KeyEvent event) {
            if (event.code() != code) {
                return false;
            }
            if (!event.modifiers().equals(modifiers)) {
                return false;
            }
            if (code == KeyCode.CHAR) {
                return Character.toLowerCase(event.character()) == Character.toLowerCase(character);
            }
            return true;
        }
    }
}
