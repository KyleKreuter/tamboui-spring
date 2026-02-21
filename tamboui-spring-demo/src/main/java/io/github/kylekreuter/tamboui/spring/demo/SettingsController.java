package io.github.kylekreuter.tamboui.spring.demo;

import dev.tamboui.widgets.form.FormState;
import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.OnSubmit;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@TamboScreen(value = "settings", template = "settings")
public class SettingsController implements ScreenController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final NavigationRouter router;
    private final FormState settingsForm;
    private String savedMessage = "";
    private String lastSaveTime = "";
    private int saveCount = 0;

    public SettingsController(NavigationRouter router) {
        this.router = router;
        this.settingsForm = FormState.builder()
                .textField("username", "")
                .textField("email", "")
                .build();
    }

    @Override
    public void populate(TemplateModel model) {
        model.put("savedMessage", savedMessage)
            .put("hasSavedMessage", !savedMessage.isEmpty())
            .put("lastSaveTime", lastSaveTime)
            .put("saveCount", String.valueOf(saveCount))
            .put("appVersion", "0.1.0")
            .put("backend", "jline3")
            .bindState("settingsForm", settingsForm);
    }

    @OnSubmit("settingsForm")
    void onSettingsSubmit(FormState form) {
        String username = form.textValue("username");
        String email = form.textValue("email");
        log.info("Settings saved — username: {}, email: {}", username, email);
        lastSaveTime = LocalTime.now().format(TIME_FMT);
        saveCount++;
        savedMessage = "Settings saved for " + username + " (" + lastSaveTime + ")";
    }

    @OnKey("1")
    void goToDashboard() {
        router.navigateTo("dashboard");
    }

    @OnKey("2")
    void goToUsers() {
        router.navigateTo("users");
    }

    @OnKey("3")
    void goToSettings() {
        router.navigateTo("settings");
    }
}
