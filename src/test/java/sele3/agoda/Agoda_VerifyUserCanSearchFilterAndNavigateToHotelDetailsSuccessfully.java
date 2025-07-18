package sele3.agoda;

import com.sele3.data.agoda.HotelData;
import com.sele3.data.agoda.HotelDetailPage;
import com.sele3.data.agoda.ReviewPointData;
import com.sele3.data.agoda.SearchHotelData;
import com.sele3.enums.agoda.FilterType;
import com.sele3.enums.agoda.PropertyFacilitiesOptions;
import com.sele3.enums.agoda.ReviewType;
import com.sele3.page.agoda.HomePage;
import com.sele3.page.agoda.SearchResultPage;
import com.sele3.page.general.GeneralPage;
import com.sele3.utils.Assertion;
import com.sele3.utils.WebUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sele3.TestBase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Agoda_VerifyUserCanSearchFilterAndNavigateToHotelDetailsSuccessfully extends TestBase {

    @BeforeMethod(alwaysRun = true)
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
        filterType = FilterType.PROPERTY_FACILITIES;
        swimmingPool = PropertyFacilitiesOptions.SWIMMING_POOL;
        expectedReviewTypes = Arrays.asList(ReviewType.values());
    }

    @Test(groups = "agoda", description = "TC03 - Agoda - Search, filter and navigate to hotel details successfully")
    public void Agoda_VerifyUserCanSearchFilterAndNavigateToHotelDetailsSuccessfully() {
        generalPage.openPage();
        homePage.closeAds();
        homePage.searchHotel(searchHotelData);
        Assertion.assertTrue(searchResultPage.areAllDisplayedDestinationsRelevant(5, detination), String.format("VP1: Check the first 5 destinations have search content: %s", detination));

        searchResultPage.filterByOption(FilterType.PROPERTY_FACILITIES, PropertyFacilitiesOptions.SWIMMING_POOL, true);
        the5thHotelData = searchResultPage.getHotelDataByIndex(5);
        searchResultPage.clickOnHotelByIndex(5);
        Assertion.assertEquals(hotelDetailPage.getName(), the5thHotelData.getName(), "VP2: Check the hotel detail page displays correct hotel name");
        Assertion.assertTrue(hotelDetailPage.getAddress().contains(the5thHotelData.getAddress()), "VP3: Check the hotel detail page displays correct destination");
        Assertion.assertTrue(hotelDetailPage.getSpecialBenefitList().contains(PropertyFacilitiesOptions.SWIMMING_POOL.toString()), "VP4: Check the hotel detail page displays correct the selected filter");

        WebUtils.closeCurrentTabAndSwitchBack();
        reviewPointDataInSearchPage = searchResultPage.getListReviewPointOfHotelByIndex(1);
        actualReviewTypes = reviewPointDataInSearchPage.stream()
                .map(ReviewPointData::getReviewName)
                .sorted()
                .collect(Collectors.toList());
        Assertion.assertTrue(expectedReviewTypes.containsAll(actualReviewTypes), "VP5: Check the detail review popup appears with correct information");

        firstHotelData = searchResultPage.getHotelDataByIndex(1);
        searchResultPage.clickOnHotelByIndex(1);
        reviewPointDataInDetailPage = hotelDetailPage.getListReviewPointOfHotel();
        Assertion.assertEquals(hotelDetailPage.getName(), firstHotelData.getName(), "VP6: Check the hotel detail page displays correct hotel name");
        Assertion.assertTrue(hotelDetailPage.getAddress().contains(firstHotelData.getAddress()), "VP7: Check the hotel detail page displays correct destination");
        Assertion.assertTrue(hotelDetailPage.getSpecialBenefitList().contains(PropertyFacilitiesOptions.SWIMMING_POOL.toString()), "VP8: Check the hotel detail page displays correct the selected filter");

        reviewPointDataInDetailPage.sort(Comparator.comparing(ReviewPointData::getReviewName));
        reviewPointDataInSearchPage.sort(Comparator.comparing(ReviewPointData::getReviewName));
        Assertion.assertEquals(reviewPointDataInDetailPage, reviewPointDataInSearchPage, "VP9: Check the hotel detail page displays correct review");

        Assertion.assertAll("Complete running test case");
    }

    HomePage homePage = new HomePage();
    GeneralPage generalPage = new GeneralPage();
    SearchResultPage searchResultPage = new SearchResultPage();
    HotelDetailPage hotelDetailPage = new HotelDetailPage();
    String detination;
    LocalDate nextFriday, threeDaysFromNextFriday;
    SearchHotelData searchHotelData;
    HotelData the5thHotelData, firstHotelData;
    FilterType filterType;
    PropertyFacilitiesOptions swimmingPool;
    List<String> benifitList;
    List<ReviewType> expectedReviewTypes;
    List<ReviewType> actualReviewTypes;
    List<ReviewPointData> reviewPointDataInSearchPage;
    List<ReviewPointData> reviewPointDataInDetailPage;
}