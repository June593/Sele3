package sele3.book;

import com.sele3.enums.book.WorkingShadowDomMethod;
import com.sele3.page.book.HomePage;
import com.sele3.page.book.SearchResultPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.Constants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sele3.TestBase;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class Book_VerifyUserCanSearchBookSuccessfully extends TestBase {

    @DataProvider(name = "workingShowdownDomMethod")
    public Object[][] getWorkingShowdownDomMethod() {
        return new Object[][]{
                {WorkingShadowDomMethod.SELENIDE},
                {WorkingShadowDomMethod.SELENIUM},
        };
    }

    @Test(dataProvider = "workingShowdownDomMethod", description = "Verify user can search book successfully using different methods")
    public void verifyUserCanSearchBookSuccessfully(WorkingShadowDomMethod method) {
        open(Constants.BOOK_BASE_URL);
        homePage.search(method, KEY_SEARCH);
        List<String> bookTitles = searchResultPage.getAllBookTitles(method);

        Assertion.assertTrue(searchResultPage.areAllBookTitlesContainKeyword(bookTitles, KEY_SEARCH), String.format("VP: Verify all book titles contain the searched keyword (%s)", method));

        Assertion.assertAll("Complete running test case");
    }

    private final HomePage homePage = new HomePage();
    private final SearchResultPage searchResultPage = new SearchResultPage();
    private static final String KEY_SEARCH = "playwright";
}
