package kamaz.project.sandbox.mapper;

import java.util.HashMap;
import java.util.Map;

import kamaz.project.sandbox.enums.ANSIColor;

public class HttpMethodColors {
private static final Map<String, String> METHOD_COLORS = new HashMap<>();

    static {
        METHOD_COLORS.put("GET", ANSIColor.BLUE.getCode());
        METHOD_COLORS.put("POST", ANSIColor.GREEN.getCode());
        METHOD_COLORS.put("PUT", ANSIColor.ORANGE.getCode());
        METHOD_COLORS.put("DELETE", ANSIColor.RED.getCode());
        METHOD_COLORS.put("PATCH", ANSIColor.CYAN.getCode());
    }

    public static String getMethodColor(String method) {
        return METHOD_COLORS.getOrDefault(method.toUpperCase(), ANSIColor.WHITE.getCode());
    }
}
