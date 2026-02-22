package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

/**
 * Dialog screen that appears centered when the user presses {@code d} on the showcase.
 * Press {@code y} to confirm, {@code n} to cancel, or {@code Escape} to go back.
 */
@TamboScreen(value = "dialogShowcase", template = "dialog-showcase")
public class DialogShowcaseController implements ScreenController {

    private final NavigationRouter navigationRouter;

    private String dialogStatus = "Pending...";

    public DialogShowcaseController(NavigationRouter navigationRouter) {
        this.navigationRouter = navigationRouter;
    }

    @Override
    public void populate(TemplateModel model) {
        model.put("dialogStatus", dialogStatus);
    }

    @OnKey("y")
    void confirm() {
        dialogStatus = "Confirmed!";
        navigationRouter.navigateTo("showcase");
    }

    @OnKey("n")
    void cancel() {
        dialogStatus = "Cancelled.";
        navigationRouter.navigateTo("showcase");
    }

    @OnKey("Escape")
    void back() {
        dialogStatus = "Pending...";
        navigationRouter.navigateTo("showcase");
    }
}
