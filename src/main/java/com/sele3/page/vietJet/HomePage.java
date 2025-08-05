package com.sele3.page.vietJet;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.sele3.data.vietjet.Passenger;
import com.sele3.data.vietjet.SearchTicketData;
import com.sele3.enums.vietjet.LabelType;
import com.sele3.utils.DateTimeUtils;
import com.sele3.utils.YamlUtils;
import io.qameta.allure.Step;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import static com.codeborne.selenide.Selenide.*;

@Data
public class HomePage {

    public void acceptCookie() {
        acceptDialog.shouldBe(Condition.visible).click();
    }

    public void fillSearchTicketInfo(SearchTicketData searchTicketData) {
        if (Objects.nonNull(searchTicketData.getFrom())) {
            searchAndSelectLocation(LabelType.FROM, searchTicketData.getFrom());
        }
        if (Objects.nonNull(searchTicketData.getTo())) {
            searchAndSelectLocation(LabelType.TO, searchTicketData.getTo());
        }
        if (Objects.nonNull(searchTicketData.getDepartureDate())) {
            selectDate(searchTicketData.getDepartureDate());
        }
        if (Objects.nonNull(searchTicketData.getReturnDate())) {
            selectDate(searchTicketData.getReturnDate());
        }
        if (Objects.nonNull(searchTicketData.getPassenger())) {
            Passenger passenger = searchTicketData.getPassenger();
            if (Objects.nonNull(passenger.getAdultNumber())) {
                selectNumberOfAdults(passenger.getAdultNumber());
            }
            if (Objects.nonNull(passenger.getChildrenNumber())) {
                selectNumberOfChildren(passenger.getChildrenNumber());
            }
            if (Objects.nonNull(passenger.getInfantNumber())) {
                selectNumberOfInfants(passenger.getInfantNumber());
            }
        }
    }

    public void searchTicket(SearchTicketData searchTicketData) {
        fillSearchTicketInfo(searchTicketData);
        clickSearchButton();
    }

    @Step("Select date: {0}")
    public void selectDate(LocalDate localDate) {
        String localeStr = (String) YamlUtils.getProperty("config.locale");
        Locale locale = new Locale(localeStr);

        String day = String.valueOf(localDate.getDayOfMonth());
        String monthYear = localDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale));

        calendarWrapper.shouldBe(Condition.visible);

        ElementsCollection visibleMonths = $$(".rdrMonthName");
        String lastVisibleMonth = visibleMonths.get(visibleMonths.size() - 1).getText().trim();

        int monthDiff = DateTimeUtils.getMonthDiff(lastVisibleMonth, monthYear, locale);
        for (int i = 0; i < monthDiff; i++) {
            $(".rdrNextButton").click();
        }
        SelenideElement dateInCalender = $x(String.format("//div[@class='rdrMonth' and contains(.,'%s')]//span[@class='rdrDayNumber']/span[text()='%s']", monthYear, day));
        dateInCalender.shouldBe(Condition.visible).click();
    }


    @Step("Search and select location: {labelType} - {location}")
    public void searchAndSelectLocation(LabelType labelType, String location) {
        String labelText = labelType.equals(LabelType.FROM) ?
                (String) YamlUtils.getProperty("labelType.from") :
                (String) YamlUtils.getProperty("labelType.to");
        SelenideElement inputField = $x(String.format(dynamicPlaceTextBox, labelText));
        inputField.shouldBe(Condition.visible).click();
        inputField.setValue(location);
        SelenideElement dropdownOption = $$("div.MuiBox-root").findBy(Condition.text(location));
        dropdownOption.shouldBe(Condition.visible).click();
    }

    private void selectNumber(LabelType labelType, int targetNumber) {
        SelenideElement section = $x(String.format("//div[div/p[contains(.,'%s')]]/following-sibling::div", labelType.toString()));
        SelenideElement minusBtn = section.$x(".//button[1]");
        SelenideElement plusBtn = section.$x(".//button[2]");
        SelenideElement counter = section.$x(".//span[contains(@class,'MuiTypography-h2')]");

        int current = Integer.parseInt(counter.getText());

        while (current < targetNumber) {
            plusBtn.click();
            current++;
        }

        while (current > targetNumber) {
            minusBtn.click();
            current--;
        }
    }

    public void selectNumberOfAdults(Integer numberOfAdults) {
        selectNumber(LabelType.ADULT, numberOfAdults);
    }

    public void selectNumberOfChildren(Integer numberOfChildren) {
        selectNumber(LabelType.CHILDREN, numberOfChildren);
    }

    public void selectNumberOfInfants(Integer numberOfInfants) {
        selectNumber(LabelType.INFANT, numberOfInfants);
    }

    @Step("Click on Search button")
    public void clickSearchButton() {
        String buttonText = (String) YamlUtils.getProperty("button.letGo");
        $x(String.format(button, buttonText)).scrollIntoCenter().shouldBe(Condition.visible).click();
    }

    private SelenideElement acceptDialog = $x("//div[@role='dialog']//span/h5");
    private SelenideElement lateButton = $("#NC_CTA_TWO");
    private SelenideElement iframe = $("#preview-notification-frame");
    private ElementsCollection placeDropdownOption = $$("div.MuiBox-root");
    private String button = "//div[contains(@class, 'MuiPaper-root')]//button[span[.=\"%s\"]]";
    private String dynamicPlaceTextBox = "//div[label[text()='%s']]//input";
    private SelenideElement calendarWrapper = $x("//div[contains(@class, 'rdrCalendarWrapper')]");
}