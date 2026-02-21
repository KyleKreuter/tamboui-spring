package io.github.kylekreuter.tamboui.spring.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.tamboui.widgets.form.FormState;

import io.github.kylekreuter.tamboui.spring.annotation.OnSubmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * Scans Spring beans for {@link OnSubmit}-annotated methods and stores them
 * as submit handlers that can be invoked when a form is submitted.
 * <p>
 * Implements {@link BeanPostProcessor} to discover submit bindings during
 * bean initialization. Unlike {@link OnKeyRegistrar}, submit handlers are
 * invoked programmatically by the form infrastructure rather than through
 * global event routing.
 * <p>
 * Example usage:
 * <pre>{@code
 * @OnSubmit("settingsForm")
 * public void onSave(FormState state) {
 *     String username = state.textValue("username");
 *     // ...
 * }
 * }</pre>
 */
public class OnSubmitRegistrar implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(OnSubmitRegistrar.class);

    private final List<SubmitBinding> bindings = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();

        ReflectionUtils.doWithMethods(targetClass, method -> {
            OnSubmit onSubmit = method.getAnnotation(OnSubmit.class);
            if (onSubmit == null) {
                return;
            }

            validateMethod(method);

            String formName = onSubmit.value();
            if (formName.isBlank()) {
                throw new BeansException(
                        "@OnSubmit value must not be blank on method %s.%s()"
                                .formatted(targetClass.getSimpleName(), method.getName())) {
                };
            }

            bindings.add(new SubmitBinding(bean, method, formName));
            log.debug("Discovered @OnSubmit(\"{}\") on {}.{}()",
                    formName, targetClass.getSimpleName(), method.getName());
        });

        return bean;
    }

    /**
     * Validates that the annotated method has an acceptable signature.
     * Allowed signatures: no parameters, or a single {@link FormState} parameter.
     */
    private void validateMethod(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 1) {
            throw new IllegalStateException(
                    "@OnSubmit method %s.%s() must have zero parameters or a single FormState parameter, but has %d"
                            .formatted(method.getDeclaringClass().getSimpleName(), method.getName(),
                                    paramTypes.length));
        }
        if (paramTypes.length == 1 && !FormState.class.isAssignableFrom(paramTypes[0])) {
            throw new IllegalStateException(
                    "@OnSubmit method %s.%s() parameter must be of type FormState, but is %s"
                            .formatted(method.getDeclaringClass().getSimpleName(), method.getName(),
                                    paramTypes[0].getSimpleName()));
        }
    }

    /**
     * Invokes all submit handlers registered for the given form name.
     *
     * @param formName  the form binding name
     * @param formState the form state to pass to the handler (may be {@code null} for no-arg methods)
     */
    public void fireSubmit(String formName, FormState formState) {
        for (SubmitBinding binding : bindings) {
            if (binding.formName().equals(formName)) {
                invokeHandler(binding, formState);
            }
        }
    }

    /**
     * Returns whether any submit bindings have been discovered.
     *
     * @return {@code true} if at least one {@code @OnSubmit} binding exists
     */
    public boolean hasBindings() {
        return !bindings.isEmpty();
    }

    /**
     * Returns an unmodifiable view of the discovered bindings (for testing).
     *
     * @return the list of discovered submit bindings
     */
    List<SubmitBinding> getBindings() {
        return Collections.unmodifiableList(bindings);
    }

    /**
     * Returns all bindings for a specific form name (for testing).
     *
     * @param formName the form name to filter by
     * @return list of matching bindings
     */
    List<SubmitBinding> getBindingsForForm(String formName) {
        return bindings.stream()
                .filter(b -> b.formName().equals(formName))
                .toList();
    }

    private void invokeHandler(SubmitBinding binding, FormState formState) {
        try {
            Method method = binding.method();
            method.setAccessible(true);

            if (method.getParameterCount() == 0) {
                method.invoke(binding.bean());
            } else {
                method.invoke(binding.bean(), formState);
            }

            log.debug("Invoked @OnSubmit(\"{}\") handler {}.{}()",
                    binding.formName(),
                    binding.bean().getClass().getSimpleName(),
                    binding.method().getName());
        } catch (Exception e) {
            log.error("Error invoking @OnSubmit(\"{}\") handler {}.{}()",
                    binding.formName(),
                    binding.bean().getClass().getSimpleName(),
                    binding.method().getName(), e);
        }
    }

    /**
     * Holds a discovered {@code @OnSubmit} binding: the bean, method, and form name.
     *
     * @param bean     the bean instance owning the method
     * @param method   the annotated method
     * @param formName the form binding name from {@code @OnSubmit} value
     */
    record SubmitBinding(Object bean, Method method, String formName) {
    }
}
