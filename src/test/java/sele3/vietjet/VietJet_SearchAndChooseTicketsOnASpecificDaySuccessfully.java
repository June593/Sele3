package sele3.vietjet;

import com.sele3.data.vietjet.Passenger;
import com.sele3.data.vietjet.SearchTicketData;
import com.sele3.data.vietjet.TicketData;
import com.sele3.page.general.GeneralPage;
import com.sele3.page.vietJet.HomePage;
import com.sele3.page.vietJet.PassengerInformationPage;
import com.sele3.page.vietJet.SelectFlightPage;
import com.sele3.utils.Assertion;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sele3.TestBase;

import java.time.LocalDate;
import java.time.MonthDay;

@Slf4j
public class VietJet_SearchAndChooseTicketsOnASpecificDaySuccessfully extends TestBase {
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        departureDate = LocalDate.now().plusDays(1);
        returnDate = LocalDate.now().plusDays(4);

        searchTicketData = SearchTicketData.builder()
                .from("Ho Chi Minh")
                .to("Ha Noi")
                .departureDate(departureDate)
                .returnDate(returnDate)
                .passenger(Passenger.builder()
                        .adultNumber(2)
                        .infantNumber(0)
                        .childrenNumber(0)
                        .build())
                .build();
    }

    @Test(groups = {"smoke", "regression"}, description = "TC01 - VietJet - Search and choose tickets on a specific day successfully")
    public void VietJet_SearchAndChooseTicketsOnASpecificDaySuccessfully() {
        generalPage.openPage();
        homePage.acceptCookie();
        homePage.closeNotificationModal();

        homePage.searchTicket(searchTicketData);
        selectFlightPage.closeAdsDialogIfVisible();

        Assertion.assertTrue(selectFlightPage.isSelectTravelOptionsPageDisplayed(), "VP: Verify that the Select Travel Options page is displayed.");
        Assertion.assertTrue(selectFlightPage.allPricesHaveVND(), "VP: Verify that all ticket prices are displayed in VND.");
        Assertion.assertEquals(selectFlightPage.getHighlightedMonthDay(), MonthDay.from(departureDate), "VP: Verify that the return flight date is displayed correctly.");
        Assertion.assertEquals(selectFlightPage.getFromPlace(), searchTicketData.getFrom(), "VP: Verify that the departure location is correct.");
        Assertion.assertEquals(selectFlightPage.getToPlace(), searchTicketData.getTo(), "VP: Verify that the arrival location is correct.");
        Assertion.assertEquals(selectFlightPage.getPassengerInfo(), searchTicketData.getPassenger(), "VP: Verify that the number of passengers is correct.");

        selectFlightPage.selectTheFirstCheapestTicket();
        departureInfo = selectFlightPage.getDepartureTicketInfo();
        selectFlightPage. clickContinueButton();
        selectFlightPage.selectTheFirstCheapestTicket();
        returnInfo = selectFlightPage.getReturnTicketInfo();
        selectFlightPage. clickContinueButton();

        Assertion.assertTrue(passengerInformationPage.isPageDisplayed(), "VP: Verify that the Passenger Information page is displayed.");
        Assertion.assertEquals(departureInfo, passengerInformationPage.getDepartureTicketInfo(), "VP: Verify that the departure flight information is correct.");
        Assertion.assertEquals(returnInfo, passengerInformationPage.getReturnTicketInfo(), "VP: Verify that the return flight information is correct.");

        Assertion.assertAll("Complete running test case");
    }

    GeneralPage generalPage = new GeneralPage();
    HomePage homePage = new HomePage();
    SelectFlightPage selectFlightPage = new SelectFlightPage();
    PassengerInformationPage passengerInformationPage = new PassengerInformationPage();
    LocalDate departureDate;
    LocalDate returnDate;
    TicketData departureInfo;
    TicketData returnInfo;
    SearchTicketData searchTicketData;
}
