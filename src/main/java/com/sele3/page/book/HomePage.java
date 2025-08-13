package com.sele3.page.book;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;

public class HomePage {

    @Step("Enter keyword into search text box using {method}: {keyword}")
    public void search(String method, String keyword) {
        if ("selenide".equalsIgnoreCase(method)) {
            SelenideElement searchBox = getSearchBoxSelenide();
            searchBox.click();
            searchBox.setValue(keyword).pressEnter();
        } else if ("selenium".equalsIgnoreCase(method)) {
            WebElement searchBox = getSearchBoxSelenium();
            searchBox.click();
            searchBox.sendKeys(keyword, Keys.ENTER);
        } else {
            throw new IllegalArgumentException("Unsupported search method: " + method);
        }
    }

    private SelenideElement getSearchBoxSelenide() {
        return $(shadowDeepCss(SEARCH_TEXTBOX_SELECTOR));
    }

    private WebElement getSearchBoxSelenium() {
        return WebDriverRunner.getWebDriver()
                .findElement(BOOK_APP)
                .getShadowRoot()
                .findElement(SEARCH_TEXTBOX);
    }

    private static final String SEARCH_TEXTBOX_SELECTOR = "input#input";
    private static final By BOOK_APP = By.cssSelector("book-app");
    private static final By SEARCH_TEXTBOX = By.cssSelector(SEARCH_TEXTBOX_SELECTOR);
}
