package com.sele3.page.agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.sele3.utils.Constants;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;

public class SearchResultPage {

    /**
     * Scroll page until number hotels are loaded as expected
     *
     * @param numberHotel
     */
    @Step("Scroll until {numberHotel} hotels loaded data")
    public void scrollUntilAllHotelDataLoaded(int numberHotel) {
        int retries = 0;
        int maxRetries = 20;
        while (price.size() < numberHotel && retries++ < maxRetries) {
            waitForHotelImgLoading();

            if (!price.isEmpty()) {
                price.last()
                        .shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT)
                        .scrollIntoView(true);
            }
            Selenide.sleep(500);
        }

        if (price.size() < numberHotel) {
            Allure.step("Only " + price.size() + " hotels loaded, expected: " + numberHotel, Status.FAILED);
        }

        header.scrollIntoView(true);
        waitForHotelImgLoading();
    }

    /**
     * Get all the hotel prices
     *
     * @param hotelNumber Number of hotel need to get
     * @return List of all prices
     */
    public List<Integer> getPriceList(Integer hotelNumber) {
        List<String> allPrices = price.texts();
        if (Objects.nonNull(hotelNumber)) {
            if (hotelNumber > allPrices.size()) {
                String errorMessage = String.format("Page only show %d instead of %d result(s). Scroll down for more results", allPrices.size(), hotelNumber);
                Allure.step(String.format(errorMessage), Status.FAILED);
                throw new IllegalStateException(errorMessage);
            }
                allPrices = allPrices.subList(0, hotelNumber);
        }
        return allPrices.stream()
                .map(e -> e.replaceAll("[^0-9]", ""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Get all the hotel area city
     *
     * @param hotelNumber Number of hotel need to get
     * @return List of all area city
     */
    public List<String> getDestinationList(Integer hotelNumber) {
        List<String> allDestinations = destination.texts();
        if (Objects.nonNull(hotelNumber)) {
            if (hotelNumber > allDestinations.size()) {
                String errorMessage = String.format("Page only show %d instead of %d result(s). Scroll down for more results", allDestinations.size(), hotelNumber);
                Allure.step(String.format(errorMessage), Status.FAILED);
                throw new IllegalStateException(errorMessage);
            }
                allDestinations = allDestinations.subList(0, hotelNumber);
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
    public boolean areAllTheDestinationsHaveSearchContent(Integer destinationNumber, String place) {
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
        hotelImg.forEach(img ->
                img.shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT)
                        .shouldBe(Condition.image, Constants.MEDIUM_TIMEOUT)
        );
    }

    private SelenideElement getSelectedCheckboxByValue(int dataValue) {
        return $x(String.format("//div[@id='SideBarLocationFilters']//legend[@id='filter-menu-RecentFilters']//following-sibling::ul//li//label[@data-element-value='%d']//input", dataValue))
                .shouldBe(Condition.exist);
    }

    private ElementsCollection destination = $$x("//li[@data-selenium='hotel-item']//div[@data-selenium='area-city']");
    private ElementsCollection price = $$x("//div[@data-element-name='final-price']");
    private ElementsCollection hotelImg = $$x("//li[@data-selenium='hotel-item']//button[@data-element-name='ssrweb-mainphoto']/img");
    private SelenideElement lowestPriceButton = $x("//button[@data-element-name='search-sort-price']");
    private SelenideElement header = $("header");
    private SelenideElement minPriceTextBox = $("#price_box_0");
    private SelenideElement maxPriceTextBox = $("#price_box_1");
}
