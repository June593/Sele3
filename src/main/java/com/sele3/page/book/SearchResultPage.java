package com.sele3.page.book;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.sele3.utils.Constants;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$$;

public class SearchResultPage {

    /**
     * Get all book titles (choose method: "selenide" or "selenium").
     */
    public List<String> getAllBookTitles(String method) {
        if ("selenium".equalsIgnoreCase(method)) {
            return getTitlesBySelenium();
        }
        return getTitlesBySelenide();
    }

    // --- Private core methods ---
    private List<String> getTitlesBySelenide() {
        return $$(shadowDeepCss(BOOK_TITLE_SELECTOR))
                .should(CollectionCondition.allMatch("All titles should be non-empty",
                        el -> !el.getText().isEmpty()))
                .texts();
    }

    private List<String> getTitlesBySelenium() {
        WebDriver driver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Constants.MEDIUM_TIMEOUT);

        return wait.until(d -> {
            var exploreRoot = driver.findElement(BOOK_APP).getShadowRoot()
                    .findElement(BOOK_EXPLORE).getShadowRoot();

            List<String> titles = exploreRoot.findElements(BOOK_ITEM).stream()
                    .map(item -> item.getShadowRoot()
                            .findElement(By.cssSelector(BOOK_TITLE_SELECTOR))
                            .getText().trim())
                    .collect(Collectors.toList());

            return !titles.isEmpty() && titles.stream().noneMatch(String::isEmpty) ? titles : null;
        });
    }

    /**
     * Verify all titles contain the given keyword (case-insensitive).
     */
    public boolean areAllBookTitlesContainKeyword(List<String> titles, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return titles.stream().allMatch(title -> {
            boolean contains = title.toLowerCase().contains(lowerKeyword);
            if (!contains) {
                Allure.step(String.format("The book title '%s' does not contain keyword '%s'", title, keyword));
            }
            return contains;
        });
    }

    private static final String BOOK_TITLE_SELECTOR = "h2.title";
    private static final By BOOK_APP = By.cssSelector("book-app");
    private static final By BOOK_EXPLORE = By.cssSelector("book-explore");
    private static final By BOOK_ITEM = By.cssSelector("book-item");
}
