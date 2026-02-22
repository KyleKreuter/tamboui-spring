package io.github.kylekreuter.tamboui.spring.demo;

import java.util.Map;

public enum Language {

    DE("Deutsch", Map.ofEntries(
            Map.entry("title", "System-Monitor"),
            Map.entry("language", "Sprache"),
            Map.entry("os", "Betriebssystem"),
            Map.entry("arch", "Architektur"),
            Map.entry("processors", "Prozessoren"),
            Map.entry("cpuLoad", "CPU-Last"),
            Map.entry("memory", "Speicher (Heap)"),
            Map.entry("uptime", "Laufzeit"),
            Map.entry("java", "Java-Version"),
            Map.entry("vm", "VM"),
            Map.entry("footerHint", "\u25C0 \u25B6 Sprache wechseln  |  Ctrl+Q Beenden")
    )),

    EN("English", Map.ofEntries(
            Map.entry("title", "System Monitor"),
            Map.entry("language", "Language"),
            Map.entry("os", "Operating System"),
            Map.entry("arch", "Architecture"),
            Map.entry("processors", "Processors"),
            Map.entry("cpuLoad", "CPU Load"),
            Map.entry("memory", "Memory (Heap)"),
            Map.entry("uptime", "Uptime"),
            Map.entry("java", "Java Version"),
            Map.entry("vm", "VM"),
            Map.entry("footerHint", "\u25C0 \u25B6 Switch language  |  Ctrl+Q Quit")
    ));

    private final String displayName;
    private final Map<String, String> labels;

    Language(String displayName, Map<String, String> labels) {
        this.displayName = displayName;
        this.labels = labels;
    }

    public String displayName() {
        return displayName;
    }

    public Map<String, String> labels() {
        return labels;
    }
}
