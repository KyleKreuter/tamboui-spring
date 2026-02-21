package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.KeyModifiers;

import io.github.kylekreuter.tamboui.spring.core.KeyBindingParser.ParsedBinding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link KeyBindingParser}.
 */
class KeyBindingParserTest {

    @Nested
    @DisplayName("Simple character bindings")
    class SimpleCharacters {

        @Test
        @DisplayName("parse('q') should return CHAR binding with character 'q'")
        void parseSingleCharQ() {
            ParsedBinding binding = KeyBindingParser.parse("q");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('q');
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }

        @Test
        @DisplayName("parse('a') should return CHAR binding with character 'a'")
        void parseSingleCharA() {
            ParsedBinding binding = KeyBindingParser.parse("a");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('a');
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }

        @Test
        @DisplayName("parse('1') should return CHAR binding with character '1'")
        void parseSingleCharDigit() {
            ParsedBinding binding = KeyBindingParser.parse("1");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('1');
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }
    }

    @Nested
    @DisplayName("Modifier combinations")
    class ModifierCombinations {

        @Test
        @DisplayName("parse('ctrl+c') should return CHAR binding with Ctrl modifier")
        void parseCtrlC() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+c");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('c');
            assertThat(binding.modifiers().ctrl()).isTrue();
            assertThat(binding.modifiers().alt()).isFalse();
            assertThat(binding.modifiers().shift()).isFalse();
        }

        @Test
        @DisplayName("parse('alt+x') should return CHAR binding with Alt modifier")
        void parseAltX() {
            ParsedBinding binding = KeyBindingParser.parse("alt+x");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('x');
            assertThat(binding.modifiers().ctrl()).isFalse();
            assertThat(binding.modifiers().alt()).isTrue();
            assertThat(binding.modifiers().shift()).isFalse();
        }

        @Test
        @DisplayName("parse('shift+a') should return CHAR binding with Shift modifier")
        void parseShiftA() {
            ParsedBinding binding = KeyBindingParser.parse("shift+a");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('a');
            assertThat(binding.modifiers().ctrl()).isFalse();
            assertThat(binding.modifiers().alt()).isFalse();
            assertThat(binding.modifiers().shift()).isTrue();
        }

