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

    @Test(groups = {"smoke", "regression"}, description =  "Verify the data from excel file with information on UI")
    public void compareGamesWithExcel() {
        open(String.format(LeapFrogPage.BASE_URL, 1));
        totalPages = leapFrogPage.getTotalPages();
        List<GameData> gameDataListFromExcel = ExcelUtil.readGameDataListFromExcel(new File(Constants.LEAPFROG_GAMES_FILE_PATH));
        List<GameData> gameDataListFromAllPages = leapFrogPage.getAllGameDataFromAllPages(totalPages);
        GameDataComparator.compareGameData(gameDataListFromExcel, gameDataListFromAllPages);
        Assertion.assertAll("Completed");
    }

    LeapFrogPage leapFrogPage = new LeapFrogPage();
    int totalPages;
}
