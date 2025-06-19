package com.sele3.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);

    @Override
    public boolean retry(ITestResult result) {
        if ("immediate".equals(RetryConfig.retryMode)) {
            if (retryCount < RetryConfig.maxRetryCount) {
                retryCount++;
                logger.info("Immediate retry " + result.getName() + " attempt " + (retryCount + 1));
                return true;
            }
        }
        return false;
    }
}
