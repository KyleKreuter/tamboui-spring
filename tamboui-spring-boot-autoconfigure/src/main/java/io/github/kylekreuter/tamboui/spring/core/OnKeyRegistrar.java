package io.github.kylekreuter.tamboui.spring.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.toolkit.event.EventRouter;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyEvent;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.KeyBindingParser.ParsedBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.ReflectionUtils;

/**
 * Scans Spring beans for {@link OnKey}-annotated methods and registers them
 * as global key event handlers on the TamboUI {@link EventRouter}.
 * <p>
 * Implements {@link BeanPostProcessor} to discover bindings during bean
 * initialization, and {@link SmartLifecycle} to register the handlers
 * once the {@link TamboSpringApp} has started and a {@link ToolkitRunner}
 * is available.
 * <p>
 * If a bean is annotated with {@link TamboScreen @TamboScreen}, its key bindings
 * are scoped to that screen -- they only fire when the screen is active in the
 * {@link NavigationRouter}. Beans without {@code @TamboScreen} have global key
 * bindings that fire regardless of the active screen.
 * <p>
 * Lifecycle phase is {@code Integer.MAX_VALUE - 2}, which runs before
 * {@link TamboSpringApp} (phase {@code Integer.MAX_VALUE - 1}).
 */
public class OnKeyRegistrar implements BeanPostProcessor, SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(OnKeyRegistrar.class);

    private final TamboSpringApp tamboSpringApp;
    private final NavigationRouter navigationRouter;
    private final List<KeyBinding> bindings = new ArrayList<>();
    private volatile boolean running = false;

    /**
     * Creates a new OnKeyRegistrar.
     *
     * @param tamboSpringApp   the TamboUI Spring application providing the {@link ToolkitRunner}
     * @param navigationRouter the navigation router for screen-scoped key bindings
     */
    public OnKeyRegistrar(TamboSpringApp tamboSpringApp, NavigationRouter navigationRouter) {
        this.tamboSpringApp = tamboSpringApp;
        this.navigationRouter = navigationRouter;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();

        // Check if the bean has @TamboScreen for screen-scoping
        String screenName = resolveScreenName(targetClass, beanName);

        ReflectionUtils.doWithMethods(targetClass, method -> {
            OnKey onKey = method.getAnnotation(OnKey.class);
            if (onKey == null) {
                return;
            }

            validateMethod(method, beanName);

            try {
                ParsedBinding parsed = KeyBindingParser.parse(onKey.value());
                bindings.add(new KeyBinding(bean, method, parsed, onKey.value(), screenName));
                if (screenName != null) {
                    log.debug("Discovered @OnKey(\"{}\") on {}.{}() scoped to screen '{}'",
                            onKey.value(), targetClass.getSimpleName(), method.getName(), screenName);
                } else {
                    log.debug("Discovered @OnKey(\"{}\") on {}.{}() (global)",
                            onKey.value(), targetClass.getSimpleName(), method.getName());
                }
            } catch (IllegalArgumentException e) {
                throw new BeansException(
                        "Invalid @OnKey value \"%s\" on method %s.%s(): %s"
                                .formatted(onKey.value(), targetClass.getSimpleName(), method.getName(),
                                        e.getMessage())) {
                };
            }
        });

        return bean;
    }

    /**
     * Resolves the screen name for a bean class by checking for {@link TamboScreen}.
     * Returns {@code null} if the bean is not annotated with {@code @TamboScreen},
     * meaning its key bindings will be global.
     */
    private String resolveScreenName(Class<?> beanClass, String beanName) {
        TamboScreen tamboScreen = findTamboScreenAnnotation(beanClass);
        if (tamboScreen == null) {
            return null;
        }
        String value = tamboScreen.value();
        if (value != null && !value.isBlank()) {
            return value;
        }
        return beanName;
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

    /**
     * Validates that the annotated method has an acceptable signature.
     * Allowed signatures: no parameters, or a single {@link KeyEvent} parameter.
     */
    private void validateMethod(Method method, String beanName) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 1) {
            throw new IllegalStateException(
                    "@OnKey method %s.%s() must have zero parameters or a single KeyEvent parameter, but has %d"
                            .formatted(method.getDeclaringClass().getSimpleName(), method.getName(),
                                    paramTypes.length));
        }
        if (paramTypes.length == 1 && !KeyEvent.class.isAssignableFrom(paramTypes[0])) {
            throw new IllegalStateException(
                    "@OnKey method %s.%s() parameter must be of type KeyEvent, but is %s"
                            .formatted(method.getDeclaringClass().getSimpleName(), method.getName(),
                                    paramTypes[0].getSimpleName()));
        }
    }

    @Override
    public void start() {
        if (running || bindings.isEmpty()) {
            return;
        }
        running = true;

        log.info("Found {} @OnKey binding(s), registering runner-ready callback", bindings.size());

        tamboSpringApp.onRunnerReady(runner -> {
            registerHandlers(runner.eventRouter());
        });
    }

    /**
     * Registers all collected bindings on the given {@link EventRouter}.
     * Called externally (e.g. by TamboSpringApp) once the runner is available.
     *
     * @param eventRouter the event router to register handlers on
     */
    public void registerHandlers(EventRouter eventRouter) {
        for (KeyBinding binding : bindings) {
            eventRouter.addGlobalHandler(event -> handleEvent(event, binding));
            if (binding.screenName() != null) {
                log.info("Registered @OnKey(\"{}\") handler: {}.{}() (screen: {})",
                        binding.keyExpression(),
                        binding.bean().getClass().getSimpleName(),
                        binding.method().getName(),
                        binding.screenName());
            } else {
                log.info("Registered @OnKey(\"{}\") handler: {}.{}() (global)",
                        binding.keyExpression(),
                        binding.bean().getClass().getSimpleName(),
                        binding.method().getName());
            }
        }
    }

    /**
     * Returns whether any bindings have been discovered.
     *
     * @return {@code true} if at least one {@code @OnKey} binding exists
     */
    public boolean hasBindings() {
        return !bindings.isEmpty();
    }

    /**
     * Returns an unmodifiable view of the discovered bindings (for testing).
     *
     * @return the list of discovered key bindings
     */
    List<KeyBinding> getBindings() {
        return List.copyOf(bindings);
    }

    private EventResult handleEvent(Event event, KeyBinding binding) {
        if (!(event instanceof KeyEvent keyEvent)) {
            return EventResult.UNHANDLED;
        }

        if (!binding.parsed().matches(keyEvent)) {
            return EventResult.UNHANDLED;
        }

        // Screen-scoped bindings only fire when their screen is active
        if (binding.screenName() != null) {
            String activeScreen = navigationRouter.getActiveScreen();
            if (!binding.screenName().equals(activeScreen)) {
                return EventResult.UNHANDLED;
            }
        }

        try {
            Method method = binding.method();
            method.setAccessible(true);

            if (method.getParameterCount() == 0) {
                method.invoke(binding.bean());
            } else {
                method.invoke(binding.bean(), keyEvent);
            }
            return EventResult.HANDLED;
        } catch (Exception e) {
            log.error("Error invoking @OnKey(\"{}\") handler {}.{}()",
                    binding.keyExpression(),
                    binding.bean().getClass().getSimpleName(),
                    binding.method().getName(), e);
            return EventResult.UNHANDLED;
        }
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        // Start before TamboSpringApp (Integer.MAX_VALUE - 1)
        return Integer.MAX_VALUE - 2;
    }

    /**
     * Holds a discovered {@code @OnKey} binding: the bean, method, parsed key, original expression,
     * and optional screen name for scoping.
     *
     * @param bean          the bean instance owning the method
     * @param method        the annotated method
     * @param parsed        the parsed key binding
     * @param keyExpression the original {@code @OnKey} value string
     * @param screenName    the screen name from {@code @TamboScreen} for scoping, or {@code null} for global bindings
     */
    record KeyBinding(Object bean, Method method, ParsedBinding parsed, String keyExpression, String screenName) {
    }
}
