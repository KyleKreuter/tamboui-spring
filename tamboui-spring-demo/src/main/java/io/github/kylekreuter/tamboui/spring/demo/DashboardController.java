package io.github.kylekreuter.tamboui.spring.demo;

import io.github.kylekreuter.tamboui.spring.annotation.OnKey;
import io.github.kylekreuter.tamboui.spring.annotation.TamboScreen;
import io.github.kylekreuter.tamboui.spring.core.ScreenController;
import io.github.kylekreuter.tamboui.spring.core.TamboSpringApp;
import io.github.kylekreuter.tamboui.spring.core.TemplateModel;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@TamboScreen(template = "dashboard")
public class DashboardController implements ScreenController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final TamboSpringApp tamboSpringApp;
    private final List<Language> languages = List.of(Language.values());
    private int languageIndex = 0;

    public DashboardController(TamboSpringApp tamboSpringApp) {
        this.tamboSpringApp = tamboSpringApp;
    }

    @Override
    public void populate(TemplateModel model) {
        Language lang = languages.get(languageIndex);
        Map<String, String> l = lang.labels();
        LocalDateTime now = LocalDateTime.now();

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();

        long heapUsed = mem.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long heapMax = mem.getHeapMemoryUsage().getMax() / (1024 * 1024);

        long uptimeSec = rt.getUptime() / 1000;
        String uptimeFormatted = String.format("%02d:%02d:%02d",
                uptimeSec / 3600, (uptimeSec % 3600) / 60, uptimeSec % 60);

        double loadAvg = os.getSystemLoadAverage();
        String loadStr = loadAvg < 0 ? "N/A" : String.format("%.1f", loadAvg);

        model.put("title", l.get("title"))
             .put("date", now.format(DATE_FMT))
             .put("time", now.format(TIME_FMT))
             .put("languageLabel", l.get("language"))
             .put("languageName", lang.displayName())
             .put("osLabel", l.get("os"))
             .put("osValue", os.getName() + " " + os.getVersion())
             .put("archLabel", l.get("arch"))
             .put("archValue", os.getArch())
             .put("cpuLoadLabel", l.get("cpuLoad"))
             .put("cpuLoadValue", loadStr)
             .put("processorsLabel", l.get("processors"))
             .put("processorsValue", String.valueOf(os.getAvailableProcessors()))
             .put("memoryLabel", l.get("memory"))
             .put("memoryValue", heapUsed + " / " + heapMax + " MB")
             .put("uptimeLabel", l.get("uptime"))
             .put("uptimeValue", uptimeFormatted)
             .put("javaLabel", l.get("java"))
             .put("javaValue", rt.getSpecVersion())
             .put("vmLabel", l.get("vm"))
             .put("vmValue", rt.getVmName())
             .put("footerHint", l.get("footerHint"));
    }

    @OnKey("left")
    void previousLanguage() {
        languageIndex = (languageIndex - 1 + languages.size()) % languages.size();
    }

    @OnKey("right")
    void nextLanguage() {
        languageIndex = (languageIndex + 1) % languages.size();
    }

    @OnKey("ctrl+q")
    void quit() {
        tamboSpringApp.getRunner().quit();
    }
}
