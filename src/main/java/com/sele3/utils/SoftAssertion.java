package com.sele3.utils;

import com.codeborne.selenide.Screenshots;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.apache.commons.io.FileUtils;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class SoftAssertion extends Assertion {

    private static final String DEFAULT_SOFT_ASSERT_MESSAGE = "The assertion is failed! ";
    private static Map<AssertionError, IAssert<?>> m_errors = Maps.newLinkedHashMap();

    public void assertAll() {
        assertAll("All assertions completed");
    }

    public void assertAll(String message) {
        if (!m_errors.isEmpty()) {
            Allure.step(message + " - Total check point failed: " + m_errors.size(), Status.FAILED);
            Map<AssertionError, IAssert<?>> tempErrors = m_errors;
            m_errors = Maps.newLinkedHashMap();
            throw new AssertionError(tempErrors);
        } else {
            Allure.step(message, Status.PASSED);
        }
    }

    @Override
    protected void doAssert(IAssert<?> a) {
        onBeforeAssert(a);
        try {
            a.doAssert();
            onAssertSuccess(a);
            Allure.step(a.getMessage(), Status.PASSED);
        } catch (AssertionError ex) {
            onAssertFailure(a, ex);
            m_errors.put(ex, a);

            Allure.step(DEFAULT_SOFT_ASSERT_MESSAGE + getErrorDetails(ex), Status.FAILED);

            File screenshot = Screenshots.takeScreenShotAsFile();
            if (Objects.nonNull(screenshot)) {
                try {
                    byte[] screenshotBytes = FileUtils.readFileToByteArray(screenshot);
                    Allure.addAttachment("Screenshot - Failed at: " + a.getMessage(),
                            new ByteArrayInputStream(screenshotBytes));
                } catch (IOException e) {
                    Allure.step("Failed to attach screenshot: " + e.getMessage(), Status.BROKEN);
                }
            }
        } finally {
            onAfterAssert(a);
        }
    }
}
