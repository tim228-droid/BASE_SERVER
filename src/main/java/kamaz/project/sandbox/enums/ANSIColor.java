package kamaz.project.sandbox.enums;

import java.util.HashMap;
import java.util.Map;

public enum ANSIColor {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    ORANGE("\u001B[38;5;208m");
    private final String code;

    ANSIColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // Optional: Create a Map for dynamic lookups
    private static final Map<String, String> COLOR_MAP = new HashMap<>();
    static {
        for (ANSIColor color : ANSIColor.values()) {
            COLOR_MAP.put(color.name(), color.code);
        }
    }

    public static Map<String, String> getColorMap() {
        return COLOR_MAP;
    }
}
