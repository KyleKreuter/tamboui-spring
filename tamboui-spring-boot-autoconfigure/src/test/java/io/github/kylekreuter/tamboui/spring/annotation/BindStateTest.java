package io.github.kylekreuter.tamboui.spring.annotation;

import dev.tamboui.widgets.select.SelectState;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link BindState} annotation.
 */
class BindStateTest {

    // Test class with annotated fields
    static class TestController {
        @BindState
        SelectState defaultName = new SelectState("A", "B");

        @BindState("customKey")
        SelectState explicitName = new SelectState("X", "Y");

        SelectState notAnnotated = new SelectState("P", "Q");
    }

    @Test
    @DisplayName("@BindState is retained at runtime")
    void retainedAtRuntime() {
        Retention retention = BindState.class.getAnnotation(Retention.class);
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }

    @Test
    @DisplayName("@BindState targets fields")
    void targetsFields() {
        Target target = BindState.class.getAnnotation(Target.class);
        assertThat(target).isNotNull();
        assertThat(target.value()).containsExactly(ElementType.FIELD);
    }

    @Test
    @DisplayName("@BindState is documented")
    void isDocumented() {
        assertThat(BindState.class.isAnnotationPresent(Documented.class)).isTrue();
    }

    @Test
    @DisplayName("default value is empty string")
    void defaultValueIsEmpty() throws Exception {
        Field field = TestController.class.getDeclaredField("defaultName");
        BindState annotation = field.getAnnotation(BindState.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEmpty();
    }

    @Test
    @DisplayName("explicit value is preserved")
    void explicitValuePreserved() throws Exception {
        Field field = TestController.class.getDeclaredField("explicitName");
        BindState annotation = field.getAnnotation(BindState.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo("customKey");
    }

    @Test
    @DisplayName("non-annotated fields have no @BindState")
    void nonAnnotatedFieldsClean() throws Exception {
        Field field = TestController.class.getDeclaredField("notAnnotated");
        BindState annotation = field.getAnnotation(BindState.class);

        assertThat(annotation).isNull();
    }

    @Test
    @DisplayName("annotation is present on annotated field")
    void presentOnAnnotatedField() throws Exception {
        Field field = TestController.class.getDeclaredField("defaultName");

        assertThat(field.isAnnotationPresent(BindState.class)).isTrue();
    }
}
