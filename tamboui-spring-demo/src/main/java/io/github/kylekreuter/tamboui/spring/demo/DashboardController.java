package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@TamboScreen(value = "dashboard", template = "dashboard")
public class DashboardController implements ScreenController {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final NavigationRouter router;
    private int refreshCount = 0;

    public DashboardController(NavigationRouter router) {
        this.router = router;
    }

    @Override
    public void populate(TemplateModel model) {
        refreshCount++;
        model.put("title", "TamboUI Demo Dashboard")
             .put("status", "Running")
             .put("currentTime", LocalDateTime.now().format(TIME_FMT))
             .put("userCount", "3")
             .put("taskCount", "7")
             .put("uptime", "3h 14m")
             .put("version", "0.1.0")
             .put("refreshes", String.valueOf(refreshCount))
             .put("navItems", List.of("  [1] Dashboard", "  [2] Users", "  [3] Settings"));
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