        @Test
        @DisplayName("parse('ctrl+shift+x') should return CHAR binding with Ctrl+Shift modifiers")
        void parseCtrlShiftX() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+shift+x");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('x');
            assertThat(binding.modifiers().ctrl()).isTrue();
            assertThat(binding.modifiers().alt()).isFalse();
            assertThat(binding.modifiers().shift()).isTrue();
        }

        @Test
        @DisplayName("parse('ctrl+alt+shift+z') should return CHAR binding with all modifiers")
        void parseAllModifiers() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+alt+shift+z");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('z');
            assertThat(binding.modifiers().ctrl()).isTrue();
            assertThat(binding.modifiers().alt()).isTrue();
            assertThat(binding.modifiers().shift()).isTrue();
        }
    }

    @Nested
    @DisplayName("Special keys")
    class SpecialKeys {

        @Test
        @DisplayName("parse('esc') should return ESCAPE key code")
        void parseEsc() {
            ParsedBinding binding = KeyBindingParser.parse("esc");

            assertThat(binding.code()).isEqualTo(KeyCode.ESCAPE);
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }

        @Test
        @DisplayName("parse('escape') should return ESCAPE key code")
        void parseEscape() {
            ParsedBinding binding = KeyBindingParser.parse("escape");

            assertThat(binding.code()).isEqualTo(KeyCode.ESCAPE);
        }

        @Test
        @DisplayName("parse('enter') should return ENTER key code")
        void parseEnter() {
            ParsedBinding binding = KeyBindingParser.parse("enter");

            assertThat(binding.code()).isEqualTo(KeyCode.ENTER);
        }

        @Test
        @DisplayName("parse('tab') should return TAB key code")
        void parseTab() {
            ParsedBinding binding = KeyBindingParser.parse("tab");

            assertThat(binding.code()).isEqualTo(KeyCode.TAB);
        }

        @Test
        @DisplayName("parse('backspace') should return BACKSPACE key code")
        void parseBackspace() {
            ParsedBinding binding = KeyBindingParser.parse("backspace");

            assertThat(binding.code()).isEqualTo(KeyCode.BACKSPACE);
        }

        @Test
        @DisplayName("parse('delete') should return DELETE key code")
        void parseDelete() {
            ParsedBinding binding = KeyBindingParser.parse("delete");

            assertThat(binding.code()).isEqualTo(KeyCode.DELETE);
        }

        @Test
        @DisplayName("parse('insert') should return INSERT key code")
        void parseInsert() {
            ParsedBinding binding = KeyBindingParser.parse("insert");

            assertThat(binding.code()).isEqualTo(KeyCode.INSERT);
        }

        @ParameterizedTest
        @ValueSource(strings = {"up", "down", "left", "right"})
        @DisplayName("parse arrow keys should return correct key codes")
        void parseArrowKeys(String key) {
            ParsedBinding binding = KeyBindingParser.parse(key);

            assertThat(binding.code()).isEqualTo(KeyCode.valueOf(key.toUpperCase()));
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }

        @Test
        @DisplayName("parse('home') should return HOME key code")
        void parseHome() {
            assertThat(KeyBindingParser.parse("home").code()).isEqualTo(KeyCode.HOME);
        }

        @Test
        @DisplayName("parse('end') should return END key code")
        void parseEnd() {
            assertThat(KeyBindingParser.parse("end").code()).isEqualTo(KeyCode.END);
        }

        @Test
        @DisplayName("parse('pageup') should return PAGE_UP key code")
        void parsePageUp() {
            assertThat(KeyBindingParser.parse("pageup").code()).isEqualTo(KeyCode.PAGE_UP);
        }

        @Test
        @DisplayName("parse('page_up') should also return PAGE_UP key code")
        void parsePageUpWithUnderscore() {
            assertThat(KeyBindingParser.parse("page_up").code()).isEqualTo(KeyCode.PAGE_UP);
        }

        @Test
        @DisplayName("parse('pagedown') should return PAGE_DOWN key code")
        void parsePageDown() {
            assertThat(KeyBindingParser.parse("pagedown").code()).isEqualTo(KeyCode.PAGE_DOWN);
        }

        @Test
        @DisplayName("parse('page_down') should also return PAGE_DOWN key code")
        void parsePageDownWithUnderscore() {
            assertThat(KeyBindingParser.parse("page_down").code()).isEqualTo(KeyCode.PAGE_DOWN);
        }

        @ParameterizedTest
        @ValueSource(strings = {"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10", "f11", "f12"})
        @DisplayName("parse function keys should return correct key codes")
        void parseFunctionKeys(String key) {
            ParsedBinding binding = KeyBindingParser.parse(key);

            assertThat(binding.code()).isEqualTo(KeyCode.valueOf(key.toUpperCase()));
            assertThat(binding.modifiers()).isEqualTo(KeyModifiers.NONE);
        }
    }

    @Nested
    @DisplayName("Modifier + special key combinations")
    class ModifierPlusSpecial {

        @Test
        @DisplayName("parse('ctrl+esc') should return ESCAPE with Ctrl modifier")
        void parseCtrlEsc() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+esc");

            assertThat(binding.code()).isEqualTo(KeyCode.ESCAPE);
            assertThat(binding.modifiers().ctrl()).isTrue();
        }

        @Test
        @DisplayName("parse('shift+up') should return UP with Shift modifier")
        void parseShiftUp() {
            ParsedBinding binding = KeyBindingParser.parse("shift+up");

            assertThat(binding.code()).isEqualTo(KeyCode.UP);
            assertThat(binding.modifiers().shift()).isTrue();
        }

        @Test
        @DisplayName("parse('ctrl+f1') should return F1 with Ctrl modifier")
        void parseCtrlF1() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+f1");

            assertThat(binding.code()).isEqualTo(KeyCode.F1);
            assertThat(binding.modifiers().ctrl()).isTrue();
        }
    }

    @Nested
    @DisplayName("Case insensitivity")
    class CaseInsensitivity {

        @Test
        @DisplayName("parse('Ctrl+C') should work case-insensitively")
        void parseMixedCase() {
            ParsedBinding binding = KeyBindingParser.parse("Ctrl+C");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('c');
            assertThat(binding.modifiers().ctrl()).isTrue();
        }

        @Test
        @DisplayName("parse('ESC') should work case-insensitively")
        void parseUpperCaseSpecial() {
            assertThat(KeyBindingParser.parse("ESC").code()).isEqualTo(KeyCode.ESCAPE);
        }
    }

    @Nested
    @DisplayName("Whitespace handling")
    class WhitespaceHandling {

        @Test
        @DisplayName("parse(' q ') should handle leading/trailing whitespace")
        void parseWithWhitespace() {
            ParsedBinding binding = KeyBindingParser.parse(" q ");

            assertThat(binding.code()).isEqualTo(KeyCode.CHAR);
            assertThat(binding.character()).isEqualTo('q');
        }
    }

    @Nested
    @DisplayName("Error cases")
    class ErrorCases {

        @Test
        @DisplayName("parse(null) should throw IllegalArgumentException")
        void parseNull() {
            assertThatThrownBy(() -> KeyBindingParser.parse(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null or blank");
        }

        @Test
        @DisplayName("parse('') should throw IllegalArgumentException")
        void parseEmpty() {
            assertThatThrownBy(() -> KeyBindingParser.parse(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null or blank");
        }

        @Test
        @DisplayName("parse('   ') should throw IllegalArgumentException")
        void parseBlank() {
            assertThatThrownBy(() -> KeyBindingParser.parse("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null or blank");
        }

        @Test
        @DisplayName("parse('ctrl') should throw IllegalArgumentException (only modifiers)")
        void parseOnlyModifier() {
            assertThatThrownBy(() -> KeyBindingParser.parse("ctrl"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no key specified");
        }

        @Test
        @DisplayName("parse('ctrl+shift') should throw IllegalArgumentException (only modifiers)")
        void parseOnlyModifiers() {
            assertThatThrownBy(() -> KeyBindingParser.parse("ctrl+shift"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no key specified");
        }

        @Test
        @DisplayName("parse('unknown') should throw IllegalArgumentException")
        void parseUnknownKey() {
            assertThatThrownBy(() -> KeyBindingParser.parse("unknown"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown key");
        }

        @Test
        @DisplayName("parse('ctrl+ab') should throw IllegalArgumentException (multi-char non-special)")
        void parseMultiCharKey() {
            assertThatThrownBy(() -> KeyBindingParser.parse("ctrl+ab"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown key");
        }

        @Test
        @DisplayName("parse('a+b') should throw IllegalArgumentException (multiple non-modifier keys)")
        void parseMultipleKeys() {
            assertThatThrownBy(() -> KeyBindingParser.parse("a+b"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("multiple non-modifier keys");
        }
    }

    @Nested
    @DisplayName("ParsedBinding.matches()")
    class Matching {

        @Test
        @DisplayName("'q' binding should match KeyEvent.ofChar('q')")
        void simpleCharMatch() {
            ParsedBinding binding = KeyBindingParser.parse("q");

            assertThat(binding.matches(KeyEvent.ofChar('q'))).isTrue();
        }

        @Test
        @DisplayName("'q' binding should match KeyEvent.ofChar('Q') (case-insensitive)")
        void caseInsensitiveCharMatch() {
            ParsedBinding binding = KeyBindingParser.parse("q");

            assertThat(binding.matches(KeyEvent.ofChar('Q'))).isTrue();
        }

        @Test
        @DisplayName("'q' binding should not match KeyEvent.ofChar('x')")
        void simpleCharNoMatch() {
            ParsedBinding binding = KeyBindingParser.parse("q");

            assertThat(binding.matches(KeyEvent.ofChar('x'))).isFalse();
        }

        @Test
        @DisplayName("'ctrl+c' binding should match Ctrl+C event")
        void ctrlCMatch() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+c");
            KeyEvent event = KeyEvent.ofChar('c', KeyModifiers.CTRL);

            assertThat(binding.matches(event)).isTrue();
        }

        @Test
        @DisplayName("'ctrl+c' binding should not match plain 'c' event")
        void ctrlCNoMatchWithoutModifier() {
            ParsedBinding binding = KeyBindingParser.parse("ctrl+c");

            assertThat(binding.matches(KeyEvent.ofChar('c'))).isFalse();
        }

        @Test
        @DisplayName("'esc' binding should match ESCAPE event")
        void escapeMatch() {
            ParsedBinding binding = KeyBindingParser.parse("esc");

            assertThat(binding.matches(KeyEvent.ofKey(KeyCode.ESCAPE))).isTrue();
        }

        @Test
        @DisplayName("'esc' binding should not match ENTER event")
        void escapeNoMatch() {
            ParsedBinding binding = KeyBindingParser.parse("esc");

            assertThat(binding.matches(KeyEvent.ofKey(KeyCode.ENTER))).isFalse();
        }

        @Test
        @DisplayName("'shift+up' binding should match Shift+UP event")
        void shiftUpMatch() {
            ParsedBinding binding = KeyBindingParser.parse("shift+up");
            KeyEvent event = KeyEvent.ofKey(KeyCode.UP, KeyModifiers.SHIFT);

            assertThat(binding.matches(event)).isTrue();
        }

        @Test
        @DisplayName("'shift+up' binding should not match plain UP event")
        void shiftUpNoMatchWithoutModifier() {
            ParsedBinding binding = KeyBindingParser.parse("shift+up");
            KeyEvent event = KeyEvent.ofKey(KeyCode.UP);

            assertThat(binding.matches(event)).isFalse();
        }

        @Test
        @DisplayName("'q' binding should not match ESCAPE event")
        void charShouldNotMatchSpecialKey() {
            ParsedBinding binding = KeyBindingParser.parse("q");

            assertThat(binding.matches(KeyEvent.ofKey(KeyCode.ESCAPE))).isFalse();
        }

        @Test
        @DisplayName("'enter' binding should not match character event")
        void specialShouldNotMatchChar() {
            ParsedBinding binding = KeyBindingParser.parse("enter");

            assertThat(binding.matches(KeyEvent.ofChar('e'))).isFalse();
        }
    }
}
