package com.sele3.testng;

import com.sele3.utils.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.Objects;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        return InputParameters.RETRY_TYPE.equalsIgnoreCase("immediate") && retryCount++ < InputParameters.RETRY_MAX;
    }

    /**
     * Initialize retry config from either system properties or TestNG XML parameters.
     * Priority: System property (-D) > testng.xml > default
     */
    public static void init(ITestContext context) {
        String xmlRetryType = context.getCurrentXmlTest().getParameter("retryType");
        String xmlMaxRetry = context.getCurrentXmlTest().getParameter("maxRetry");

        InputParameters.RETRY_TYPE = System.getProperty("retryType",
                Objects.requireNonNullElse(xmlRetryType, "none"));

        String maxRetryRaw = System.getProperty("maxRetry",
                Objects.requireNonNullElse(xmlMaxRetry, "0"));

        try {
            InputParameters.RETRY_MAX = Integer.parseInt(maxRetryRaw);
        } catch (NumberFormatException e) {
            logger.warn("Invalid maxRetry '{}', fallback to 0", maxRetryRaw);
            InputParameters.RETRY_MAX = 0;
        }

        logger.info("RETRY_TYPE: [{}], RETRY_MAX: [{}]",
                InputParameters.RETRY_TYPE, InputParameters.RETRY_MAX);
    }
}
