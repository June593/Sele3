package sele3;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;
@Slf4j
public class TestRetry {

    @Test
    public void testPass1() {
        Allure.step("✅ Running testPass1");
        Assert.assertTrue(true, "This test should pass");
    }

    @Test
    public void testFail1() {
        Allure.step("❌ Running testFail1");

        Assert.assertTrue(false, "This test should fail");
    }

    @Test
    public void testPass2() {
        Allure.step("✅ Running testPass2");
        Assert.assertTrue(5 > 1, "This test should pass");
    }

    @Test
    public void testFail2() {
        Allure.step("❌ Running testFail2");
        Assert.assertTrue(1 > 5, "This test should fail");
    }
}
