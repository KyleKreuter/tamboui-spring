package io.github.kylekreuter.tamboui.spring.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Unit tests for {@link NavigationRouter}.
 */
class NavigationRouterTest {

    private NavigationRouter router;

    private final ScreenController dashboardController = model ->
            model.put("title", "Dashboard");

    private final ScreenController settingsController = model ->
            model.put("title", "Settings");

    @BeforeEach
    void setUp() {
        router = new NavigationRouter();
    }

    // ==================== Registration ====================

    @Nested
    @DisplayName("registerScreen()")
    class RegisterScreen {

        @Test
        @DisplayName("should register a screen controller with name and template")
        void registerSuccessfully() {
            router.registerScreen("dashboard", dashboardController, "dashboard");

            assertThat(router.hasScreen("dashboard")).isTrue();
            assertThat(router.getController("dashboard")).isSameAs(dashboardController);
            assertThat(router.getRegisteredScreens()).containsExactly("dashboard");
        }

        @Test
        @DisplayName("should register multiple screens")
        void registerMultipleScreens() {
            router.registerScreen("dashboard", dashboardController, "dashboard");
            router.registerScreen("settings", settingsController, "settings");

            assertThat(router.getRegisteredScreens()).containsExactlyInAnyOrder("dashboard", "settings");
        }

        @Test
        @DisplayName("should throw on null screenName")
        void nullScreenName() {
            assertThatNullPointerException()
                    .isThrownBy(() -> router.registerScreen(null, dashboardController, "dashboard"))
                    .withMessage("screenName must not be null");
        }

