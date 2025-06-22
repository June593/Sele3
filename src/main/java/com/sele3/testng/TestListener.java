package com.sele3.testng;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class TestListener implements ITestListener, IAnnotationTransformer, StepLifecycleListener {

    private static final List<ITestResult> failedTests = new ArrayList<>();

    public static List<ITestResult> getFailedTests() {
        return new ArrayList<>(failedTests);
    }


    @Override
    public void onTestFailure(ITestResult result) {
        try {
            byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
            Allure.addAttachment("Screenshot on Failure", new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            logger.info("Failed to capture screenshot: " + e.getMessage());
        }
        failedTests.add(result);
    }

    @Override
    public void onStart(ITestContext context) {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false)
                        .includeSelenideSteps(true)
        );
        RetryAnalyzer.init(context);
        logger.info(String.format("RETRY_TYE: [%s], RETRY_COUNT: [%d]", RetryAnalyzer.RETRY_TYPE, RetryAnalyzer.MAX_RETRY));
    }

    @Override
    public void onFinish(ITestContext context) {
    }


    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {

        if ("immediate".equalsIgnoreCase(RetryAnalyzer.RETRY_TYPE)) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void beforeStepStart(StepResult result) {
        logger.info("[Step]: {} -> is running", result.getName());
    }

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
}