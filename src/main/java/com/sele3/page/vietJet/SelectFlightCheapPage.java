package com.sele3.page.vietJet;

import com.codeborne.selenide.*;
import com.sele3.enums.vietjet.FlightDirection;
import com.sele3.utils.Constants;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.sleep;

public class SelectFlightCheapPage extends SelectFlightPage {

    public boolean isPageDisplayed() {
        return WebDriverRunner.url().endsWith("/select-flight-cheap");
    }

    /**
     * Find the cheapest flight pair within a given month range.
     *
     * @param daysTrip   Number of days between departure and return.
     * @param monthRange Number of months to search.
     * @return Pair of departure and return dates with the lowest total price.
     */
    @Step("Find cheapest departure-return pair flight within {monthRange} months and {daysTrip} days trip")
    public Pair<LocalDate, LocalDate> findCheapestDepartureReturnPairFlight(int daysTrip, int monthRange) {
        scrollUntilLastDateOfReturnMonthPrices();
        loadingImg.shouldBe(Condition.hidden, Constants.MEDIUM_TIMEOUT);
        int minPrice = Integer.MAX_VALUE;
        Pair<LocalDate, LocalDate> cheapestFlight = null;
        YearMonth currentMonth = getSelectedMonthDepartureFlight();

        for (int i = 0; i < monthRange; i++) {
            int firstDay = i == 0 ? getFirstAvailableFlightDate() : 1;
            int lastDay = currentMonth.lengthOfMonth();

            for (int day = firstDay; day <= lastDay; day++) {
                LocalDate departureDate = currentMonth.atDay(day);
                LocalDate returnDate = departureDate.plusDays(daysTrip);

                int departurePrice = getPriceByDate(FlightDirection.DEPARTURE, departureDate);
                int returnPrice = getPriceByDate(FlightDirection.RETURN, returnDate);
                int totalPrice = departurePrice + returnPrice;

                Allure.step(String.format("Departure: %s (%s VND), Return: %s (%s VND), Total: %s VND",
                        departureDate, departurePrice, returnDate, returnPrice, totalPrice));

                if (totalPrice < minPrice) {
                    minPrice = totalPrice;
                    cheapestFlight = Pair.of(departureDate, returnDate);
                    Allure.step(String.format("Found cheaper flight: %s VND", minPrice));
                }
            }

            currentMonth = currentMonth.plusMonths(1); // move to next month
        }

        return cheapestFlight;
    }

    @Step("Get selected month for {flightDirection} flight")
    public YearMonth getSelectedMonth(FlightDirection flightDirection) {
        SelenideElement element = getFlightSectionContainer(flightDirection)
                .$x("./following-sibling::div//div[contains(@class, 'slick-current')]//p");
        return YearMonth.parse(element.getText(), DateTimeFormatter.ofPattern("MM/yyyy"));
    }

    @Step("Get selected month for DEPARTURE flight")
    public YearMonth getSelectedMonthDepartureFlight() {
        return getSelectedMonth(FlightDirection.DEPARTURE);
    }

    private SelenideElement getFlightSectionContainer(FlightDirection flightDirection) {
        return $x(String.format(flightSectionContainer, flightDirection.toString()));
    }

    @Step("Get first available flight date for DEPARTURE flight")
    private int getFirstAvailableFlightDate() {
        SelenideElement firstAvailable = getFlightSectionContainer(FlightDirection.DEPARTURE)
                .$$x("./following-sibling::div//div[@role='button' and div/span]/p")
                .first();
        return Integer.parseInt(firstAvailable.shouldBe(Condition.visible).getText());
    }

    @Step("Change selected month for {flightDirection} flight to {selectedMonth}")
    public void changeSelectedMonth(FlightDirection flightDirection, YearMonth selectedMonth) {
        YearMonth currentMonth = getSelectedMonth(flightDirection);
        boolean clickNext = selectedMonth.isAfter(currentMonth);

        while (!currentMonth.equals(selectedMonth)) {
            if (clickNext) {
                getSliderButton(flightDirection).last().scrollIntoView(false).click();
            } else {
                getSliderButton(flightDirection).first().scrollIntoView(false).click();
            }
            scrollUntilLastDateOfReturnMonthPrices();
            currentMonth = getSelectedMonth(flightDirection); // update after click
        }
    }

    @Step("Get slider navigation buttons for {flightDirection} flight")
    public ElementsCollection getSliderButton(FlightDirection flightDirection) {
        return getFlightSectionContainer(flightDirection)
                .$$x("./following-sibling::div//div[contains(@class, 'slick-slider')]//button");
    }

    @Step("Get price for {flightDirection} flight on {date}")
    public int getPriceByDate(FlightDirection flightDirection, LocalDate date) {
        changeSelectedMonth(flightDirection, YearMonth.from(date));

        return Integer.parseInt(getPriceElementByDate(flightDirection, date)
                .getText()
                .replaceAll(",", ""));
    }

    private SelenideElement getPriceElementByDate(FlightDirection flightDirection, LocalDate date) {
        return getFlightSectionContainer(flightDirection)
                .$$x(String.format("./following-sibling::div//div[p[text()='%d']]//span", date.getDayOfMonth()))
                .first();
    }

    @Step("Scroll until all return month prices are loaded")
    public void scrollUntilLastDateOfReturnMonthPrices() {
        int unchangedCount = 0;
        int previousSize = 0;
        ElementsCollection price = getFlightSectionContainer(FlightDirection.RETURN)
                .$$x("./following-sibling::div//div[p]//span");
        while (unchangedCount < 3) {
            price.last().scrollIntoView(true);
            sleep(500);

            int currentSize = price.size();
            if (currentSize == previousSize) {
                unchangedCount++;
            } else {
                unchangedCount = 0;
                previousSize = currentSize;
            }
        }
    }

    @Step("Select ticket with DEPARTURE date {departureDate} and RETURN date {returnDate}")
    public void selectTicket(LocalDate departureDate, LocalDate returnDate) {
        selectTicketByDate(FlightDirection.DEPARTURE, departureDate);
        selectTicketByDate(FlightDirection.RETURN, returnDate);
        clickContinueButton();
    }

    @Step("Select ticket for {flightType} flight on {date}")
    public void selectTicketByDate(FlightDirection flightType, LocalDate date) {
        changeSelectedMonth(flightType, YearMonth.from(date));
        getPriceElementByDate(flightType, date).scrollIntoCenter().click();
    }

    private String flightSectionContainer = "//div[p[contains(@class, 'MuiTypography-h2') and text()='%s']]";
    private SelenideElement loadingImg = $x("//img[@alt='loading...']");
}
