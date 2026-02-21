package io.github.kylekreuter.tamboui.spring.core;

import java.util.Map;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.autoconfigure.TamboUiProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

/**
 * Discovers all {@link TamboScreen @TamboScreen}-annotated beans at startup and
 * registers them on the {@link NavigationRouter}.
 * <p>
 * Implements {@link SmartInitializingSingleton} so that discovery runs after all
 * singleton beans have been fully initialized (including any BeanPostProcessor work).
 * <p>
 * The screen name is derived from {@link TamboScreen#value()} if non-empty, otherwise
 * from the Spring bean name. The template name is taken from {@link TamboScreen#template()}.
 * <p>
 * After registration, the router auto-navigates to the configured
 * {@code tamboui.default-screen} or, if not set, the first discovered screen.
 */
public class ScreenAutoDiscovery implements SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(ScreenAutoDiscovery.class);

    private final ApplicationContext applicationContext;
    private final NavigationRouter navigationRouter;
    private final TamboUiProperties properties;

    /**
     * Creates a new ScreenAutoDiscovery.
     *
     * @param applicationContext the Spring application context to scan for beans
     * @param navigationRouter   the navigation router to register screens on
     * @param properties         the TamboUI configuration properties
     */
    public ScreenAutoDiscovery(ApplicationContext applicationContext,
                               NavigationRouter navigationRouter,
                               TamboUiProperties properties) {
        this.applicationContext = applicationContext;
        this.navigationRouter = navigationRouter;
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> screenBeans = applicationContext.getBeansWithAnnotation(TamboScreen.class);

        if (screenBeans.isEmpty()) {
            log.info("No @TamboScreen beans found — skipping auto-discovery");
            return;
        }

        String firstScreenName = null;

        for (Map.Entry<String, Object> entry : screenBeans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();

            if (!(bean instanceof ScreenController controller)) {
                log.warn("@TamboScreen bean '{}' ({}) does not implement ScreenController — skipping",
                        beanName, bean.getClass().getName());
                continue;
            }

            TamboScreen annotation = bean.getClass().getAnnotation(TamboScreen.class);
            if (annotation == null) {
                // Might happen with CGLIB proxies; try to find annotation on superclass
                annotation = findTamboScreenAnnotation(bean.getClass());
                if (annotation == null) {
                    log.warn("Could not find @TamboScreen annotation on bean '{}' — skipping", beanName);
                    continue;
                }
            }

            String screenName = resolveScreenName(annotation, beanName);
            String templateName = annotation.template();

            navigationRouter.registerScreen(screenName, controller, templateName);
            log.info("Auto-discovered screen '{}' -> template '{}' (bean: {})",
                    screenName, templateName, beanName);

            if (firstScreenName == null) {
                firstScreenName = screenName;
            }
        }

        navigateToInitialScreen(firstScreenName);
    }

    /**
     * Resolves the screen name from the annotation value or falls back to the bean name.
     */
    private String resolveScreenName(TamboScreen annotation, String beanName) {
        String value = annotation.value();
        if (value != null && !value.isBlank()) {
            return value;
        }
        return beanName;
    }

    /**
     * Navigates to the configured default screen or the first discovered screen.
     */
    private void navigateToInitialScreen(String firstScreenName) {
        String defaultScreen = properties.getDefaultScreen();

        if (defaultScreen != null && !defaultScreen.isBlank()) {
            if (navigationRouter.hasScreen(defaultScreen)) {
                navigationRouter.navigateTo(defaultScreen);
                log.info("Navigated to configured default screen '{}'", defaultScreen);
            } else {
                log.warn("Configured default screen '{}' not found. Available screens: {}",
                        defaultScreen, navigationRouter.getRegisteredScreens());
                if (firstScreenName != null) {
                    navigationRouter.navigateTo(firstScreenName);
                    log.info("Fell back to first discovered screen '{}'", firstScreenName);
                }
            }
        } else if (firstScreenName != null) {
            navigationRouter.navigateTo(firstScreenName);
            log.info("No default screen configured — navigated to first discovered screen '{}'",
                    firstScreenName);
        }
    }

    /**
     * Walks the class hierarchy to find the {@link TamboScreen} annotation,
     * which may be on a superclass when the bean is a CGLIB proxy.
     */
    private TamboScreen findTamboScreenAnnotation(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            TamboScreen annotation = current.getAnnotation(TamboScreen.class);
            if (annotation != null) {
                return annotation;
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
