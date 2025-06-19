package com.sele3.config;

import com.sele3.utils.JsonUtils;


public class ConfigLoader {

    public static BrowserConfig loadConfig(String jsonPath) {
        return JsonUtils.fromJson(JsonUtils.getJson(jsonPath), BrowserConfig.class);
    }
}
