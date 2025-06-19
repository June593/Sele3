package sele3;

import com.codeborne.selenide.Configuration;
import com.sele3.config.BrowserConfig;
import com.sele3.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.sele3.utils.Constants.ConfigFiles;

@Slf4j
public class TestBase {

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser", "language"})
    public void beforeClass(@Optional String browser,
                            @Optional String languageOverride) {

        BrowserConfig config = ConfigLoader.loadConfig(ConfigFiles.get(browser));

        Configuration.browser = browser;
        Configuration.browserVersion = config.getBrowserVersion();
        Configuration.headless = Boolean.TRUE.equals(config.getHeadless());
        Configuration.timeout = config.getTimeout();
        Configuration.browserSize = config.getBrowserSize();

        String lang = Objects.requireNonNullElse(languageOverride, config.getLanguage());
        System.setProperty("LANGUAGE", lang);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        closeWebDriver();
    }

}