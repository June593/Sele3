package sele3.leapFrog;

import com.codeborne.selenide.WebDriverRunner;
import com.sele3.data.leapfrog.GameData;
import com.sele3.page.leapfrog.LeapFrogPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.Constants;

import org.testng.annotations.Test;

import java.util.*;

public class LeapFrogContentTest {

    @Test
    public void compareGamesWithExcel() {
        List<GameData> expectedList = leapFrogPage.readExpectedData(Constants.LEAPFROG_GAMES_FILE_PATH);
        List<GameData> actualList = leapFrogPage.crawlActualData();
        leapFrogPage.compareData(expectedList, actualList);
        Assertion.assertAll("Completed");
    }
    LeapFrogPage leapFrogPage = new LeapFrogPage();
}
