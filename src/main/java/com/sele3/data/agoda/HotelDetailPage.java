package com.sele3.data.agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.sele3.enums.agoda.ReviewType;
import com.sele3.utils.Constants;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Selenide.*;

public class HotelDetailPage {

    /**
     * Get the hotel name.
     */
    public String getName() {
        return name.shouldBe(Condition.visible).getText().trim();
    }

    /**
     * Get the hotel address (before the first hyphen).
     */
    public String getAddress() {
        return address.shouldBe(Condition.visible).getText();
    }

    /**
     * Get the list of special benefits offered by the hotel.
     *
     * @return List of special benefit texts. Returns empty list if none are visible.
     */
    public List<String> getSpecialBenefitList() {
        specialBenefits.shouldBe(CollectionCondition.allMatch("All benefits must be visible",
                WebElement::isDisplayed), Duration.ofSeconds(10));
        return specialBenefits.stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toList());
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

    private SelenideElement name = $("h1[data-selenium='hotel-header-name']");
    private SelenideElement address = $("span[data-selenium='hotel-address-map']");
    private ElementsCollection specialBenefits = $$x("//div[@data-element-name='property-feature']");
    private SelenideElement showReviewTooltipButton = $("button[data-testid='review-tooltip-icon']");
    private SelenideElement reviewTooltip = $("div[data-testid='floater-container']");
    private ElementsCollection reviewName = $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//span");
    private ElementsCollection reviewPoint = $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//p");
}
