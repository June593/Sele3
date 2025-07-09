package sele3.agoda;

import com.sele3.data.agoda.FilterHotelData;
import com.sele3.enums.agoda.FilterType;
import com.sele3.data.agoda.SearchHotelData;
import com.sele3.enums.agoda.StarRatingOption;
import com.sele3.page.agoda.HomePage;
import com.sele3.page.agoda.SearchResultPage;
import com.sele3.page.general.GeneralPage;
import com.sele3.utils.Assertion;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sele3.TestBase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class Agoda_VerifyUserCanSearchAndFilterHotelSuccessfully extends TestBase {

    @BeforeMethod
    public void setUp() {
        detination = "Da Nang";
        nextFriday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        threeDaysFromNextFriday = nextFriday.plusDays(3);
        searchHotelData = SearchHotelData.builder()
                .place(detination)
                .fromDate(nextFriday)
                .toDate(threeDaysFromNextFriday)
                .isDayUseStays(false)
                .occupancy(SearchHotelData.Occupancy.builder()
                        .room(2)
                        .adults(4)
                        .build())
                .build();

        filterHotelData = FilterHotelData.builder()
                .minPrice(500000)
                .maxPrice(5000000)
                .filters(List.of(
                        FilterHotelData.Filter.builder()
                                .filterType(FilterType.STAR_RATING)
                                .filterValue(StarRatingOption.RATING_FOUR_STAR)
                                .build()
                ))
                .build();
    }

    @Test(description = "TC02 - Agoda - Search and filter hotel successfully")
    public void agoda_VerifyUserCanSearchAndFilterHotelSuccessfully() {
        generalPage.openPage();
        homePage.closeAds();

        homePage.searchHotel(searchHotelData);
        searchResultPage.scrollUntilAllHotelDataLoaded(5);
        Assertion.assertTrue(searchResultPage.areAllDisplayedDestinationsRelevant(5, detination), String.format("VP: Verify that the first 5 destinations contain the search keyword: %s", detination));

        defaultMinPrice = searchResultPage.getMinPrice();
        defaultMaxPrice = searchResultPage.getMaxPrice();

        searchResultPage.filterHotel(filterHotelData);
        Assertion.assertTrue(searchResultPage.areAllSelectedFiltersHighlighted(filterHotelData), String.format("VP: Verify that all selected filters are highlighted."));

        searchResultPage.scrollUntilAllHotelDataLoaded(5);
        Assertion.assertTrue(searchResultPage.areAllDisplayedDestinationsRelevant(5, detination), String.format("VP: Verify that the first 5 destinations contain the search keyword: %s", detination));

        Assertion.assertTrue(searchResultPage.areAllPricesInExpectedRange(5, filterHotelData.getMinPrice(), filterHotelData.getMaxPrice()), String.format("VP: Verify that the prices of the first 5 results are within the range: %d - %d", filterHotelData.getMinPrice(), filterHotelData.getMaxPrice()));
        Assertion.assertTrue(searchResultPage.areAllResultsWithinSelectedRating(5, StarRatingOption.RATING_FOUR_STAR), String.format("VP: Verify that the star ratings of the first 5 results match the selected rating: %s", StarRatingOption.RATING_FOUR_STAR));

        searchResultPage.filterByPriceRange(defaultMinPrice, defaultMaxPrice, true);

        Assertion.assertEquals(searchResultPage.getMinPriceSlider(), defaultMinPrice, String.format("VP: Verify that the minimum price in the slider is reset to the default value: %d", defaultMinPrice));
        Assertion.assertEquals(searchResultPage.getMaxPriceSlider(), defaultMaxPrice, String.format("VP: Verify that the maximum price in the slider is reset to the default value: %d", defaultMaxPrice));

        Assertion.assertAll("Complete running test case");
    }

    HomePage homePage = new HomePage();
    GeneralPage generalPage = new GeneralPage();
    String detination;
    LocalDate nextFriday, threeDaysFromNextFriday;
    SearchHotelData searchHotelData;
    FilterHotelData filterHotelData;
    int defaultMinPrice, defaultMaxPrice;
    SearchResultPage searchResultPage = new SearchResultPage();
}