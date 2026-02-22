package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.select.SelectState;

import io.github.kylekreuter.tamboui.spring.annotation.BindState;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BindStateRegistrar}.
 */
class BindStateRegistrarTest {

    private BindStateRegistrar registrar;

    @BeforeEach
    void setUp() {
        registrar = new BindStateRegistrar();
    }

    // ==================== Test Beans ====================

    @TamboScreen(template = "test")
    static class ControllerWithBindState implements ScreenController {
        @BindState
        SelectState languageSelect = new SelectState("Deutsch", "English");

        @BindState
        TextInputState searchInput = new TextInputState();

        @Override
        public void populate(TemplateModel model) {
        }
    }

    @TamboScreen(template = "test")
    static class ControllerWithExplicitName implements ScreenController {
        @BindState("mySelect")
        SelectState languageSelect = new SelectState("A", "B");

        @Override
        public void populate(TemplateModel model) {
        }
    }

    @TamboScreen(template = "test")
    static class ControllerWithNullField implements ScreenController {
        @BindState
        SelectState nullSelect = null;

        @Override
        public void populate(TemplateModel model) {
        }
    }

    @TamboScreen(template = "test")
    static class ControllerWithoutBindState implements ScreenController {
        SelectState notAnnotated = new SelectState("X", "Y");

        @Override
        public void populate(TemplateModel model) {
        }
    }

    static class NonScreenBean {
        @BindState
        SelectState ignored = new SelectState("A", "B");
    }

    // ==================== Tests ====================

    @Nested
    @DisplayName("postProcessAfterInitialization")
    class PostProcess {

        @Test
        @DisplayName("discovers @BindState fields on @TamboScreen beans")
        void discoversAnnotatedFields() {
            var controller = new ControllerWithBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            assertThat(registrar.hasBindings(controller)).isTrue();
            assertThat(registrar.getBindings(controller)).hasSize(2);
        }

        @Test
        @DisplayName("uses field name as default binding name")
        void usesFieldNameAsDefault() {
            var controller = new ControllerWithBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            List<BindStateRegistrar.StateBinding> bindings = registrar.getBindings(controller);
            List<String> names = bindings.stream()
                    .map(BindStateRegistrar.StateBinding::bindingName)
                    .toList();
            assertThat(names).contains("languageSelect", "searchInput");
        }

        @Test
        @DisplayName("uses explicit value when provided")
        void usesExplicitValue() {
            var controller = new ControllerWithExplicitName();
            registrar.postProcessAfterInitialization(controller, "testBean");

            List<BindStateRegistrar.StateBinding> bindings = registrar.getBindings(controller);
            assertThat(bindings).hasSize(1);
            assertThat(bindings.get(0).bindingName()).isEqualTo("mySelect");
        }

        @Test
        @DisplayName("ignores beans without @TamboScreen")
        void ignoresNonScreenBeans() {
            var bean = new NonScreenBean();
            registrar.postProcessAfterInitialization(bean, "testBean");

            assertThat(registrar.hasBindings(bean)).isFalse();
        }

        @Test
        @DisplayName("ignores fields without @BindState")
        void ignoresNonAnnotatedFields() {
            var controller = new ControllerWithoutBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            assertThat(registrar.hasBindings(controller)).isFalse();
        }

        @Test
        @DisplayName("returns the bean unchanged")
        void returnsBeanUnchanged() {
            var controller = new ControllerWithBindState();
            Object result = registrar.postProcessAfterInitialization(controller, "testBean");

            assertThat(result).isSameAs(controller);
        }
    }

    @Nested
    @DisplayName("autoBindStates")
    class AutoBind {

        @Test
        @DisplayName("registers field values as state bindings in TemplateModel")
        void registersStateBindings() {
            var controller = new ControllerWithBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(controller, model);

            assertThat(model.getState("languageSelect")).isSameAs(controller.languageSelect);
            assertThat(model.getState("searchInput")).isSameAs(controller.searchInput);
        }

        @Test
        @DisplayName("uses explicit binding name from annotation")
        void usesExplicitBindingName() {
            var controller = new ControllerWithExplicitName();
            registrar.postProcessAfterInitialization(controller, "testBean");

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(controller, model);

            assertThat(model.getState("mySelect")).isSameAs(controller.languageSelect);
        }

        @Test
        @DisplayName("skips null field values without error")
        void skipsNullFields() {
            var controller = new ControllerWithNullField();
            registrar.postProcessAfterInitialization(controller, "testBean");

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(controller, model);

            assertThat(model.getState("nullSelect")).isNull();
        }

        @Test
        @DisplayName("does nothing for controllers without bindings")
        void noOpForUnregisteredControllers() {
            var controller = new ControllerWithoutBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(controller, model);

            assertThat(model.stateBindings()).isEmpty();
        }

        @Test
        @DisplayName("does nothing for unknown controllers")
        void noOpForUnknownControllers() {
            var unknownController = new ControllerWithBindState();
            // NOT registered via postProcessAfterInitialization

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(unknownController, model);

            assertThat(model.stateBindings()).isEmpty();
        }

        @Test
        @DisplayName("bindings are registered as references (not copies)")
        void bindingsAreReferences() {
            var controller = new ControllerWithBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            TemplateModel model = new TemplateModel();
            registrar.autoBindStates(controller, model);

            // Modify the controller field
            controller.languageSelect.selectNext();

            // The state in the model should reflect the change (same reference)
            SelectState boundState = (SelectState) model.getState("languageSelect");
            assertThat(boundState.selectedIndex()).isEqualTo(1);
            assertThat(boundState).isSameAs(controller.languageSelect);
        }
    }

    @Nested
    @DisplayName("hasBindings")
    class HasBindings {

        @Test
        @DisplayName("returns true for controllers with @BindState fields")
        void trueForAnnotatedControllers() {
            var controller = new ControllerWithBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            assertThat(registrar.hasBindings(controller)).isTrue();
        }

        @Test
        @DisplayName("returns false for controllers without @BindState fields")
        void falseForNonAnnotatedControllers() {
            var controller = new ControllerWithoutBindState();
            registrar.postProcessAfterInitialization(controller, "testBean");

            assertThat(registrar.hasBindings(controller)).isFalse();
        }

        @Test
        @DisplayName("returns false for unknown controllers")
        void falseForUnknownControllers() {
            assertThat(registrar.hasBindings(new Object())).isFalse();
        }
    }
}
