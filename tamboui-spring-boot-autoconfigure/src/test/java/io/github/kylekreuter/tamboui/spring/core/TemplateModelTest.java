package io.github.kylekreuter.tamboui.spring.core;

import dev.tamboui.widgets.form.FormState;
import dev.tamboui.widgets.input.TextInputState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TemplateModel}.
 */
class TemplateModelTest {

    private TemplateModel model;

    @BeforeEach
    void setUp() {
        model = new TemplateModel();
    }

    @Nested
    @DisplayName("attributes (transient)")
    class AttributeTests {

        @Test
        @DisplayName("put and get should store and retrieve values")
        void putAndGet() {
            model.put("title", "Hello");

            assertThat(model.get("title")).isEqualTo("Hello");
        }

        @Test
        @DisplayName("get should return null for missing key")
        void getMissing() {
            assertThat(model.get("nonexistent")).isNull();
        }

        @Test
        @DisplayName("containsKey should return true for existing attribute")
        void containsKeyForAttribute() {
            model.put("key", "value");

            assertThat(model.containsKey("key")).isTrue();
        }

        @Test
        @DisplayName("containsKey should return false for missing key")
        void containsKeyMissing() {
            assertThat(model.containsKey("missing")).isFalse();
        }

        @Test
        @DisplayName("clear should remove all attributes")
        void clearRemovesAttributes() {
            model.put("a", 1);
            model.put("b", 2);

            model.clear();

            assertThat(model.get("a")).isNull();
            assertThat(model.get("b")).isNull();
        }

        @Test
        @DisplayName("asMap should return all attributes")
        void asMapReturnsAttributes() {
            model.put("x", "1");
            model.put("y", "2");

            Map<String, Object> map = model.asMap();

            assertThat(map).containsEntry("x", "1").containsEntry("y", "2");
        }

        @Test
        @DisplayName("put should support fluent chaining")
        void fluentChaining() {
            TemplateModel result = model.put("a", 1).put("b", 2);

            assertThat(result).isSameAs(model);
            assertThat(model.get("a")).isEqualTo(1);
            assertThat(model.get("b")).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("state bindings (persistent)")
    class StateBindingTests {

        @Test
        @DisplayName("bindState and getState should store and retrieve state objects")
        void bindAndGetState() {
            TextInputState state = new TextInputState("test");

            model.bindState("input", state);

            assertThat(model.getState("input")).isSameAs(state);
        }

        @Test
        @DisplayName("getState should return null for missing key")
        void getStateMissing() {
            assertThat(model.getState("nonexistent")).isNull();
        }

        @Test
        @DisplayName("state bindings should survive clear()")
        void stateSurvivesClear() {
            FormState formState = FormState.builder()
                    .textField("name", "Alice")
                    .build();

            model.bindState("form", formState);
            model.put("transient", "gone");

            model.clear();

            assertThat(model.getState("form")).isSameAs(formState);
            assertThat(model.get("transient")).isNull();
        }

        @Test
        @DisplayName("clearState should remove all state bindings")
        void clearStateRemovesBindings() {
            model.bindState("a", new TextInputState());
            model.bindState("b", new TextInputState());

            model.clearState();

            assertThat(model.getState("a")).isNull();
            assertThat(model.getState("b")).isNull();
        }

        @Test
        @DisplayName("containsKey should return true for state binding")
        void containsKeyForState() {
            model.bindState("state", new TextInputState());

            assertThat(model.containsKey("state")).isTrue();
        }

        @Test
        @DisplayName("get should return state binding when both attribute and state exist")
        void stateBindingTakesPrecedence() {
            model.put("key", "attribute-value");
            model.bindState("key", "state-value");

            assertThat(model.get("key")).isEqualTo("state-value");
        }

        @Test
        @DisplayName("get should fall back to attribute when no state binding exists")
        void getFallsBackToAttribute() {
            model.put("key", "attribute-value");

            assertThat(model.get("key")).isEqualTo("attribute-value");
        }

        @Test
        @DisplayName("asMap should merge attributes and state bindings")
        void asMapMergesAll() {
            model.put("attr", "value1");
            model.bindState("state", "value2");

            Map<String, Object> map = model.asMap();

            assertThat(map).containsEntry("attr", "value1")
                    .containsEntry("state", "value2");
        }

        @Test
        @DisplayName("asMap should let state bindings override attributes")
        void asMapStateOverridesAttributes() {
            model.put("key", "from-attribute");
            model.bindState("key", "from-state");

            Map<String, Object> map = model.asMap();

            assertThat(map).containsEntry("key", "from-state");
        }

        @Test
        @DisplayName("stateBindings should return only state bindings")
        void stateBindingsReturnsOnlyState() {
            model.put("attr", "value");
            model.bindState("state", "stateValue");

            Map<String, Object> bindings = model.stateBindings();

            assertThat(bindings).hasSize(1).containsEntry("state", "stateValue");
        }

        @Test
        @DisplayName("bindState should support fluent chaining")
        void fluentChaining() {
            TemplateModel result = model.bindState("a", "1").bindState("b", "2");

            assertThat(result).isSameAs(model);
            assertThat(model.getState("a")).isEqualTo("1");
            assertThat(model.getState("b")).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("interaction between attributes and state bindings")
    class InteractionTests {

        @Test
        @DisplayName("clear should only remove attributes, not state bindings")
        void clearOnlyRemovesAttributes() {
            model.put("attr1", "a");
            model.put("attr2", "b");
            model.bindState("state1", "x");
            model.bindState("state2", "y");

            model.clear();

            assertThat(model.get("attr1")).isNull();
            assertThat(model.get("attr2")).isNull();
            assertThat(model.getState("state1")).isEqualTo("x");
            assertThat(model.getState("state2")).isEqualTo("y");
        }

        @Test
        @DisplayName("state binding with FormState should preserve mutable state across clear")
        void formStateSurvivesClearWithMutations() {
            FormState form = FormState.builder()
                    .textField("name", "")
                    .booleanField("active", false)
                    .build();

            model.bindState("myForm", form);

            // Simulate user input
            form.setTextValue("name", "Bob");
            form.setBooleanValue("active", true);

            // Simulate frame cycle
            model.clear();

            // State should still contain the user's input
            FormState retrieved = (FormState) model.getState("myForm");
            assertThat(retrieved.textValue("name")).isEqualTo("Bob");
            assertThat(retrieved.booleanValue("active")).isTrue();
        }
    }
}
