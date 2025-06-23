package com.sele3.utils;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final Map<String, String> ConfigFiles = new HashMap<>();
    public static final String CHROME = "chrome";
    public static final String SAFARI = "safari";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    static {
        ConfigFiles.put(CHROME, "src/test/resources/configuration/chrome.json");
        ConfigFiles.put(SAFARI, "src/test/resources/configuration/safari.json");
    }
}
