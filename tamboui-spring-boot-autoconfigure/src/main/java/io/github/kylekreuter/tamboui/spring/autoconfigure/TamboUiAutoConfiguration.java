package io.github.kylekreuter.tamboui.spring.autoconfigure;

import dev.tamboui.toolkit.app.ToolkitRunner;

import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.OnKeyRegistrar;
import io.github.kylekreuter.tamboui.spring.core.OnSubmitRegistrar;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.ToolkitRunnerFactory;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import io.github.kylekreuter.tamboui.spring.template.TemplateCache;
import io.github.kylekreuter.tamboui.spring.template.TemplateEngine;
import io.github.kylekreuter.tamboui.spring.template.tags.FormTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.InputTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

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
    public PanelTagHandler panelTagHandler() {
        return new PanelTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TextTagHandler textTagHandler() {
        return new TextTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateEngine templateEngine(TemplateCache templateCache,
                                         TamboUiProperties properties,
                                         List<TagHandler> tagHandlers) {
        return new TemplateEngine(templateCache, properties, tagHandlers);
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

    @Bean
    @ConditionalOnMissingBean
    public FormTagHandler formTagHandler() {
        return new FormTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public InputTagHandler inputTagHandler() {
        return new InputTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public OnKeyRegistrar onKeyRegistrar(TamboSpringApp tamboSpringApp) {
        return new OnKeyRegistrar(tamboSpringApp);
    }

    @Bean
    @ConditionalOnMissingBean
    public OnSubmitRegistrar onSubmitRegistrar() {
        return new OnSubmitRegistrar();
    }
}
