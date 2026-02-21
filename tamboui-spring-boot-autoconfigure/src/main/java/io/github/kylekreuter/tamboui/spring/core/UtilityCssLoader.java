package io.github.kylekreuter.tamboui.spring.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import dev.tamboui.css.StyleEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the TamboUI utility CSS stylesheet from the classpath and registers
 * it with the {@link StyleEngine}.
 * <p>
 * The utility stylesheet provides Tailwind-like CSS classes (e.g. {@code .text-red},
 * {@code .bold}, {@code .p-2}, {@code .flex-center}) that can be used in
 * TamboUI templates via the {@code class} attribute.
 * <p>
 * This loader reads the CSS content eagerly during construction and provides
 * it to the {@link StyleEngine} when {@link #loadInto(StyleEngine)} is called.
 * Integration with the TamboUI lifecycle is handled by
 * {@link TamboUiStyleConfigurer}, which calls this loader once the
 * {@link dev.tamboui.toolkit.app.ToolkitRunner} is ready.
 *
 * @see StyleEngine
 * @see TamboUiStyleConfigurer
 */
public class UtilityCssLoader {

    private static final Logger log = LoggerFactory.getLogger(UtilityCssLoader.class);

    private final String cssContent;
    private final String resourcePath;

    /**
     * Creates a new UtilityCssLoader that reads the utility CSS from the given classpath resource.
     *
     * @param classpathResource the classpath resource path (e.g. {@code META-INF/tamboui-spring/utility.tcss})
     * @throws IllegalStateException if the resource cannot be found or read
     */
    public UtilityCssLoader(String classpathResource) {
        this.resourcePath = classpathResource;
        this.cssContent = readResource(classpathResource);
        log.debug("Loaded utility CSS from classpath: {} ({} chars)", classpathResource, cssContent.length());
    }

    /**
     * Loads the utility CSS into the given {@link StyleEngine} as an inline stylesheet.
     *
     * @param styleEngine the style engine to load the CSS into
     */
    public void loadInto(StyleEngine styleEngine) {
        if (cssContent.isEmpty()) {
            log.warn("Utility CSS is empty, skipping registration with StyleEngine");
            return;
        }
        styleEngine.addStylesheet(cssContent);
        log.info("Registered utility CSS ({} chars) with StyleEngine from {}", cssContent.length(), resourcePath);
    }

    /**
     * Returns the raw CSS content that was loaded from the classpath.
     *
     * @return the CSS content string, never {@code null}
     */
    public String getCssContent() {
        return cssContent;
    }

    /**
     * Returns the classpath resource path this loader was configured with.
     *
     * @return the resource path
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * Reads a classpath resource into a String.
     *
     * @param classpathResource the resource path
     * @return the resource content as a UTF-8 string
     * @throws IllegalStateException if the resource is not found or cannot be read
     */
    private static String readResource(String classpathResource) {
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IllegalStateException(
                        "Utility CSS resource not found on classpath: " + classpathResource);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read utility CSS from classpath: " + classpathResource, e);
        }
    }
}
