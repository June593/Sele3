package sele3.agoda;

import com.sele3.data.agoda.SearchHotelData;
import com.sele3.page.agoda.HomePage;
import com.sele3.page.agoda.SearchResultPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.Common;
import lombok.extern.slf4j.Slf4j;
import sele3.TestBase;
import com.sele3.page.general.GeneralPage;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
public class Agoda_VerifyUserCanSearchAndSortHotelSuccessfully extends TestBase {
    @BeforeMethod
    public void setUp() {
        destination = "Da Nang";
        nextFriday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        threeDaysFromNextFriday = nextFriday.plusDays(3);
        searchHotelData = SearchHotelData.builder()
                .place(destination)
                .fromDate(nextFriday)
                .toDate(threeDaysFromNextFriday)
                .isDayUseStays(false)
                .occupancy(SearchHotelData.Occupancy.builder()
                        .room(2)
                        .adults(4)
                        .build())
                .build();
    }

    @Test(description = "TC01 - Agoda - Search and sort hotel successfully")
    public void agoda_VerifyUserCanSearchAndSortHotelSuccessfully() {
        generalPage.openPage();
        homePage.closeAds();
        homePage.searchHotel(searchHotelData);
        searchResultPage.scrollUntilAllHotelDataLoaded(5);

        Assertion.assertTrue(searchResultPage.areAllDisplayedDestinationsRelevant(5, destination), String.format("VP: Verify that the first 5 hotel destinations are relevant to the search keyword: %s", destination));

        searchResultPage.clickLowestPriceFirstButton();
        searchResultPage.scrollUntilAllHotelDataLoaded(5);
        priceList = searchResultPage.getPriceList(5);

        Assertion.assertEquals(priceList, Common.sort(priceList), "VP: Verify that the first 5 hotel prices are sorted in ascending order");
        Assertion.assertTrue(searchResultPage.areAllDisplayedDestinationsRelevant(5, destination), "VP: Verify the destination names remain accurate after sorting the results");

        Assertion.assertAll("Complete running test case");
    }

    HomePage homePage = new HomePage();
    GeneralPage generalPage = new GeneralPage();
    String destination;
    LocalDate nextFriday, threeDaysFromNextFriday;
    SearchHotelData searchHotelData;
    SearchResultPage searchResultPage = new SearchResultPage();
    List<Integer> priceList;
}
