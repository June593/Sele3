package sele3;

import com.codeborne.selenide.Configuration;
import com.sele3.utils.Constants;
import com.sele3.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Slf4j
public class TestBase {

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser", "environment", "language"})
    public void beforeClass(String browser, String env, @Optional String language) {
        Configuration.browser = Objects.requireNonNullElse(browser, "chrome");
        System.setProperty("profile", env);
        System.setProperty("language", language);
        String yamlPath = language.equals("vi") ? Constants.VI_LANGUAGE_YAML_FILE_PATH : Constants.EN_LANGUAGE_YAML_FILE_PATH;
        YamlUtils.loadYaml(yamlPath);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        closeWebDriver();
    }
}