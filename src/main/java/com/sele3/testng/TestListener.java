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
    import org.testng.IAnnotationTransformer;
    import org.testng.ITestContext;
    import org.testng.ITestListener;
    import org.testng.ITestResult;
    import org.testng.annotations.ITestAnnotation;

    import java.io.ByteArrayInputStream;
    import java.lang.reflect.Constructor;
    import java.lang.reflect.Method;


    public class TestListener implements ITestListener, IAnnotationTransformer, StepLifecycleListener {

        @Override
        public void onTestFailure(ITestResult result) {
            try {
                byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
                Allure.addAttachment("Screenshot on Failure", new ByteArrayInputStream(screenshot));
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }

        @Override
        public void onStart(ITestContext context) {
            RetryConfig.init(context);
            SelenideLogger.addListener("AllureSelenide",
                    new AllureSelenide()
                            .screenshots(true)
                            .savePageSource(false)
                            .includeSelenideSteps(true)
            );
        }

        @Override
        public void onFinish(ITestContext context) {
            if ("delayed".equalsIgnoreCase(RetryConfig.retryMode)) {
                for (ITestResult failed : context.getFailedTests().getAllResults()) {
                    for (int i = 1; i <= RetryConfig.maxRetryCount; i++) {
                        try {
                            logger.info("Delayed retry " + failed.getName() + " attempt " + (i + 1));
                            Method method = failed.getMethod().getConstructorOrMethod().getMethod();
                            Object instance = failed.getInstance();
                            method.invoke(instance, failed.getParameters());
                        } catch (Exception e) {
                            logger.info("Retry failed: " + e.getMessage());
                        }
                    }
                }
            }
        }

        @Override
        public void transform(
                ITestAnnotation annotation,
                Class testClass,
                Constructor testConstructor,
                Method testMethod) {

            if ("immediate".equalsIgnoreCase(RetryConfig.retryMode)) {
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