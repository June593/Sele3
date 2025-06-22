package sele3;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Slf4j
public class TestBase {

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser", "language"})
    public void beforeClass(@Optional String browser,
                            @Optional String languageOverride) {
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        closeWebDriver();
    }
}