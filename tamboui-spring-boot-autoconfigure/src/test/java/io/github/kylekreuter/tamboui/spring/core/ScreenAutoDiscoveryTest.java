package io.github.kylekreuter.tamboui.spring.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.autoconfigure.TamboUiProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit tests for {@link ScreenAutoDiscovery}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScreenAutoDiscoveryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private NavigationRouter navigationRouter;

    private TamboUiProperties properties;

    private ScreenAutoDiscovery discovery;

    @BeforeEach
    void setUp() {
        properties = new TamboUiProperties();
        discovery = new ScreenAutoDiscovery(applicationContext, navigationRouter, properties);
    }

    // ==================== No Screens ====================

    @Nested
    @DisplayName("No screens found")
    class NoScreens {

        @Test
        @DisplayName("should do nothing when no @TamboScreen beans exist")
        void noScreenBeans() {
            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(Collections.emptyMap());

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter, never()).registerScreen(anyString(), any(), anyString());
            verify(navigationRouter, never()).navigateTo(anyString());
        }
    }

    // ==================== Registration ====================

    @Nested
    @DisplayName("Screen registration")
    class Registration {

        @Test
        @DisplayName("should register @TamboScreen bean with bean name as screen name")
        void registerWithBeanName() {
            DashboardBean bean = new DashboardBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("dashboardBean", bean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);
            when(navigationRouter.hasScreen("dashboardBean")).thenReturn(true);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).registerScreen("dashboardBean", bean, "dashboard");
        }

        @Test
        @DisplayName("should register @TamboScreen bean with annotation value as screen name")
        void registerWithAnnotationValue() {
            NamedDashboardBean bean = new NamedDashboardBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("namedDashboardBean", bean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);
            when(navigationRouter.hasScreen("myDashboard")).thenReturn(true);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).registerScreen("myDashboard", bean, "dashboard");
        }

        @Test
        @DisplayName("should register multiple screens")
        void registerMultipleScreens() {
            DashboardBean dashBean = new DashboardBean();
            SettingsBean settingsBean = new SettingsBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("dashboardBean", dashBean);
            beans.put("settingsBean", settingsBean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);
            when(navigationRouter.hasScreen("dashboardBean")).thenReturn(true);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).registerScreen("dashboardBean", dashBean, "dashboard");
            verify(navigationRouter).registerScreen("settingsBean", settingsBean, "settings-view");
        }

        @Test
        @DisplayName("should skip beans that do not implement ScreenController")
        void skipNonScreenControllerBeans() {
            Object nonController = new NonControllerBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("nonControllerBean", nonController);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter, never()).registerScreen(anyString(), any(), anyString());
        }
    }

    // ==================== Initial Navigation ====================

    @Nested
    @DisplayName("Initial navigation")
    class InitialNavigation {

        @Test
        @DisplayName("should navigate to first discovered screen when no default configured")
        void navigateToFirstScreen() {
            DashboardBean bean = new DashboardBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("dashboardBean", bean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).navigateTo("dashboardBean");
        }

        @Test
        @DisplayName("should navigate to configured default screen")
        void navigateToConfiguredDefault() {
            DashboardBean dashBean = new DashboardBean();
            SettingsBean settingsBean = new SettingsBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("dashboardBean", dashBean);
            beans.put("settingsBean", settingsBean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);
            when(navigationRouter.hasScreen("settingsBean")).thenReturn(true);

            properties.setDefaultScreen("settingsBean");
            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).navigateTo("settingsBean");
        }

        @Test
        @DisplayName("should fall back to first screen when configured default is not found")
        void fallbackWhenDefaultNotFound() {
            DashboardBean bean = new DashboardBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("dashboardBean", bean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);
            when(navigationRouter.hasScreen("nonexistent")).thenReturn(false);

            properties.setDefaultScreen("nonexistent");
            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).navigateTo("dashboardBean");
        }

        @Test
        @DisplayName("should navigate to first screen using @TamboScreen.value() when set")
        void navigateToFirstScreenWithAnnotationValue() {
            NamedDashboardBean bean = new NamedDashboardBean();
            Map<String, Object> beans = new LinkedHashMap<>();
            beans.put("namedDashboardBean", bean);

            when(applicationContext.getBeansWithAnnotation(TamboScreen.class))
                    .thenReturn(beans);

            discovery.afterSingletonsInstantiated();

            verify(navigationRouter).navigateTo("myDashboard");
        }
    }

    // --- Test beans ---

    @TamboScreen(template = "dashboard")
    static class DashboardBean implements ScreenController {
        @Override
        public void populate(TemplateModel model) {}
    }

    @TamboScreen(value = "myDashboard", template = "dashboard")
    static class NamedDashboardBean implements ScreenController {
        @Override
        public void populate(TemplateModel model) {}
    }

    @TamboScreen(template = "settings-view")
    static class SettingsBean implements ScreenController {
        @Override
        public void populate(TemplateModel model) {}
    }

    @TamboScreen(template = "broken")
    static class NonControllerBean {
        // Intentionally does NOT implement ScreenController
    }
}
