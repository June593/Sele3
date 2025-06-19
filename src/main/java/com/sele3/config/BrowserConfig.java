package com.sele3.config;

import lombok.Data;

@Data
public class BrowserConfig {
    private String browser;
    private String browserVersion;
    private String browserSize;
    private Boolean headless;
    private Integer timeout;
    private String language;
}
