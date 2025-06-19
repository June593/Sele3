package com.sele3.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

public class RetryConfig {
    public static String retryMode = "immediate";  // default
    public static int maxRetryCount = 1;           // default
    private static final Logger logger = LoggerFactory.getLogger(RetryConfig.class);

    public static void init(ITestContext context) {
        String mode = context.getCurrentXmlTest().getParameter("retryMode");
        String maxRetry = context.getCurrentXmlTest().getParameter("maxRetryCount");

        if (mode != null && !mode.isEmpty()) {
            retryMode = mode;
        }

        if (maxRetry != null && !maxRetry.isEmpty()) {
            try {
                maxRetryCount = Integer.parseInt(maxRetry);
            } catch (NumberFormatException e) {
                logger.info("Invalid maxRetryCount, using default: 1");
                maxRetryCount = 1;
            }
        }

        logger.info("RetryConfig loaded: mode = " + retryMode + ", maxRetryCount = " + maxRetryCount);
    }
}
