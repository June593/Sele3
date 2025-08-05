package com.sele3.controls;

import com.codeborne.selenide.*;
import io.qameta.allure.Step;

import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.*;

public class CalendarComponent {

    private final SelenideElement previousMonthButton;
    private final SelenideElement nextMonthButton;
    private final ElementsCollection calendarDates;

    public CalendarComponent(SelenideElement rootElement) {
        this.previousMonthButton = rootElement.$("[data-selenium='calendar-previous-month-button']");
        this.nextMonthButton = rootElement.$("[data-selenium='calendar-next-month-button']");
        this.calendarDates = rootElement.$$("[data-selenium-date]");
    }

    @Step("Select date: {date}")
    public void selectDate(LocalDate date) {
        while (true) {
            LocalDate minDate = getMinimumDateVisible();
            LocalDate maxDate = getMaximumDateVisible();

            if (date.isBefore(minDate)) {
                previousMonthButton.click();
            } else if (date.isAfter(maxDate)) {
                nextMonthButton.click();
            } else {
                break;
            }
        }

        getDateElement(date).click();
    }

    private LocalDate getMinimumDateVisible() {
        return LocalDate.parse(calendarDates.first().getAttribute("data-selenium-date"));
    }

    private LocalDate getMaximumDateVisible() {
        return LocalDate.parse(calendarDates.last().getAttribute("data-selenium-date"));
    }

    private SelenideElement getDateElement(LocalDate date) {
        return calendarDates.findBy(Condition.attribute("data-selenium-date", date.toString()))
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled);
    }
}