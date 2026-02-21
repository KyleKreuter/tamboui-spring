package io.github.kylekreuter.tamboui.spring.css;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.tamboui.css.model.PropertyValue;
import dev.tamboui.css.model.Rule;
import dev.tamboui.css.model.Stylesheet;
import dev.tamboui.css.parser.CssParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Tests that validate the utility.tcss file can be parsed by the TamboUI
 * {@link CssParser} and that individual CSS rules produce the expected
 * selectors and property declarations.
 * <p>
 * Unlike {@code UtilityCssLoaderTest} which tests the loader mechanism,
 * these tests validate the <em>parsed CSS structure</em> at the rule level.
 */
class UtilityCssTest {

    private static final String UTILITY_CSS_PATH = "META-INF/tamboui-spring/utility.tcss";

    private static String cssContent;
    private static Stylesheet stylesheet;
    private static List<Rule> rules;

    @BeforeAll
    static void loadAndParseUtilityCss() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(UTILITY_CSS_PATH)) {
            assertThat(is)
                    .as("utility.tcss must be found on classpath at %s", UTILITY_CSS_PATH)
                    .isNotNull();
            cssContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertThat(cssContent).isNotBlank();

        stylesheet = CssParser.parse(cssContent);
        assertThat(stylesheet).isNotNull();

        rules = stylesheet.rules();
        assertThat(rules).isNotEmpty();
    }

    // -- Helper methods --

    /**
     * Finds the first rule whose selector CSS representation matches the given value.
     * Uses {@code Selector.toCss()} which produces e.g. {@code .text-red}.
     */
    private static Optional<Rule> findRule(String cssSelector) {
        return rules.stream()
                .filter(r -> r.selector().toCss().equals(cssSelector))
                .findFirst();
    }

    /**
     * Asserts that a rule with the given CSS selector exists and contains the expected declaration.
     * Uses {@code PropertyValue.toString()} to compare the value as a string.
     */
    private static void assertRuleHasDeclaration(String cssSelector, String property, String value) {
        Optional<Rule> rule = findRule(cssSelector);
        assertThat(rule)
                .as("Rule with selector '%s' should exist", cssSelector)
                .isPresent();

        Map<String, PropertyValue> declarations = rule.get().declarations();
        assertThat(declarations)
                .as("Rule '%s' should have property '%s'", cssSelector, property)
                .containsKey(property);
        assertThat(declarations.get(property).toString())
                .as("Rule '%s' property '%s' should be '%s'", cssSelector, property, value)
                .isEqualTo(value);
    }

    // -- Test groups --

    @Nested
    @DisplayName("CSS Loading and Parsing")
    class CssLoadingAndParsing {

        @Test
        @DisplayName("utility.tcss should be loadable from classpath")
        void utilityCssShouldBeLoadableFromClasspath() {
            assertThat(cssContent)
                    .isNotNull()
                    .isNotBlank();
        }

        @Test
        @DisplayName("utility.tcss should parse without errors via CssParser.parse()")
        void utilityCssShouldParseWithoutErrors() {
            assertThatNoException().isThrownBy(() -> CssParser.parse(cssContent));
        }

        @Test
        @DisplayName("parsed stylesheet should contain rules")
        void parsedStylesheetShouldContainRules() {
            assertThat(rules).isNotEmpty();
            // The utility.tcss has at least 80+ rules (colors, typography, spacing, layout, etc.)
            assertThat(rules.size()).isGreaterThanOrEqualTo(80);
        }
    }

    @Nested
    @DisplayName("Text Color Classes")
    class TextColorClasses {

        @ParameterizedTest(name = ".text-{0} -> color: {0}")
        @ValueSource(strings = {
                "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white",
                "gray", "dark-gray", "light-red", "light-green", "light-yellow",
                "light-blue", "light-magenta", "light-cyan"
        })
        @DisplayName("text color class should set correct color property")
        void textColorClassShouldSetCorrectColorProperty(String color) {
            assertRuleHasDeclaration(".text-" + color, "color", color);
        }
    }

    @Nested
    @DisplayName("Background Color Classes")
    class BackgroundColorClasses {

        @ParameterizedTest(name = ".bg-{0} -> background: {0}")
        @ValueSource(strings = {
                "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white",
                "gray", "dark-gray", "light-red", "light-green", "light-yellow",
                "light-blue", "light-magenta", "light-cyan"
        })
        @DisplayName("background color class should set correct background property")
        void backgroundColorClassShouldSetCorrectBackgroundProperty(String color) {
            assertRuleHasDeclaration(".bg-" + color, "background", color);
        }
    }

    @Nested
    @DisplayName("Typography Classes")
    class TypographyClasses {

        @ParameterizedTest(name = ".{0} -> text-style: {0}")
        @CsvSource({
                "bold, bold",
                "dim, dim",
                "italic, italic",
                "underlined, underlined",
                "reversed, reversed",
                "crossed-out, crossed-out"
        })
        @DisplayName("typography class should set correct text-style property")
        void typographyClassShouldSetCorrectTextStyleProperty(String className, String value) {
            assertRuleHasDeclaration("." + className, "text-style", value);
        }
    }

    @Nested
    @DisplayName("Padding Classes")
    class PaddingClasses {

        @ParameterizedTest(name = ".p-{0} -> padding: {0}")
        @CsvSource({
                "0, 0",
                "1, 1",
                "2, 2",
                "3, 3",
                "4, 4"
        })
        @DisplayName("uniform padding class should set correct padding property")
        void uniformPaddingClassShouldSetCorrectPaddingProperty(String suffix, String value) {
            assertRuleHasDeclaration(".p-" + suffix, "padding", value);
        }

        @ParameterizedTest(name = ".px-{0} -> padding: 0 {0}")
        @CsvSource({
                "0, 0 0",
                "1, 0 1",
                "2, 0 2",
                "3, 0 3",
                "4, 0 4"
        })
        @DisplayName("horizontal padding class should set correct padding property")
        void horizontalPaddingClassShouldSetCorrectPaddingProperty(String suffix, String value) {
            assertRuleHasDeclaration(".px-" + suffix, "padding", value);
        }

        @ParameterizedTest(name = ".py-{0} -> padding: {1}")
        @CsvSource({
                "0, 0 0",
                "1, 1 0",
                "2, 2 0",
                "3, 3 0",
                "4, 4 0"
        })
        @DisplayName("vertical padding class should set correct padding property")
        void verticalPaddingClassShouldSetCorrectPaddingProperty(String suffix, String value) {
            assertRuleHasDeclaration(".py-" + suffix, "padding", value);
        }

        @Test
        @DisplayName("individual padding sides should set correct values")
        void individualPaddingSidesShouldSetCorrectValues() {
            assertRuleHasDeclaration(".pt-1", "padding", "1 0 0 0");
            assertRuleHasDeclaration(".pr-1", "padding", "0 1 0 0");
            assertRuleHasDeclaration(".pb-1", "padding", "0 0 1 0");
            assertRuleHasDeclaration(".pl-1", "padding", "0 0 0 1");
        }
    }

    @Nested
    @DisplayName("Margin Classes")
    class MarginClasses {

        @ParameterizedTest(name = ".m-{0} -> margin: {0}")
        @CsvSource({
                "0, 0",
                "1, 1",
                "2, 2",
                "3, 3",
                "4, 4"
        })
        @DisplayName("uniform margin class should set correct margin property")
        void uniformMarginClassShouldSetCorrectMarginProperty(String suffix, String value) {
            assertRuleHasDeclaration(".m-" + suffix, "margin", value);
        }

        @ParameterizedTest(name = ".mx-{0} -> margin: 0 {0}")
        @CsvSource({
                "0, 0 0",
                "1, 0 1",
                "2, 0 2",
                "3, 0 3",
                "4, 0 4"
        })
        @DisplayName("horizontal margin class should set correct margin property")
        void horizontalMarginClassShouldSetCorrectMarginProperty(String suffix, String value) {
            assertRuleHasDeclaration(".mx-" + suffix, "margin", value);
        }

        @ParameterizedTest(name = ".my-{0} -> margin: {1}")
        @CsvSource({
                "0, 0 0",
                "1, 1 0",
                "2, 2 0",
                "3, 3 0",
                "4, 4 0"
        })
        @DisplayName("vertical margin class should set correct margin property")
        void verticalMarginClassShouldSetCorrectMarginProperty(String suffix, String value) {
            assertRuleHasDeclaration(".my-" + suffix, "margin", value);
        }
    }

    @Nested
    @DisplayName("Layout Classes")
    class LayoutClasses {

        @ParameterizedTest(name = ".flex-{0} -> flex: {1}")
        @CsvSource({
                "start, start",
                "center, center",
                "end, end",
                "between, space-between",
                "around, space-around",
                "evenly, space-evenly"
        })
        @DisplayName("flex alignment class should set correct flex property")
        void flexAlignmentClassShouldSetCorrectFlexProperty(String suffix, String value) {
            assertRuleHasDeclaration(".flex-" + suffix, "flex", value);
        }

        @ParameterizedTest(name = ".direction-{0} -> direction: {0}")
        @CsvSource({
                "horizontal, horizontal",
                "vertical, vertical"
        })
        @DisplayName("direction class should set correct direction property")
        void directionClassShouldSetCorrectDirectionProperty(String suffix, String value) {
            assertRuleHasDeclaration(".direction-" + suffix, "direction", value);
        }

        @ParameterizedTest(name = ".gap-{0} -> spacing: {0}")
        @CsvSource({
                "0, 0",
                "1, 1",
                "2, 2",
                "3, 3",
                "4, 4"
        })
        @DisplayName("gap class should set correct spacing property")
        void gapClassShouldSetCorrectSpacingProperty(String suffix, String value) {
            assertRuleHasDeclaration(".gap-" + suffix, "spacing", value);
        }
    }

    @Nested
    @DisplayName("Sizing Classes")
    class SizingClasses {

        @ParameterizedTest(name = ".w-{0} -> width: {1}")
        @CsvSource({
                "fill, fill",
                "fit, fit",
                "25, 25%",
                "50, 50%",
                "75, 75%",
                "100, 100%"
        })
        @DisplayName("width class should set correct width property")
        void widthClassShouldSetCorrectWidthProperty(String suffix, String value) {
            assertRuleHasDeclaration(".w-" + suffix, "width", value);
        }

        @ParameterizedTest(name = ".h-{0} -> height: {1}")
        @CsvSource({
                "fill, fill",
                "fit, fit",
                "25, 25%",
                "50, 50%",
                "75, 75%",
                "100, 100%"
        })
        @DisplayName("height class should set correct height property")
        void heightClassShouldSetCorrectHeightProperty(String suffix, String value) {
            assertRuleHasDeclaration(".h-" + suffix, "height", value);
        }
    }

    @Nested
    @DisplayName("Border Classes")
    class BorderClasses {

        @ParameterizedTest(name = ".border-{0} -> border-type: {0}")
        @CsvSource({
                "none, none",
                "plain, plain",
                "rounded, rounded",
                "double, double",
                "thick, thick"
        })
        @DisplayName("border class should set correct border-type property")
        void borderClassShouldSetCorrectBorderTypeProperty(String suffix, String value) {
            assertRuleHasDeclaration(".border-" + suffix, "border-type", value);
        }
    }

    @Nested
    @DisplayName("Text Alignment and Overflow Classes")
    class TextAlignmentAndOverflowClasses {

        @ParameterizedTest(name = ".text-{0} -> text-align: {0}")
        @CsvSource({
                "left, left",
                "center, center",
                "right, right"
        })
        @DisplayName("text alignment class should set correct text-align property")
        void textAlignmentClassShouldSetCorrectTextAlignProperty(String suffix, String value) {
            assertRuleHasDeclaration(".text-" + suffix, "text-align", value);
        }

        @ParameterizedTest(name = ".text-{0} -> text-overflow: {0}")
        @CsvSource({
                "clip, clip",
                "wrap, wrap"
        })
        @DisplayName("text overflow class should set correct text-overflow property")
        void textOverflowClassShouldSetCorrectTextOverflowProperty(String suffix, String value) {
            assertRuleHasDeclaration(".text-" + suffix, "text-overflow", value);
        }
    }

    @Nested
    @DisplayName("Rule Completeness")
    class RuleCompleteness {

        @Test
        @DisplayName("all rules should have non-empty selectors")
        void allRulesShouldHaveNonEmptySelectors() {
            for (Rule rule : rules) {
                assertThat(rule.selector().toCss())
                        .as("Every rule should have a non-empty CSS selector")
                        .isNotBlank();
            }
        }

        @Test
        @DisplayName("all rules should have at least one declaration")
        void allRulesShouldHaveAtLeastOneDeclaration() {
            for (Rule rule : rules) {
                assertThat(rule.declarations())
                        .as("Rule '%s' should have at least one declaration", rule.selector().toCss())
                        .isNotEmpty();
            }
        }

        @Test
        @DisplayName("should have exactly 16 text color rules")
        void shouldHaveExactly16TextColorRules() {
            long textColorCount = rules.stream()
                    .filter(r -> r.selector().toCss().startsWith(".text-"))
                    .filter(r -> r.declarations().containsKey("color"))
                    .count();
            assertThat(textColorCount).isEqualTo(16);
        }

        @Test
        @DisplayName("should have exactly 16 background color rules")
        void shouldHaveExactly16BackgroundColorRules() {
            long bgColorCount = rules.stream()
                    .filter(r -> r.selector().toCss().startsWith(".bg-"))
                    .filter(r -> r.declarations().containsKey("background"))
                    .count();
            assertThat(bgColorCount).isEqualTo(16);
        }

        @Test
        @DisplayName("should have exactly 6 typography rules")
        void shouldHaveExactly6TypographyRules() {
            long typographyCount = rules.stream()
                    .filter(r -> r.declarations().containsKey("text-style"))
                    .count();
            assertThat(typographyCount).isEqualTo(6);
        }

        @Test
        @DisplayName("should have exactly 5 border type rules")
        void shouldHaveExactly5BorderTypeRules() {
            long borderCount = rules.stream()
                    .filter(r -> r.selector().toCss().startsWith(".border-"))
                    .filter(r -> r.declarations().containsKey("border-type"))
                    .count();
            assertThat(borderCount).isEqualTo(5);
        }
    }
}
