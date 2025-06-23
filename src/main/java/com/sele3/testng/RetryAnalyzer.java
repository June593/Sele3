package com.sele3.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);

    public static int MAX_RETRY = 0;
    public static String RETRY_TYPE = "none";
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        return RETRY_TYPE.equalsIgnoreCase("immediate") && retryCount++ < MAX_RETRY;
    }

    /**
     * Initialize retry config from either TestNG XML parameters or system properties
     */
    public static void init(ITestContext context) {
        String xmlRetryType = context.getCurrentXmlTest().getParameter("retryMode");
        String xmlMaxRetry = context.getCurrentXmlTest().getParameter("maxRetryCount");

        RETRY_TYPE = (xmlRetryType != null && !xmlRetryType.isEmpty())
                ? xmlRetryType
                : System.getProperty("retryType", "none");

        String maxRetryRaw = (xmlMaxRetry != null && !xmlMaxRetry.isEmpty())
                ? xmlMaxRetry
                : System.getProperty("maxRetry", "0");

        try {
            MAX_RETRY = Integer.parseInt(maxRetryRaw);
        } catch (NumberFormatException e) {
            logger.warn("Invalid retry count '{}', fallback to 0", maxRetryRaw);
            MAX_RETRY = 0;
        }

        logger.info("Retry config - Type: {}, MaxRetry: {}", RETRY_TYPE, MAX_RETRY);
    }
}
