package com.sele3.page.book;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.sele3.enums.book.WorkingShadowDomMethod;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;

public class HomePage {

    @Step("Enter keyword into search text box using {method}: {keyword}")
    public void search(WorkingShadowDomMethod method, String keyword) {
        if (method.equals(WorkingShadowDomMethod.SELENIDE)) {
            SelenideElement searchBox = getSearchBoxSelenide();
            searchBox.click();
            searchBox.setValue(keyword).pressEnter();
        } else {
            WebElement searchBox = getSearchBoxSelenium();
            searchBox.click();
            searchBox.sendKeys(keyword, Keys.ENTER);
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
