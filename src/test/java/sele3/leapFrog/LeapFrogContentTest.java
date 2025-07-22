package sele3.leapFrog;

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
        List<GameData> gameDataListFromExcel = ExcelUtil.readFromExcel(new File(Constants.LEAPFROG_GAMES_FILE_PATH), GameData::fromRow);
        List<GameData> gameDataListFromAllPages = leapFrogPage.getAllGameDataFromAllPages(totalPages);
        GameDataComparator.compareGameData(gameDataListFromExcel, gameDataListFromAllPages);
        Assertion.assertAll("Completed");
    }

    LeapFrogPage leapFrogPage = new LeapFrogPage();
    int totalPages;
}
