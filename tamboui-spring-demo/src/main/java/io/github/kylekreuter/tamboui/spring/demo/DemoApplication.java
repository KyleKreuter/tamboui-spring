package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.bindings.BindingSets;
import dev.tamboui.tui.bindings.Bindings;
import dev.tamboui.tui.bindings.KeyTrigger;
import io.github.kylekreuter.tamboui.spring.core.ToolkitRunnerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ToolkitRunnerFactory toolkitRunnerFactory() {
        Bindings bindings = BindingSets.standard().toBuilder()
                .unbind(Actions.QUIT)
                .bind(KeyTrigger.ctrl('q'), Actions.QUIT)
                .bind(KeyTrigger.ctrl('c'), Actions.QUIT)
                .build();
        return () -> ToolkitRunner.builder().bindings(bindings).build();
    }
}
