package sele3.vietjet;


import com.sele3.data.vietjet.Passenger;
import com.sele3.data.vietjet.SearchTicketData;
import com.sele3.data.vietjet.TicketData;
import com.sele3.page.general.GeneralPage;
import com.sele3.page.vietJet.HomePage;
import com.sele3.page.vietJet.PassengerInformationPage;
import com.sele3.page.vietJet.SelectFlightCheapPage;
import com.sele3.page.vietJet.SelectFlightPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.YamlUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sele3.TestBase;

import java.time.LocalDate;

public class Vietjet_VerifyUserCanSearchAndChooseCheapestTicketOnNext3Months extends TestBase {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        departureDate = LocalDate.now().plusDays(5);
        returnDate = LocalDate.now().plusDays(8);
        totalAdult = 2;
        searchTicketData = SearchTicketData.builder()
                .from("Hồ Chí Minh")
                .to("Hà Nội")
                .departureDate(departureDate)
                .returnDate(returnDate)
                .passenger(Passenger.builder()
                        .adultNumber(totalAdult)
                        .childrenNumber(0)
                        .infantNumber(0)
                        .build())
                .isFindLowestFare(true)
                .build();
        language = (String) YamlUtils.getProperty("config.displaying_language");
    }

    @Test(groups = {"smoke", "regression"}, description = "TC02 - Vietjet - Search and choose cheapest tickets on next 3 months successfully")
    public void vietjet_VerifyUserCanSearchAndChooseCheapestTicketOnNext3Months() {
        generalPage.openPage();
        homePage.acceptCookie();
        homePage.closeNotificationModal();

        Assertion.assertEquals(homePage.getLanguageDisplaying(), language, "VP: Verify that the page is displayed in the correct language.");

        homePage.searchTicket(searchTicketData);

        Assertion.assertTrue(selectFlightCheapPage.isPageDisplayed(), "VP: Verify that the \"Chọn giá vé\" page is displayed.");
        Assertion.assertTrue(selectFlightCheapPage.getFromPlace().contains(searchTicketData.getFrom()), "VP: Verify that the departure location is correct.");
        Assertion.assertEquals(selectFlightCheapPage.getToPlace(), searchTicketData.getTo(), "VP: Verify that the arrival location is correct.");
        Assertion.assertEquals(selectFlightCheapPage.getPassengerInfo(), searchTicketData.getPassenger(), "VP: Verify that the number of passengers is correct.");

        flightDate = selectFlightCheapPage.findCheapestDepartureReturnPairFlight(3, 3);
        departureDate = flightDate.getLeft();
        returnDate = flightDate.getRight();
        selectFlightCheapPage.selectTicket(departureDate, returnDate);
        selectFlightPage.closeAdsDialogIfVisible();

        Assertion.assertTrue(selectFlightPage.isSelectTravelOptionsPageDisplayed(), "VP: Verify that \"Lựa chọn chuyến đi\" page is displayed");
        Assertion.assertTrue(selectFlightPage.getFromPlace().contains(searchTicketData.getFrom()), "VP: Verify that the departure location is correct.");
        Assertion.assertEquals(selectFlightPage.getToPlace(), searchTicketData.getTo(), "VP: Verify that the arrival location is correct.");
        Assertion.assertEquals(selectFlightPage.getPassengerInfo(), searchTicketData.getPassenger(), "VP: Verify that the number of passengers is correct.");

        selectFlightPage.selectTheFirstCheapestTicket();
        departureInfo = selectFlightPage.getDepartureTicketInfo();
        selectFlightPage.clickContinueButton();

        selectFlightPage.selectTheFirstCheapestTicket();
        returnInfo = selectFlightPage.getReturnTicketInfo();
        selectFlightPage.clickContinueButton();

        Assertion.assertTrue(passengerInformationPage.isPageDisplayed(), "VP: Verify that the Passenger Information page is displayed.");
        Assertion.assertEquals(departureInfo, passengerInformationPage.getDepartureTicketInfo(), "VP: Verify that the departure flight ticket information is correct.");
        Assertion.assertEquals(returnInfo, passengerInformationPage.getReturnTicketInfo(), "VP: Verify that the return flight ticket information is correct.");

        Assertion.assertAll("Completed running the test case.");
    }

    SearchTicketData searchTicketData;
    TicketData departureInfo, returnInfo;
    LocalDate departureDate, returnDate;
    int totalAdult;
    HomePage homePage = new HomePage();
    SelectFlightCheapPage selectFlightCheapPage = new SelectFlightCheapPage();
    SelectFlightPage selectFlightPage = new SelectFlightPage();
    GeneralPage generalPage = new GeneralPage();
    PassengerInformationPage passengerInformationPage = new PassengerInformationPage();
    Pair<LocalDate, LocalDate> flightDate;
    String language;
}
