package io.github.kylekreuter.tamboui.spring.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import dev.tamboui.widgets.form.FormState;

import io.github.kylekreuter.tamboui.spring.annotation.OnSubmit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link OnSubmitRegistrar}.
 */
class OnSubmitRegistrarTest {

    private OnSubmitRegistrar registrar;

    @BeforeEach
    void setUp() {
        registrar = new OnSubmitRegistrar();
    }

    @Test
    @DisplayName("should discover @OnSubmit-annotated methods on beans")
    void shouldDiscoverAnnotatedMethods() {
        SampleFormBean bean = new SampleFormBean();

        registrar.postProcessAfterInitialization(bean, "sampleFormBean");

        assertThat(registrar.hasBindings()).isTrue();
        assertThat(registrar.getBindings()).hasSize(1);
    }

    @Test
    @DisplayName("should discover multiple @OnSubmit methods on a single bean")
    void shouldDiscoverMultipleAnnotatedMethods() {
        MultiFormBean bean = new MultiFormBean();

        registrar.postProcessAfterInitialization(bean, "multiFormBean");

        assertThat(registrar.getBindings()).hasSize(2);
    }

    @Test
    @DisplayName("should not discover beans without @OnSubmit methods")
    void shouldIgnoreBeansWithoutAnnotation() {
        PlainBean bean = new PlainBean();

        registrar.postProcessAfterInitialization(bean, "plainBean");

        assertThat(registrar.hasBindings()).isFalse();
    }

    @Test
    @DisplayName("should throw on method with too many parameters")
    void shouldThrowOnMethodWithTooManyParams() {
        TooManyParamsBean bean = new TooManyParamsBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "tooManyParams"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("zero parameters or a single FormState parameter");
    }

    @Test
    @DisplayName("should throw on method with wrong parameter type")
    void shouldThrowOnWrongParamType() {
        WrongParamTypeBean bean = new WrongParamTypeBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "wrongParam"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be of type FormState");
    }

    @Test
    @DisplayName("should throw on blank @OnSubmit value")
    void shouldThrowOnBlankValue() {
        BlankValueBean bean = new BlankValueBean();

        assertThatThrownBy(() -> registrar.postProcessAfterInitialization(bean, "blankValue"))
                .isInstanceOf(org.springframework.beans.BeansException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    @DisplayName("should invoke no-arg submit handler when form is submitted")
    void shouldInvokeNoArgHandler() {
        NoArgSubmitBean bean = new NoArgSubmitBean();
        registrar.postProcessAfterInitialization(bean, "noArgSubmitBean");

        FormState formState = FormState.builder().textField("test").build();
        registrar.fireSubmit("myForm", formState);

        assertThat(bean.submitted).isTrue();
    }

    @Test
    @DisplayName("should invoke handler with FormState parameter when form is submitted")
    void shouldInvokeHandlerWithFormState() {
        SampleFormBean bean = new SampleFormBean();
        registrar.postProcessAfterInitialization(bean, "sampleFormBean");

        FormState formState = FormState.builder()
                .textField("username", "Alice")
                .build();
        registrar.fireSubmit("settingsForm", formState);

        assertThat(bean.receivedState).isSameAs(formState);
    }

    @Test
    @DisplayName("should not invoke handler for different form name")
    void shouldNotInvokeForDifferentForm() {
        SampleFormBean bean = new SampleFormBean();
        registrar.postProcessAfterInitialization(bean, "sampleFormBean");

        FormState formState = FormState.builder().textField("test").build();
        registrar.fireSubmit("otherForm", formState);

        assertThat(bean.receivedState).isNull();
    }

    @Test
    @DisplayName("should invoke multiple handlers for the same form name")
    void shouldInvokeMultipleHandlersForSameForm() {
        NoArgSubmitBean bean1 = new NoArgSubmitBean();
        AnotherMyFormBean bean2 = new AnotherMyFormBean();

        registrar.postProcessAfterInitialization(bean1, "bean1");
        registrar.postProcessAfterInitialization(bean2, "bean2");

        registrar.fireSubmit("myForm", null);

        assertThat(bean1.submitted).isTrue();
        assertThat(bean2.submitted).isTrue();
    }

    @Test
    @DisplayName("getBindingsForForm should return only matching bindings")
    void getBindingsForFormShouldFilter() {
        MultiFormBean bean = new MultiFormBean();
        registrar.postProcessAfterInitialization(bean, "multiFormBean");

        List<OnSubmitRegistrar.SubmitBinding> formABindings = registrar.getBindingsForForm("formA");
        List<OnSubmitRegistrar.SubmitBinding> formBBindings = registrar.getBindingsForForm("formB");

        assertThat(formABindings).hasSize(1);
        assertThat(formBBindings).hasSize(1);
    }

    @Test
    @DisplayName("hasBindings should return false initially")
    void hasBindingsFalseInitially() {
        assertThat(registrar.hasBindings()).isFalse();
    }

    @Test
    @DisplayName("getBindings should return empty list initially")
    void getBindingsEmptyInitially() {
        assertThat(registrar.getBindings()).isEmpty();
    }

    @Test
    @DisplayName("postProcessAfterInitialization should return the same bean instance")
    void shouldReturnSameBeanInstance() {
        SampleFormBean bean = new SampleFormBean();

        Object result = registrar.postProcessAfterInitialization(bean, "sampleFormBean");

        assertThat(result).isSameAs(bean);
    }

    @Test
    @DisplayName("should discover bindings from multiple beans")
    void shouldDiscoverBindingsFromMultipleBeans() {
        SampleFormBean bean1 = new SampleFormBean();
        NoArgSubmitBean bean2 = new NoArgSubmitBean();

        registrar.postProcessAfterInitialization(bean1, "bean1");
        registrar.postProcessAfterInitialization(bean2, "bean2");

        assertThat(registrar.getBindings()).hasSize(2);
    }

    @Test
    @DisplayName("fireSubmit with null formState should work for no-arg handler")
    void fireSubmitWithNullFormState() {
        NoArgSubmitBean bean = new NoArgSubmitBean();
        registrar.postProcessAfterInitialization(bean, "bean");

        registrar.fireSubmit("myForm", null);

        assertThat(bean.submitted).isTrue();
    }

    // --- Test beans ---

    static class SampleFormBean {
        FormState receivedState = null;

        @OnSubmit("settingsForm")
        void onSave(FormState state) {
            receivedState = state;
        }
    }

    static class NoArgSubmitBean {
        boolean submitted = false;

        @OnSubmit("myForm")
        void onSubmit() {
            submitted = true;
        }
    }

    static class MultiFormBean {
        @OnSubmit("formA")
        void onFormA() {}

        @OnSubmit("formB")
        void onFormB(FormState state) {}
    }

    static class PlainBean {
        void someMethod() {}
    }

    static class TooManyParamsBean {
        @OnSubmit("form")
        void onSubmit(FormState state, String extra) {}
    }

    static class WrongParamTypeBean {
        @OnSubmit("form")
        void onSubmit(String notFormState) {}
    }

    static class BlankValueBean {
        @OnSubmit("   ")
        void onSubmit() {}
    }

    static class AnotherMyFormBean {
        boolean submitted = false;

        @OnSubmit("myForm")
        void onSubmit() {
            submitted = true;
        }
    }
}
