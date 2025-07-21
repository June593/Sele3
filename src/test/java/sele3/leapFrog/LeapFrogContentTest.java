package sele3.leapFrog;

import com.codeborne.selenide.WebDriverRunner;
import com.sele3.data.leapfrog.GameData;
import com.sele3.page.leapfrog.LeapFrogPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.Constants;

import com.sele3.utils.ExcelUtil;
import com.sele3.utils.GameDataComparator;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static com.codeborne.selenide.Selenide.open;

public class LeapFrogContentTest {

    @Test
    public void compareGamesWithExcel() {
        open(String.format(LeapFrogPage.BASE_URL, 1));
        totalPages = leapFrogPage.getTotalPages();
        List<Map<String, String>> raw = ExcelUtil.readAsMapList(new File(Constants.LEAPFROG_GAMES_FILE_PATH));
        List<GameData> expectedList = raw.stream()
                .map(map -> new GameData(map.get("title"), map.get("age"), map.get("price")))
                .toList();
        List<GameData> actualList = leapFrogPage.crawlActualData(totalPages);
        GameDataComparator.compareGameData(expectedList, actualList);
        Assertion.assertAll("Completed");
    }

    LeapFrogPage leapFrogPage = new LeapFrogPage();
    int totalPages;
}
