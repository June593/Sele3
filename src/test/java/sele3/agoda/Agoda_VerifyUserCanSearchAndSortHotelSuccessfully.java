package sele3.agoda;

import lombok.extern.slf4j.Slf4j;
import sele3.TestBase;
import com.sele3.page.general.GeneralPage;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class Agoda_VerifyUserCanSearchAndSortHotelSuccessfully extends TestBase {

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void agoda_VerifyUserCanSearchAndSortHotelSuccessfully() {
        generalPage.gotoURL("https://www.agoda.com/");
    }

    GeneralPage generalPage = new GeneralPage();
}
