package com.sele3.page.agoda;

import com.codeborne.selenide.*;
import com.sele3.data.agoda.HotelData;
import com.sele3.data.agoda.ReviewPointData;
import com.sele3.enums.agoda.FilterType;
import com.sele3.enums.agoda.FilterValue;
import com.sele3.data.agoda.FilterHotelData;
import com.sele3.enums.agoda.ReviewType;
import com.sele3.enums.agoda.StarRatingOption;
import com.sele3.utils.Common;
import com.sele3.utils.Constants;
import com.sele3.utils.WebUtils;
import com.sele3.utils.YamlUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Selenide.*;

public class SearchResultPage {

    /**
     * Get all the hotel prices
     *
     * @param hotelNumber Number of hotel need to get
     * @return List of all prices
     */
    public List<Integer> getPriceList(Integer hotelNumber) {
        List<Integer> allPrices = new ArrayList<>();
        for (int i = 0; i < hotelNumber; i++) {
            SelenideElement item = price.get(i);
            item.scrollIntoView(true);
            item.shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT);
            Integer text = Integer.parseInt(item.getText().replaceAll("[^0-9]", ""));
            allPrices.add(text);
        }
        return allPrices;
    }

    /**
     * Get a list of destination names from the search result.
     * Scrolls to each destination element before retrieving its text.
     *
     * @param hotelNumber Number of destinations to retrieve (nullable). If null, return all.
     * @return List of destination names
     */
    public List<String> getDestinationList(Integer hotelNumber) {
        List<String> allDestinations = new ArrayList<>();
        for (int i = 0; i < hotelNumber; i++) {
            SelenideElement item = destination.get(i);
            item.scrollIntoView(true);
            String text = item.getText().split(" - ")[0].trim();
            allDestinations.add(text);
        }
        return allDestinations;
    }


    /**
     * Are all the destinations have search content
     *
     * @param destinationNumber Number of destinations need to check
     * @param place             Place name
     * @return
     */
    public boolean areAllDisplayedDestinationsRelevant(Integer destinationNumber, String place) {
        List<String> destinationList = getDestinationList(destinationNumber);
        if (Objects.isNull(destinationList)) {
            Allure.step("No result found", Status.FAILED);
            return false;
        }
        for (String str : destinationList) {
            String lowerStr = str.toLowerCase();
            String lowerPlace = place.toLowerCase();
            if (!lowerStr.contains(lowerPlace) && !lowerStr.contains(String.join("", lowerPlace.split(" ")))) {
                Allure.step(String.format("Destination '%s' does not contain '%s'", str, place), Status.FAILED);
                return false;
            }
        }
        return true;
    }

    @Step("Click 'Lowes price first' button")
    public void clickLowestPriceFirstButton() {
        lowestPriceButton.scrollIntoView(false);
        lowestPriceButton.click();
        waitForHotelImgLoading();
    }

    @Step("Filter by Price Range: {0} - {1}")
    public void filterByPriceRange(Integer minPrice, Integer maxPrice, boolean isWaitDataLoading) {
        minPriceTextBox.setValue(minPrice.toString()).pressEnter();
        maxPriceTextBox.setValue(maxPrice.toString()).pressEnter();
        if (isWaitDataLoading) {
            waitForHotelImgLoading();
        }
    }

    public void waitForHotelImgLoading() {
        for (int i = 0; i < hotelImg.size(); i++) {
            SelenideElement img = hotelImg.get(i);
            img.shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT);
            img.shouldBe(Condition.image, Constants.MEDIUM_TIMEOUT);
        }
    }

    private SelenideElement getSelectedCheckboxByValue(String value) {
        return $x(String.format("//div[@id='SideBarLocationFilters']//legend[@id='filter-menu-RecentFilters']//following-sibling::ul//li//label[@data-element-value=%s]//input", value))
                .shouldBe(Condition.exist);
    }

    public int getMinPrice() {
        return Common.changeMoneyValueToOnlyMoneyNumber(minPriceTextBox.getValue());
    }

    public int getMaxPrice() {
        return Common.changeMoneyValueToOnlyMoneyNumber(maxPriceTextBox.getValue());
    }

    /**
     * Filter hotel with info
     *
     * @param filterHotelData
     */
    public void filterHotel(FilterHotelData filterHotelData) {
        filterByPriceRange(filterHotelData.getMinPrice(), filterHotelData.getMaxPrice(), false);
        for (FilterHotelData.Filter filter : filterHotelData.getFilters()) {
            filterByOption(filter.getFilterType(), filter.getFilterValue(), true);
        }
    }

    @Step("Filter by type: {0}, option: {1}")
    public void filterByOption(FilterType filterType, FilterValue filterValue, boolean isWaitDataLoading) {
        getCheckboxByFilterTypeAndOption(filterType, filterValue).click();
        if (isWaitDataLoading) {
            waitForHotelImgLoading();
        }
    }

    private SelenideElement getCheckboxByFilterTypeAndOption(FilterType filterType, FilterValue filterValue) {
        return $x(String.format("//legend[contains(., '%s')]/..//span[.='%s']//ancestor::label//input", filterType.toString(), filterValue.toString()))
                .shouldBe(Condition.exist).scrollIntoView(true);
    }

    public boolean areAllSelectedFiltersHighlighted(FilterHotelData filterHotelData) {
        List<String> optionValue = filterHotelData.getFilters()
                .stream()
                .map(f -> f.getFilterValue().toString())
                .toList();

        return optionValue.stream()
                .allMatch(value -> {
                    String actualValue = value.matches("\\d+-Star.*") ? value.split("-")[0].trim() : value;
                    return getSelectedCheckboxByValue(actualValue).isSelected();
                })
                && getMinPriceSlider() == filterHotelData.getMinPrice()
                && getMaxPriceSlider() == filterHotelData.getMaxPrice();
    }

    public int getMinPriceSlider() {
        String min = (String) YamlUtils.getProperty("slider.min");
        return Common.changeMoneyValueToOnlyMoneyNumber(getPriceSlider(min).getAttribute("aria-valuetext"));
    }

    public int getMaxPriceSlider() {
        String max = (String) YamlUtils.getProperty("slider.max");
        return Common.changeMoneyValueToOnlyMoneyNumber(getPriceSlider(max).getAttribute("aria-valuetext"));
    }

    private SelenideElement getPriceSlider(String text) {
        return $x(String.format("//div[@id='SideBarLocationFilters']//div[@aria-label='%s']", text))
                .shouldBe(Condition.visible);
    }

    /**
     * Check if all hotel prices are within the expected price range.
     *
     * @param hotelNumber The number of hotels to check
     * @param minPrice    The minimum price (inclusive)
     * @param maxPrice    The maximum price (inclusive)
     * @return true if all prices are within range, false otherwise
     */
    public boolean areAllPricesInExpectedRange(Integer hotelNumber, int minPrice, int maxPrice) {
        List<Integer> prices = getPriceList(hotelNumber);

        if (prices == null || prices.isEmpty()) {
            Allure.step("No prices found to validate.", Status.FAILED);
            return false;
        }

        for (Integer price : prices) {
            if (price < minPrice || price > maxPrice) {
                Allure.step(String.format("Price '%d' is out of expected range [%d - %d]", price, minPrice, maxPrice), Status.FAILED);
                return false;
            }
        }
        return true;
    }

    /**
     * Are all the rating stars matched with selected rating
     *
     * @param hotelNumber Number of hotel need to check
     * @param starRating  The selected rating
     * @return
     */
    public boolean areAllResultsWithinSelectedRating(Integer hotelNumber, StarRatingOption starRating) {
        List<Float> stars = getRatingList(hotelNumber);
        String rawText = YamlUtils.getProperty("star_rating_options." + starRating.getValue()).toString();
        int baseRating = Integer.parseInt(rawText.split("-")[0].trim());
        if (stars == null || stars.isEmpty()) {
            Allure.step("No rating results found", Status.FAILED);
            return false;
        }

        for (Float star : stars) {
            if (star < baseRating || star >= baseRating + 1) {
                Allure.step(String.format(
                        "Rating %.1f is out of expected range [%d.0 - %.1f)",
                        star, baseRating, baseRating + 1.0), Status.FAILED);
                return false;
            }
        }
        return true;
    }

    /**
     * Get the rating list of hotels displayed on the page.
     *
     * @param hotelNumber Number of hotels to retrieve ratings for (optional). If null, get all.
     * @return List of Float ratings
     */
    public List<Float> getRatingList(Integer hotelNumber) {
        List<String> allRatings = rating.texts();

        if (hotelNumber != null) {
            if (hotelNumber > allRatings.size()) {
                String errorMessage = String.format(
                        "Page only displays %d result(s), but requested %d. Scroll down to load more results.",
                        allRatings.size(), hotelNumber
                );
                Allure.step(errorMessage, Status.FAILED);
                throw new IllegalStateException(errorMessage);
            }
            allRatings = allRatings.subList(0, hotelNumber);
        }

        try {
            return allRatings.stream()
                    .map(text -> Float.parseFloat(text.split(" ")[0]))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            Allure.step("Failed to parse rating from text: " + e.getMessage(), Status.FAILED);
            throw new RuntimeException("Unable to parse rating value", e);
        }
    }

    /**
     * Get hotel data by index
     *
     * @param index
     * @return
     */
    public HotelData getHotelDataByIndex(int index) {
        return HotelData.builder()
                .name(getHotelNameList(index).get(index - 1))
                .address(getDestinationList(index).get(index - 1))
                .price(getPriceList(index).get(index - 1))
                .build();
    }

    /**
     * Get a list of hotel names from the search result.
     *
     * @param hotelNumber Number of hotels to retrieve
     * @return List of hotel names
     */
    public List<String> getHotelNameList(int hotelNumber) {
        List<String> allHotelNames = new ArrayList<>();
        for (int i = 0; i < hotelNumber; i++) {
            SelenideElement item = hotelName.get(i);
            item.scrollIntoView(true);
            String text = item.getText();
            allHotelNames.add(text);
        }
        return allHotelNames;
    }

    @Step("Click on hotel at index: {0}")
    public void clickOnHotelByIndex(int index) {
        hotelName.get(index - 1)
                .scrollIntoView(false)
                .shouldBe(Condition.visible).click();
        WebUtils.switchToNextTab();
    }

    @Step("Hover on point of the hotel button at index: {0}")
    public void hoverOnPointOfHotelButton(int index) {
        pointOfHotelButton.get(index - 1)
                .scrollIntoView(false)
                .shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT)
                .hover();
    }

    /**
     * Get the list of review points of a hotel by its index
     *
     * @param hotelIndex the index of the hotel
     * @return List of ReviewPointData
     */
    public List<ReviewPointData>getListReviewPointOfHotelByIndex(int hotelIndex) {
        hoverOnPointOfHotelButton(hotelIndex);

        return IntStream.range(0, reviewName.size())
                .mapToObj(i -> {
                    String nameText = reviewName.get(i).shouldBe(Condition.visible).getText();
                    String pointText = reviewPoint.get(i).shouldBe(Condition.visible).getText();

                    return ReviewPointData.builder()
                            .reviewName(ReviewType.fromText(nameText))
                            .point(Float.parseFloat(pointText))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ElementsCollection destination = $$x("//li[@data-selenium='hotel-item']//div[@data-selenium='area-city']");
    private ElementsCollection price = $$x("//div[@data-element-name='final-price']");
    private ElementsCollection hotelImg = $$x("//li[@data-selenium='hotel-item']//button[@data-element-name='ssrweb-mainphoto']/img");
    private SelenideElement lowestPriceButton = $x("//button[@data-element-name='search-sort-price']");
    private SelenideElement minPriceTextBox = $("#price_box_0");
    private SelenideElement maxPriceTextBox = $("#price_box_1");
    private ElementsCollection rating = $$x("//div[@data-testid='rating-container']//span");
    private ElementsCollection hotelName = $$x("//*[self::h3[@data-selenium='hotel-name'] or self::a[@data-testid='property-name-link']//span]");
    private ElementsCollection pointOfHotelButton = $$(".ReviewWithDemographic");
    private ElementsCollection reviewName = $$(".review-bar__name");
    private ElementsCollection reviewPoint = $$(".review-bar__point");
}
