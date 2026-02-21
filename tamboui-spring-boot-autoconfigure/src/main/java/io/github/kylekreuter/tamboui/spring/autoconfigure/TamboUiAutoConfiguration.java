package io.github.kylekreuter.tamboui.spring.autoconfigure;

import dev.tamboui.toolkit.app.ToolkitRunner;

import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.ToolkitRunnerFactory;
import io.github.kylekreuter.tamboui.spring.template.TemplateCache;
import io.github.kylekreuter.tamboui.spring.template.TemplateEngine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for TamboUI Spring Boot integration.
 * <p>
 * Activates when TamboUI's Toolkit class is on the classpath.
 */
@AutoConfiguration
@ConditionalOnClass(name = "dev.tamboui.toolkit.Toolkit")
@EnableConfigurationProperties(TamboUiProperties.class)
public class TamboUiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TemplateCache templateCache() {
        return new TemplateCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateEngine templateEngine(TemplateCache templateCache, TamboUiProperties properties) {
        return new TemplateEngine(templateCache, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NavigationRouter navigationRouter() {
        return new NavigationRouter();
    }

    /**
     * Default factory that creates a {@link ToolkitRunner} with default configuration.
     * <p>
     * Users can override this bean to provide custom TuiConfig or builder settings.
     */
    @Bean
    @ConditionalOnMissingBean
    public ToolkitRunnerFactory toolkitRunnerFactory() {
        return ToolkitRunner::create;
    }

    @Bean
    @ConditionalOnMissingBean
    public TamboSpringApp tamboSpringApp(ToolkitRunnerFactory toolkitRunnerFactory) {
        return new TamboSpringApp(toolkitRunnerFactory);
    }
}
