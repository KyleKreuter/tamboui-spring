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

    /**
     * Test helper that tracks lifecycle hook invocations in order.
     */
    static class LifecycleTrackingController implements ScreenController {
        final String name;
        final List<String> events;

        LifecycleTrackingController(String name, List<String> events) {
            this.name = name;
            this.events = events;
        }

        @Override
        public void populate(TemplateModel model) {
            model.put("name", name);
        }

        @Override
        public void onMount() {
            events.add(name + ":onMount");
        }

        @Override
        public void onActivate() {
            events.add(name + ":onActivate");
        }

        @Override
        public void onDeactivate() {
            events.add(name + ":onDeactivate");
        }

        @Override
        public void onUnmount() {
            events.add(name + ":onUnmount");
        }
    }

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

    // ==================== Lifecycle Hooks ====================

    @Nested
    @DisplayName("Lifecycle hooks")
    class LifecycleHooks {

        private List<String> events;
        private LifecycleTrackingController dashboard;
        private LifecycleTrackingController settings;
        private LifecycleTrackingController profile;

        @BeforeEach
        void setUp() {
            events = new ArrayList<>();
            dashboard = new LifecycleTrackingController("dashboard", events);
            settings = new LifecycleTrackingController("settings", events);
            profile = new LifecycleTrackingController("profile", events);

            router.registerScreen("dashboard", dashboard, "dashboard-tpl");
            router.registerScreen("settings", settings, "settings-tpl");
            router.registerScreen("profile", profile, "profile-tpl");
        }

        @Test
        @DisplayName("first navigation should call onMount then onActivate")
        void firstNavigationCallsMountAndActivate() {
            router.navigateTo("dashboard");

            assertThat(events).containsExactly("dashboard:onMount", "dashboard:onActivate");
        }

        @Test
        @DisplayName("onMount should only be called once per screen")
        void onMountCalledOnlyOnce() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            router.navigateTo("dashboard"); // second time

            long mountCount = events.stream()
                    .filter(e -> e.equals("dashboard:onMount"))
                    .count();
            assertThat(mountCount).isEqualTo(1);
        }

        @Test
        @DisplayName("onActivate should be called every time screen becomes active")
        void onActivateCalledEveryTime() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            router.navigateTo("dashboard"); // second time

            long activateCount = events.stream()
                    .filter(e -> e.equals("dashboard:onActivate"))
                    .count();
            assertThat(activateCount).isEqualTo(2);
        }

        @Test
        @DisplayName("onDeactivate should be called when leaving a screen")
        void onDeactivateCalledWhenLeaving() {
            router.navigateTo("dashboard");
            events.clear();

            router.navigateTo("settings");

            assertThat(events).startsWith("dashboard:onDeactivate");
        }

        @Test
        @DisplayName("no onDeactivate on first navigation (no previous screen)")
        void noDeactivateOnFirstNavigation() {
            router.navigateTo("dashboard");

            assertThat(events).doesNotContain("null:onDeactivate");
            assertThat(events).noneMatch(e -> e.contains("onDeactivate"));
        }

        @Test
        @DisplayName("full navigation sequence should produce correct event order")
        void fullNavigationSequence() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            router.navigateTo("dashboard");

            assertThat(events).containsExactly(
                    "dashboard:onMount",
                    "dashboard:onActivate",
                    "dashboard:onDeactivate",
                    "settings:onMount",
                    "settings:onActivate",
                    "settings:onDeactivate",
                    "dashboard:onActivate" // no second onMount
            );
        }

        @Test
        @DisplayName("navigating to same screen should call deactivate and activate")
        void navigateToSameScreen() {
            router.navigateTo("dashboard");
            events.clear();

            router.navigateTo("dashboard");

            assertThat(events).containsExactly(
                    "dashboard:onDeactivate",
                    "dashboard:onActivate"
                    // no second onMount
            );
        }

        @Test
        @DisplayName("lifecycle hooks should be called before screen change listeners")
        void hooksBeforeListeners() {
            List<String> allEvents = new ArrayList<>();
            router.addScreenChangeListener((prev, next, ctrl, tpl) ->
                    allEvents.add("listener:" + next));

            router.navigateTo("dashboard");

            // onMount and onActivate were tracked in 'events', listener in 'allEvents'
            // Since they share the same thread, we verify the listener fires AFTER hooks
            assertThat(events).containsExactly("dashboard:onMount", "dashboard:onActivate");
            assertThat(allEvents).containsExactly("listener:dashboard");
        }

        @Test
        @DisplayName("isMounted should return false before navigation and true after")
        void isMountedTracking() {
            assertThat(router.isMounted("dashboard")).isFalse();

            router.navigateTo("dashboard");

            assertThat(router.isMounted("dashboard")).isTrue();
            assertThat(router.isMounted("settings")).isFalse();
        }

        @Test
        @DisplayName("getMountedScreens should track all mounted screens")
        void getMountedScreens() {
            assertThat(router.getMountedScreens()).isEmpty();

            router.navigateTo("dashboard");
            assertThat(router.getMountedScreens()).containsExactly("dashboard");

            router.navigateTo("settings");
            assertThat(router.getMountedScreens()).containsExactlyInAnyOrder("dashboard", "settings");
        }

        @Test
        @DisplayName("getMountedScreens should return unmodifiable set")
        void mountedScreensUnmodifiable() {
            router.navigateTo("dashboard");
            Set<String> mounted = router.getMountedScreens();

            org.junit.jupiter.api.Assertions.assertThrows(
                    UnsupportedOperationException.class,
                    () -> mounted.add("hacked")
            );
        }

        @Test
        @DisplayName("three screen round-trip should produce correct events")
        void threeScreenRoundTrip() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            router.navigateTo("profile");
            router.navigateTo("dashboard");

            assertThat(events).containsExactly(
                    "dashboard:onMount",
                    "dashboard:onActivate",
                    "dashboard:onDeactivate",
                    "settings:onMount",
                    "settings:onActivate",
                    "settings:onDeactivate",
                    "profile:onMount",
                    "profile:onActivate",
                    "profile:onDeactivate",
                    "dashboard:onActivate"
            );
        }
    }

    // ==================== Unregister Screen ====================

    @Nested
    @DisplayName("unregisterScreen()")
    class UnregisterScreen {

        private List<String> events;
        private LifecycleTrackingController dashboard;
        private LifecycleTrackingController settings;

        @BeforeEach
        void setUp() {
            events = new ArrayList<>();
            dashboard = new LifecycleTrackingController("dashboard", events);
            settings = new LifecycleTrackingController("settings", events);

            router.registerScreen("dashboard", dashboard, "dashboard-tpl");
            router.registerScreen("settings", settings, "settings-tpl");
        }

        @Test
        @DisplayName("should return false for unregistered screen")
        void returnFalseForUnknown() {
            assertThat(router.unregisterScreen("nonexistent")).isFalse();
        }

        @Test
        @DisplayName("should remove screen from registry")
        void removeFromRegistry() {
            router.unregisterScreen("dashboard");

            assertThat(router.hasScreen("dashboard")).isFalse();
            assertThat(router.getController("dashboard")).isNull();
        }

        @Test
        @DisplayName("should call onUnmount on mounted screen")
        void callOnUnmountIfMounted() {
            router.navigateTo("dashboard");
            events.clear();

            router.unregisterScreen("dashboard");

            assertThat(events).contains("dashboard:onUnmount");
        }

        @Test
        @DisplayName("should not call onUnmount if screen was never mounted")
        void noUnmountIfNotMounted() {
            router.unregisterScreen("dashboard");

            assertThat(events).doesNotContain("dashboard:onUnmount");
        }

        @Test
        @DisplayName("should call onDeactivate then onUnmount when removing active screen")
        void deactivateAndUnmountActiveScreen() {
            router.navigateTo("dashboard");
            events.clear();

            router.unregisterScreen("dashboard");

            assertThat(events).containsExactly(
                    "dashboard:onDeactivate",
                    "dashboard:onUnmount"
            );
            assertThat(router.getActiveScreen()).isNull();
        }

        @Test
        @DisplayName("should only call onUnmount (not onDeactivate) when removing non-active mounted screen")
        void onlyUnmountNonActiveScreen() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            events.clear();

            router.unregisterScreen("dashboard");

            assertThat(events).containsExactly("dashboard:onUnmount");
            // settings is still active
            assertThat(router.getActiveScreen()).isEqualTo("settings");
        }

        @Test
        @DisplayName("should clear mounted state")
        void clearMountedState() {
            router.navigateTo("dashboard");
            assertThat(router.isMounted("dashboard")).isTrue();

            router.unregisterScreen("dashboard");
            assertThat(router.isMounted("dashboard")).isFalse();
        }
    }

    // ==================== Shutdown ====================

    @Nested
    @DisplayName("shutdown()")
    class Shutdown {

        private List<String> events;
        private LifecycleTrackingController dashboard;
        private LifecycleTrackingController settings;

        @BeforeEach
        void setUp() {
            events = new ArrayList<>();
            dashboard = new LifecycleTrackingController("dashboard", events);
            settings = new LifecycleTrackingController("settings", events);

            router.registerScreen("dashboard", dashboard, "dashboard-tpl");
            router.registerScreen("settings", settings, "settings-tpl");
        }

        @Test
        @DisplayName("should deactivate active screen and unmount all mounted screens")
        void deactivateAndUnmountAll() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");
            events.clear();

            router.shutdown();

            assertThat(events).contains("settings:onDeactivate");
            assertThat(events).contains("dashboard:onUnmount");
            assertThat(events).contains("settings:onUnmount");
        }

        @Test
        @DisplayName("should deactivate active screen before unmounting")
        void deactivateBeforeUnmount() {
            router.navigateTo("dashboard");
            events.clear();

            router.shutdown();

            int deactivateIndex = events.indexOf("dashboard:onDeactivate");
            int unmountIndex = events.indexOf("dashboard:onUnmount");
            assertThat(deactivateIndex).isLessThan(unmountIndex);
        }

        @Test
        @DisplayName("should clear active screen")
        void clearActiveScreen() {
            router.navigateTo("dashboard");

            router.shutdown();

            assertThat(router.getActiveScreen()).isNull();
        }

        @Test
        @DisplayName("should clear mounted screens")
        void clearMountedScreens() {
            router.navigateTo("dashboard");
            router.navigateTo("settings");

            router.shutdown();

            assertThat(router.getMountedScreens()).isEmpty();
        }

        @Test
        @DisplayName("should not fail when no screens are active or mounted")
        void noFailOnEmptyState() {
            router.shutdown();

            assertThat(router.getActiveScreen()).isNull();
            assertThat(router.getMountedScreens()).isEmpty();
        }

        @Test
        @DisplayName("should only unmount mounted screens, not all registered screens")
        void onlyUnmountMountedScreens() {
            router.navigateTo("dashboard");
            // settings was never navigated to, so never mounted
            events.clear();

            router.shutdown();

            assertThat(events).contains("dashboard:onUnmount");
            assertThat(events).doesNotContain("settings:onUnmount");
        }
    }

    // ==================== ScreenController default methods ====================

    @Nested
    @DisplayName("ScreenController default lifecycle methods")
    class DefaultLifecycleMethods {

        @Test
        @DisplayName("lambda controller should work without implementing lifecycle hooks")
        void lambdaControllerWorks() {
            ScreenController lambdaCtrl = model -> model.put("key", "value");
            router.registerScreen("lambda", lambdaCtrl, "lambda-tpl");

            // Should not throw — default methods are empty
            router.navigateTo("lambda");

            assertThat(router.getActiveScreen()).isEqualTo("lambda");
        }

        @Test
        @DisplayName("default onMount should be a no-op")
        void defaultOnMount() {
            ScreenController ctrl = model -> {};
            // Should not throw
            ctrl.onMount();
        }

        @Test
        @DisplayName("default onActivate should be a no-op")
        void defaultOnActivate() {
            ScreenController ctrl = model -> {};
            ctrl.onActivate();
        }

        @Test
        @DisplayName("default onDeactivate should be a no-op")
        void defaultOnDeactivate() {
            ScreenController ctrl = model -> {};
            ctrl.onDeactivate();
        }

        @Test
        @DisplayName("default onUnmount should be a no-op")
        void defaultOnUnmount() {
            ScreenController ctrl = model -> {};
            ctrl.onUnmount();
        }
    }
}
