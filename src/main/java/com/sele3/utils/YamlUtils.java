package com.sele3.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class YamlUtils {

    private static Map<String, Object> yamlData;

    public static void loadYaml(String yamlPath) {
        try (FileReader reader = new FileReader(yamlPath)) {
            Yaml yaml = new Yaml();
            yamlData = yaml.load(reader);
        } catch (IOException e) {
            log.error("Failed to load YAML file: {}", yamlPath, e);
        }
    }

    public static Object getProperty(String key) {
        if (yamlData == null || key == null || key.isEmpty()) {
            return null;
        }

        String[] keys = key.split("\\.");
        Object current = yamlData;

        for (String k : keys) {
            if (current instanceof Map<?, ?> map) {
                current = map.get(k);
            } else {
                return null;
            }
        }

        return current;
    }
}
