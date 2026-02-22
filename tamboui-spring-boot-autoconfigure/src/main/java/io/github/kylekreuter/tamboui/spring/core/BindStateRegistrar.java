package io.github.kylekreuter.tamboui.spring.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * Scans Spring beans annotated with {@link TamboScreen} for fields annotated
 * with {@link BindState} and automatically registers them as state bindings
 * in the {@link TemplateModel} before each render cycle.
 * <p>
 * This eliminates the need to manually call {@code model.bindState()} in
 * {@code populate()} for annotated fields.
 *
 * @see BindState
 */
public class BindStateRegistrar implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(BindStateRegistrar.class);

    private final Map<Object, List<StateBinding>> bindingsByBean = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();

        // Only scan @TamboScreen beans
        if (findTamboScreenAnnotation(targetClass) == null) {
            return bean;
        }

        List<StateBinding> bindings = new ArrayList<>();

        ReflectionUtils.doWithFields(targetClass, field -> {
            BindState bindState = field.getAnnotation(BindState.class);
            if (bindState == null) {
                return;
            }

            String bindingName = bindState.value().isEmpty() ? field.getName() : bindState.value();
            bindings.add(new StateBinding(field, bindingName));
            log.debug("Discovered @BindState(\"{}\") on {}.{}",
                    bindingName, targetClass.getSimpleName(), field.getName());
        });

        if (!bindings.isEmpty()) {
            bindingsByBean.put(bean, bindings);
            log.info("Registered {} @BindState field(s) on {}",
                    bindings.size(), targetClass.getSimpleName());
        }

        return bean;
    }

    /**
     * Reads all {@link BindState}-annotated fields from the given controller
     * and registers their values as state bindings on the model.
     * <p>
     * Called before {@code controller.populate(model)} in the render pipeline.
     *
     * @param controller the screen controller instance
     * @param model      the template model to bind states into
     */
    public void autoBindStates(Object controller, TemplateModel model) {
        List<StateBinding> bindings = bindingsByBean.get(controller);
        if (bindings == null || bindings.isEmpty()) {
            return;
        }

        for (StateBinding binding : bindings) {
            try {
                binding.field().setAccessible(true);
                Object value = binding.field().get(controller);
                if (value != null) {
                    model.bindState(binding.bindingName(), value);
                } else {
                    log.warn("@BindState field '{}' on {} is null, skipping",
                            binding.bindingName(), controller.getClass().getSimpleName());
                }
            } catch (IllegalAccessException e) {
                log.error("Cannot access @BindState field '{}' on {}",
                        binding.bindingName(), controller.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Returns whether the given controller has any {@link BindState} bindings.
     *
     * @param controller the controller to check
     * @return {@code true} if the controller has at least one binding
     */
    public boolean hasBindings(Object controller) {
        List<StateBinding> bindings = bindingsByBean.get(controller);
        return bindings != null && !bindings.isEmpty();
    }

    /**
     * Returns an unmodifiable view of bindings for a controller (for testing).
     *
     * @param controller the controller
     * @return the list of bindings, or empty list
     */
    List<StateBinding> getBindings(Object controller) {
        List<StateBinding> bindings = bindingsByBean.get(controller);
        return bindings != null ? Collections.unmodifiableList(bindings) : Collections.emptyList();
    }

    /**
     * Walks the class hierarchy to find the {@link TamboScreen} annotation.
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
     * Holds a discovered {@code @BindState} binding: the field and the binding name.
     *
     * @param field       the annotated field
     * @param bindingName the binding key (field name or explicit value)
     */
    record StateBinding(Field field, String bindingName) {
    }
}
