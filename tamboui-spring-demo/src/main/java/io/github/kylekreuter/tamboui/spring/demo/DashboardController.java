package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TamboScreen(template = "dashboard")
public class DashboardController implements ScreenController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Override
    public void populate(TemplateModel model) {
        model.put("title", "TamboUI Demo Dashboard")
             .put("status", "Running");
    }

    @OnKey("q")
    void onQuitPressed() {
        log.info("Quit key 'q' pressed on Dashboard");
    }
}
