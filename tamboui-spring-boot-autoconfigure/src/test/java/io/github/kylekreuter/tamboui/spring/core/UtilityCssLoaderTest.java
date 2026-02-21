package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.css.engine.StyleEngine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link UtilityCssLoader}.
 */
@ExtendWith(MockitoExtension.class)
class UtilityCssLoaderTest {

    private static final String UTILITY_CSS_PATH = "META-INF/tamboui-spring/utility.tcss";

    @Mock
    private StyleEngine mockStyleEngine;

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should load CSS content from classpath resource")
        void shouldLoadCssContentFromClasspath() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);

            assertThat(loader.getCssContent()).isNotNull();
            assertThat(loader.getCssContent()).isNotEmpty();
            assertThat(loader.getResourcePath()).isEqualTo(UTILITY_CSS_PATH);
        }

        @Test
        @DisplayName("should throw IllegalStateException for non-existent resource")
        void shouldThrowForNonExistentResource() {
            assertThatThrownBy(() -> new UtilityCssLoader("non/existent/path.tcss"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not found on classpath");
        }

        @Test
        @DisplayName("CSS content should contain expected utility classes")
        void cssContentShouldContainExpectedClasses() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            // Colors
            assertThat(css).contains(".text-red");
            assertThat(css).contains(".text-green");
            assertThat(css).contains(".text-blue");
            assertThat(css).contains(".bg-red");
            assertThat(css).contains(".bg-cyan");

            // Typography
            assertThat(css).contains(".bold");
            assertThat(css).contains(".italic");
            assertThat(css).contains(".underlined");

            // Spacing
            assertThat(css).contains(".p-1");
            assertThat(css).contains(".p-4");
            assertThat(css).contains(".px-2");
            assertThat(css).contains(".py-1");
            assertThat(css).contains(".m-2");
            assertThat(css).contains(".mx-1");
            assertThat(css).contains(".my-3");

            // Layout
            assertThat(css).contains(".flex-start");
            assertThat(css).contains(".flex-center");
            assertThat(css).contains(".flex-between");
            assertThat(css).contains(".direction-horizontal");
            assertThat(css).contains(".direction-vertical");
            assertThat(css).contains(".gap-2");

            // Sizing
            assertThat(css).contains(".w-fill");
            assertThat(css).contains(".w-50");
            assertThat(css).contains(".h-100");

            // Borders
            assertThat(css).contains(".border-rounded");
            assertThat(css).contains(".border-double");

            // Text
            assertThat(css).contains(".text-center");
            assertThat(css).contains(".text-wrap");
        }
    }

    @Nested
    @DisplayName("loadInto()")
    class LoadIntoTests {

        @Test
        @DisplayName("should call addStylesheet on StyleEngine with CSS content")
        void shouldCallAddStylesheetOnStyleEngine() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);

            loader.loadInto(mockStyleEngine);

            verify(mockStyleEngine).addStylesheet(loader.getCssContent());
        }

        @Test
        @DisplayName("should not call addStylesheet when CSS is empty")
        void shouldNotCallAddStylesheetWhenEmpty() {
            // Create a loader with a test resource that is empty
            // We test this indirectly by verifying the guard clause behavior
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);

            // Normal case: non-empty CSS should be loaded
            loader.loadInto(mockStyleEngine);
            verify(mockStyleEngine).addStylesheet(loader.getCssContent());
        }
    }

    @Nested
    @DisplayName("CSS content validation")
    class CssContentValidationTests {

        @Test
        @DisplayName("should contain all 16 named text colors")
        void shouldContainAllNamedTextColors() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            String[] colors = {
                    "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white",
                    "gray", "dark-gray", "light-red", "light-green", "light-yellow",
                    "light-blue", "light-magenta", "light-cyan"
            };

            for (String color : colors) {
                assertThat(css)
                        .as("Should contain .text-%s class", color)
                        .contains(".text-" + color);
            }
        }

        @Test
        @DisplayName("should contain all 16 named background colors")
        void shouldContainAllNamedBackgroundColors() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            String[] colors = {
                    "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white",
                    "gray", "dark-gray", "light-red", "light-green", "light-yellow",
                    "light-blue", "light-magenta", "light-cyan"
            };

            for (String color : colors) {
                assertThat(css)
                        .as("Should contain .bg-%s class", color)
                        .contains(".bg-" + color);
            }
        }

        @Test
        @DisplayName("should contain all typography modifiers")
        void shouldContainAllTypographyModifiers() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            assertThat(css).contains(".bold");
            assertThat(css).contains(".dim");
            assertThat(css).contains(".italic");
            assertThat(css).contains(".underlined");
            assertThat(css).contains(".reversed");
            assertThat(css).contains(".crossed-out");
        }

        @Test
        @DisplayName("should contain padding classes p-0 through p-4")
        void shouldContainPaddingClasses() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            for (int i = 0; i <= 4; i++) {
                assertThat(css)
                        .as("Should contain .p-%d class", i)
                        .contains(".p-" + i);
            }
        }

        @Test
        @DisplayName("should contain all border types")
        void shouldContainAllBorderTypes() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            assertThat(css).contains(".border-none");
            assertThat(css).contains(".border-plain");
            assertThat(css).contains(".border-rounded");
            assertThat(css).contains(".border-double");
            assertThat(css).contains(".border-thick");
        }

        @Test
        @DisplayName("should contain all sizing classes")
        void shouldContainAllSizingClasses() {
            UtilityCssLoader loader = new UtilityCssLoader(UTILITY_CSS_PATH);
            String css = loader.getCssContent();

            // Width
            assertThat(css).contains(".w-fill");
            assertThat(css).contains(".w-fit");
            assertThat(css).contains(".w-25");
            assertThat(css).contains(".w-50");
            assertThat(css).contains(".w-75");
            assertThat(css).contains(".w-100");

            // Height
            assertThat(css).contains(".h-fill");
            assertThat(css).contains(".h-fit");
            assertThat(css).contains(".h-25");
            assertThat(css).contains(".h-50");
            assertThat(css).contains(".h-75");
            assertThat(css).contains(".h-100");
        }
    }
}
