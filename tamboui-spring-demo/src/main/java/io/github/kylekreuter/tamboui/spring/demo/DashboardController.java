package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

@TamboScreen(template = "dashboard")
public class DashboardController implements ScreenController {

    @Override
    public void populate(TemplateModel model) {
        model.put("title", "TamboUI Demo Dashboard")
             .put("status", "Running");
    }
}
