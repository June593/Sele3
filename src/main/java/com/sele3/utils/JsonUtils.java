package com.sele3.utils;

import lombok.SneakyThrows;
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils {

    @SneakyThrows
    public static String getJson(String jsonPath) {
        return new String(Files.readAllBytes(Paths.get(jsonPath)));
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
