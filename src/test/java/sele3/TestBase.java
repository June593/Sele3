package sele3;

import com.sele3.utils.Constants;
import com.sele3.utils.InputParameters;
import com.sele3.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;

import org.testng.annotations.*;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Slf4j
public class TestBase {

    @BeforeClass(alwaysRun = true)
    @Parameters({"environment", "language"})
    public void beforeClass(String env, @Optional String language) {
        InputParameters.ENV = System.getProperty("environment", Objects.requireNonNullElse(env, "agoda"));
        InputParameters.LANGUAGE = System.getProperty("language", Objects.requireNonNullElse(language, "vi"));

        String yamlPath = InputParameters.LANGUAGE.equals("vi") ? Constants.VI_LANGUAGE_YAML_FILE_PATH : Constants.EN_LANGUAGE_YAML_FILE_PATH;
        YamlUtils.loadYaml(yamlPath);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        closeWebDriver();
    }
}
