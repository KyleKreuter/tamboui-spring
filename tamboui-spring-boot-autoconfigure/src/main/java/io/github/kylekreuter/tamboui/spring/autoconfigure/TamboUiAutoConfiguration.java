package io.github.kylekreuter.tamboui.spring.autoconfigure;

import dev.tamboui.toolkit.app.ToolkitRunner;

import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.OnKeyRegistrar;
import io.github.kylekreuter.tamboui.spring.core.OnSubmitRegistrar;
import io.github.kylekreuter.tamboui.spring.core.ScreenAutoDiscovery;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.TamboUiStyleConfigurer;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;
import io.github.kylekreuter.tamboui.spring.core.ToolkitRunnerFactory;
import io.github.kylekreuter.tamboui.spring.core.UtilityCssLoader;
import io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter;
import io.github.kylekreuter.tamboui.spring.template.TagHandler;
import io.github.kylekreuter.tamboui.spring.template.TemplateCache;
import io.github.kylekreuter.tamboui.spring.template.TemplateEngine;
import io.github.kylekreuter.tamboui.spring.template.tags.ColTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ColumnTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.DockTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.FormTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.GridTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.InputTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ItemTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.ListTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.RowTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.SpacerTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TableTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
    public ListTagHandler listTagHandler() {
        return new ListTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ItemTagHandler itemTagHandler() {
        return new ItemTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TableTagHandler tableTagHandler() {
        return new TableTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ColTagHandler colTagHandler() {
        return new ColTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public DockTagHandler dockTagHandler() {
        return new DockTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public GridTagHandler gridTagHandler() {
        return new GridTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RowTagHandler rowTagHandler() {
        return new RowTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ColumnTagHandler columnTagHandler() {
        return new ColumnTagHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpacerTagHandler spacerTagHandler() {
        return new SpacerTagHandler();
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
    public WidgetToElementConverter widgetToElementConverter() {
        return new WidgetToElementConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public TamboSpringApp tamboSpringApp(ToolkitRunnerFactory toolkitRunnerFactory,
                                          NavigationRouter navigationRouter,
                                          TemplateEngine templateEngine,
                                          WidgetToElementConverter converter) {
        return new TamboSpringApp(toolkitRunnerFactory, () -> {
            // Read activeScreen once to avoid race conditions between
            // controller and template resolution during screen transitions
            String screen = navigationRouter.getActiveScreen();
            if (screen == null) {
                return dev.tamboui.toolkit.Toolkit.text("No screen active");
            }
            ScreenController controller = navigationRouter.getController(screen);
            String templateName = navigationRouter.getTemplateName(screen);
            if (controller == null || templateName == null) {
                return dev.tamboui.toolkit.Toolkit.text("No screen active");
            }
            TemplateModel model = new TemplateModel();
            controller.populate(model);
            Object widget = templateEngine.render(templateName, model.asMap());
            if (widget == null) {
                return dev.tamboui.toolkit.Toolkit.text("Empty template: " + templateName);
            }
            return converter.convert(widget, model.stateBindings());
        });
    }

    /**
     * Loads the utility CSS stylesheet from the classpath.
     * <p>
     * The resource path is configurable via {@code tamboui.utility-css} property
     * (default: {@code META-INF/tamboui-spring/utility.tcss}).
     */
    @Bean
    @ConditionalOnMissingBean
    public UtilityCssLoader utilityCssLoader(TamboUiProperties properties) {
        return new UtilityCssLoader(properties.getUtilityCss());
    }

    /**
     * Configures TamboUI styling by loading the utility CSS into the
     * {@link dev.tamboui.css.engine.StyleEngine} when the ToolkitRunner becomes ready.
     */
    @Bean
    @ConditionalOnMissingBean
    public TamboUiStyleConfigurer tamboUiStyleConfigurer(UtilityCssLoader utilityCssLoader,
                                                          TamboSpringApp tamboSpringApp) {
        return new TamboUiStyleConfigurer(utilityCssLoader, tamboSpringApp);
    }

    @Bean
    @ConditionalOnMissingBean
    public OnKeyRegistrar onKeyRegistrar(TamboSpringApp tamboSpringApp,
                                         NavigationRouter navigationRouter) {
        return new OnKeyRegistrar(tamboSpringApp, navigationRouter);
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
    public ScreenAutoDiscovery screenAutoDiscovery(ApplicationContext applicationContext,
                                                    NavigationRouter navigationRouter,
                                                    TamboUiProperties properties) {
        return new ScreenAutoDiscovery(applicationContext, navigationRouter, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public OnSubmitRegistrar onSubmitRegistrar() {
        return new OnSubmitRegistrar();
    }

}
