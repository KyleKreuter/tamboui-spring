package io.github.kylekreuter.tamboui.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for TamboUI Spring Boot.
 *
 * @see TamboUiAutoConfiguration
 */
@ConfigurationProperties(prefix = "tamboui")
public class TamboUiProperties {

    /**
     * Terminal backend to use. Default: jline3.
     */
    private String backend = "jline3";

    /**
     * Template file location prefix. Default: templates/.
     */
    private String templatePrefix = "templates/";

    /**
     * Template file suffix. Default: .ttl.
     */
    private String templateSuffix = ".ttl";

    /**
     * Path to the utility CSS stylesheet. Default: META-INF/tamboui-spring/utility.tcss.
     */
    private String utilityCss = "META-INF/tamboui-spring/utility.tcss";

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getTemplatePrefix() {
        return templatePrefix;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    public String getUtilityCss() {
        return utilityCss;
    }

    public void setUtilityCss(String utilityCss) {
        this.utilityCss = utilityCss;
    }
}