        @Test
        @DisplayName("should throw on blank screenName")
        void blankScreenName() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> router.registerScreen("  ", dashboardController, "dashboard"))
                    .withMessage("screenName must not be blank");
        }

        @Test
        @DisplayName("should throw on null controller")
        void nullController() {
            assertThatNullPointerException()
                    .isThrownBy(() -> router.registerScreen("dashboard", null, "dashboard"))
                    .withMessage("controller must not be null");
        }

        @Test
        @DisplayName("should throw on null templateName")
        void nullTemplateName() {
            assertThatNullPointerException()
                    .isThrownBy(() -> router.registerScreen("dashboard", dashboardController, null))
                    .withMessage("templateName must not be null");
        }

        @Test
        @DisplayName("should throw on blank templateName")
        void blankTemplateName() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> router.registerScreen("dashboard", dashboardController, "  "))
                    .withMessage("templateName must not be blank");
        }
    }

    // ==================== Navigation ====================

    @Nested
    @DisplayName("navigateTo()")
    class NavigateTo {

        @BeforeEach
        void registerScreens() {
            router.registerScreen("dashboard", dashboardController, "dashboard");
            router.registerScreen("settings", settingsController, "settings-view");
        }

        @Test
        @DisplayName("should set the active screen")
        void setActiveScreen() {
            router.navigateTo("dashboard");

            assertThat(router.getActiveScreen()).isEqualTo("dashboard");
        }

        @Test
        @DisplayName("should switch between screens")
        void switchBetweenScreens() {
            router.navigateTo("dashboard");
            assertThat(router.getActiveScreen()).isEqualTo("dashboard");

            router.navigateTo("settings");
            assertThat(router.getActiveScreen()).isEqualTo("settings");
        }

        @Test
        @DisplayName("should resolve the active controller")
        void resolveActiveController() {
            router.navigateTo("dashboard");
            assertThat(router.getActiveController()).isSameAs(dashboardController);

            router.navigateTo("settings");
            assertThat(router.getActiveController()).isSameAs(settingsController);
        }

        @Test
        @DisplayName("should resolve the active template name")
        void resolveActiveTemplateName() {
            router.navigateTo("dashboard");
            assertThat(router.getActiveTemplateName()).isEqualTo("dashboard");

            router.navigateTo("settings");
            assertThat(router.getActiveTemplateName()).isEqualTo("settings-view");
        }

        @Test
        @DisplayName("should throw on null screenName")
        void nullScreenName() {
            assertThatNullPointerException()
                    .isThrownBy(() -> router.navigateTo(null))
                    .withMessage("screenName must not be null");
        }

        @Test
        @DisplayName("should throw on blank screenName")
        void blankScreenName() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> router.navigateTo("  "))
                    .withMessage("screenName must not be blank");
        }

        @Test
        @DisplayName("should throw when navigating to an unregistered screen")
        void unregisteredScreen() {
            assertThatIllegalStateException()
                    .isThrownBy(() -> router.navigateTo("nonexistent"))
                    .withMessageContaining("No screen registered with name 'nonexistent'");
        }

        @Test
        @DisplayName("should allow navigating to the same screen again")
        void navigateToSameScreen() {
            router.navigateTo("dashboard");
            router.navigateTo("dashboard");

            assertThat(router.getActiveScreen()).isEqualTo("dashboard");
        }
    }

    // ==================== Listener ====================

    @Nested
    @DisplayName("ScreenChangeListener")
    class ListenerTests {

        @BeforeEach
        void registerScreens() {
            router.registerScreen("dashboard", dashboardController, "dashboard");
            router.registerScreen("settings", settingsController, "settings-view");
        }

        @Test
        @DisplayName("should notify listener on navigation")
        void notifyListener() {
            List<String> events = new ArrayList<>();

            router.addScreenChangeListener((prev, next, controller, template) ->
                    events.add(prev + " -> " + next + " [" + template + "]"));

            router.navigateTo("dashboard");
            assertThat(events).containsExactly("null -> dashboard [dashboard]");
        }

        @Test
        @DisplayName("should pass correct previous screen")
        void correctPreviousScreen() {
            List<String> previousScreens = new ArrayList<>();

            router.addScreenChangeListener((prev, next, controller, template) ->
                    previousScreens.add(prev));

            router.navigateTo("dashboard");
            router.navigateTo("settings");

            assertThat(previousScreens).containsExactly(null, "dashboard");
        }

        @Test
        @DisplayName("should pass the correct controller and template to listener")
        void correctControllerAndTemplate() {
            List<ScreenController> controllers = new ArrayList<>();
            List<String> templates = new ArrayList<>();

            router.addScreenChangeListener((prev, next, controller, template) -> {
                controllers.add(controller);
                templates.add(template);
            });

            router.navigateTo("dashboard");
            router.navigateTo("settings");

            assertThat(controllers).containsExactly(dashboardController, settingsController);
            assertThat(templates).containsExactly("dashboard", "settings-view");
        }

        @Test
        @DisplayName("should notify multiple listeners in order")
        void multipleListeners() {
            List<Integer> order = new ArrayList<>();

            router.addScreenChangeListener((p, n, c, t) -> order.add(1));
            router.addScreenChangeListener((p, n, c, t) -> order.add(2));
            router.addScreenChangeListener((p, n, c, t) -> order.add(3));

            router.navigateTo("dashboard");

            assertThat(order).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("should stop notifying after listener is removed")
        void removeListener() {
            List<String> events = new ArrayList<>();
            NavigationRouter.ScreenChangeListener listener =
                    (prev, next, controller, template) -> events.add(next);

            router.addScreenChangeListener(listener);
            router.navigateTo("dashboard");

            router.removeScreenChangeListener(listener);
            router.navigateTo("settings");

            assertThat(events).containsExactly("dashboard");
        }

        @Test
        @DisplayName("should throw on null listener")
        void nullListener() {
            assertThatNullPointerException()
                    .isThrownBy(() -> router.addScreenChangeListener(null))
                    .withMessage("listener must not be null");
        }
    }

    // ==================== Initial State ====================

    @Nested
    @DisplayName("Initial state")
    class InitialState {

        @Test
        @DisplayName("should have no active screen initially")
        void noActiveScreen() {
            assertThat(router.getActiveScreen()).isNull();
        }

        @Test
        @DisplayName("should have no active controller initially")
        void noActiveController() {
            assertThat(router.getActiveController()).isNull();
        }

        @Test
        @DisplayName("should have no active template initially")
        void noActiveTemplate() {
            assertThat(router.getActiveTemplateName()).isNull();
        }

        @Test
        @DisplayName("should have no registered screens initially")
        void noRegisteredScreens() {
            assertThat(router.getRegisteredScreens()).isEmpty();
        }

        @Test
        @DisplayName("hasScreen should return false for unknown screen")
        void hasScreenFalse() {
            assertThat(router.hasScreen("anything")).isFalse();
        }

        @Test
        @DisplayName("getController should return null for unknown screen")
        void getControllerNull() {
            assertThat(router.getController("anything")).isNull();
        }
    }

    // ==================== Registered screens (read-only view) ====================

    @Nested
    @DisplayName("getRegisteredScreens()")
    class RegisteredScreens {

        @Test
        @DisplayName("should return an unmodifiable set")
        void unmodifiableSet() {
            router.registerScreen("dashboard", dashboardController, "dashboard");
            Set<String> screens = router.getRegisteredScreens();

            assertThat(screens).hasSize(1);
            org.junit.jupiter.api.Assertions.assertThrows(
                    UnsupportedOperationException.class,
                    () -> screens.add("hacked")
            );
        }
    }
}
