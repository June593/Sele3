package com.sele3.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class WebUtils {

    private static WebDriver driver(){
        return WebDriverRunner.getWebDriver();
    }

    /**
     * Switch to the next browser tab (wraps around).
     */
    public static void switchToNextTab() {
        List<String> tabs = new ArrayList<>(driver().getWindowHandles());
        String current = driver().getWindowHandle();

        int currentIndex = tabs.indexOf(current);
        int nextIndex = (currentIndex + 1) % tabs.size();

        Selenide.switchTo().window(tabs.get(nextIndex));
        Allure.step(String.format("Switched to next tab (index: %d)", nextIndex));
    }

    /**
     * Close the current tab and switch back to the previous one.
     * If current tab is the first, it will switch to index 0 again.
     */
    public static void closeCurrentTabAndSwitchBack() {
        List<String> tabs = new ArrayList<>(driver().getWindowHandles());
        String current = driver().getWindowHandle();
        int currentIndex = tabs.indexOf(current);

        Selenide.closeWindow();
        Allure.step("Closed current tab.");

        int previousIndex = (currentIndex > 0) ? currentIndex - 1 : 0;

        List<String> remaining = new ArrayList<>(driver().getWindowHandles());
        Selenide.switchTo().window(remaining.get(previousIndex));
        Allure.step(String.format("Switched back to tab (index: %d)", previousIndex));
    }
}
