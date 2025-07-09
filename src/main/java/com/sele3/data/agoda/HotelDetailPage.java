package com.sele3.data.agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.sele3.enums.agoda.ReviewType;
import com.sele3.utils.Constants;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Selenide.*;

public class HotelDetailPage {

    /**
     * Get the hotel name.
     */
    public String getName() {
        return name.getText().trim();
    }

    /**
     * Get the hotel address (before the first hyphen).
     */
    public String getAddress() {
        return address.getText().split("-")[0].trim();
    }

    /**
     * Get the list of special benefits offered by the hotel.
     */
    public List<String> getSpecialBenefitList() {
        return specialBenefits.texts();
    }

    @Step("Hover on point of the hotel button ")
    public void hoverOnPointOfHotelButton() {
        showReviewTooltipButton
                .shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT)
                .scrollIntoView(false)
                .hover();
        reviewTooltip.shouldBe(Condition.visible, Constants.MEDIUM_TIMEOUT);
    }

    /**
     * Get the list of review points of a hotel
     *
     * @return List of ReviewPointData
     */
    public List<ReviewPointData> getListReviewPointOfHotel() {
        hoverOnPointOfHotelButton();
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

    /**
     * Check if the hotel address on the detail page matches the one from the result page.
     *
     * @param addressInDetailsPage The full address from the detail page
     * @param addressInResultPage  The partial address from the search result page
     * @return true if the detail page address contains the result page address
     */
    public boolean isHotelAddressMatched(String addressInDetailsPage, String addressInResultPage) {
        if (!addressInDetailsPage.contains(addressInResultPage)) {
            Allure.step(String.format(
                    "The address on the detail page '%s' does not contain '%s'",
                    addressInDetailsPage, addressInResultPage
            ), Status.FAILED);
            return false;
        }
        return true;
    }

    private SelenideElement name = $("h1[data-selenium='hotel-header-name']");
    private SelenideElement address = $("span[data-selenium='hotel-address-map']");
    private ElementsCollection specialBenefits = $$("div[data-element-value='available']");
    private SelenideElement showReviewTooltipButton = $("button[data-testid='review-tooltip-icon']");
    private SelenideElement reviewTooltip = $("div[data-testid='floater-container']");
    private ElementsCollection reviewName = $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//span");
    private ElementsCollection reviewPoint = $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//p");
}
