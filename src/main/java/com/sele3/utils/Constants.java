package com.sele3.utils;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final Duration MEDIUM_TIMEOUT = Duration.ofSeconds(10);
    public static final String EN_LANGUAGE_YAML_FILE_PATH = "src/test/resources/languages/en.yaml";
    public static final String VI_LANGUAGE_YAML_FILE_PATH = "src/test/resources/languages/vi.yaml";
    public static final String LEAPFROG_GAMES_FILE_PATH = "src/test/resources/leapfrog-games.xlsx";

}
