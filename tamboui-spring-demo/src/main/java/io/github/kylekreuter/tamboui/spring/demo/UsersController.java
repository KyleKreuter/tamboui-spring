package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.NavigationRouter;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TamboScreen(value = "users", template = "users")
public class UsersController implements ScreenController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    private static final List<Map<String, String>> USER_POOL = List.of(
            Map.of("name", "Alice Johnson", "role", "Admin", "status", "Active"),
            Map.of("name", "Bob Smith", "role", "Editor", "status", "Active"),
            Map.of("name", "Charlie Brown", "role", "Viewer", "status", "Inactive"),
            Map.of("name", "Diana Prince", "role", "Admin", "status", "Active"),
            Map.of("name", "Eve Wilson", "role", "Editor", "status", "Inactive"),
            Map.of("name", "Frank Castle", "role", "Viewer", "status", "Active")
    );

    private final NavigationRouter router;
    private final ArrayList<Map<String, String>> users = new ArrayList<>();
    private boolean showDetails = false;

    public UsersController(NavigationRouter router) {
        this.router = router;
        users.add(USER_POOL.get(0));
        users.add(USER_POOL.get(1));
        users.add(USER_POOL.get(2));
    }

    @Override
    public void populate(TemplateModel model) {
        List<String> userLines = new ArrayList<>();
        for (Map<String, String> user : users) {
            String marker = "Active".equals(user.get("status")) ? "●" : "○";
            userLines.add(user.get("name") + " (" + user.get("role") + ") " + marker);
        }

        List<String> teams = List.of("Engineering", "Design", "Marketing");

        Map<String, String> current = users.isEmpty() ? Map.of() : users.get(0);
        String currentName = current.getOrDefault("name", "-");
        String currentRole = current.getOrDefault("role", "-");
        String currentStatus = current.getOrDefault("status", "-");
        boolean currentActive = "Active".equals(currentStatus);

        model.put("userLines", userLines)
             .put("teams", teams)
             .put("showDetails", showDetails)
             .put("hasUsers", !users.isEmpty())
             .put("userCount", String.valueOf(users.size()))
             .put("currentName", currentName)
             .put("currentRole", currentRole)
             .put("currentStatus", currentStatus)
             .put("currentActive", currentActive)
             .put("canAdd", users.size() < USER_POOL.size())
             .put("canRemove", !users.isEmpty());
    }

    @OnKey("a")
    void addUser() {
        if (users.size() < USER_POOL.size()) {
            Map<String, String> next = USER_POOL.get(users.size());
            users.add(next);
            log.info("User added: {}", next.get("name"));
        } else {
            log.info("User pool exhausted");
        }
    }

    @OnKey("r")
    void removeUser() {
        if (!users.isEmpty()) {
            Map<String, String> removed = users.remove(users.size() - 1);
            log.info("User removed: {}", removed.get("name"));
        }
    }

    @OnKey("d")
    void toggleDetails() {
        showDetails = !showDetails;
        log.info("Details toggled: {}", showDetails);
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
