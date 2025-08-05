package com.sele3.page.vietJet;

import com.codeborne.selenide.*;
import com.sele3.data.vietjet.Passenger;
import com.sele3.data.vietjet.TicketData;
import com.sele3.enums.vietjet.LabelType;
import com.sele3.utils.Constants;
import com.sele3.utils.YamlUtils;
import io.qameta.allure.Step;


import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;

public class SelectFlightPage {

    public boolean allPricesHaveVND() {
        for (SelenideElement price : prices) {
            if (!price.getText().contains("VND")) {
                return false;
            }
        }
        return true;
    }

    public boolean isSelectTravelOptionsPageDisplayed() {
        return WebDriverRunner.url().endsWith("/select-flight");
    }

    public int getCheapestPrice() {
        return price.stream()
                .mapToInt(el -> {
                    String full = el.scrollIntoView(true).getText();
                    String numeric = full.replaceAll("\\D", "");
                    return Integer.parseInt(numeric);
                })
                .min()
                .orElseThrow(() -> new RuntimeException("No price found"));
    }

    public void scrollUntilAllPricesLoaded() {
        int unchangedCount = 0;
        int previousSize = 0;

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

    @Step("Select the first cheapest ticket")
    public void selectTheFirstCheapestTicket() {
        scrollUntilAllPricesLoaded();
        int minPrice = getCheapestPrice();
        String formattedPrice = String.format("%,d", minPrice / 1000);
        $$x(String.format("//p[contains(text(),'%s')]", formattedPrice)).first()
                .scrollIntoView(false)
                .shouldBe(Condition.visible)
                .click();
    }

    @Step("Click on Continue button")
    public void clickContinueButton() {
        getButtonByText((String) YamlUtils.getProperty("button.continue")).click();
    }

    public String getFromPlace() {
        return $x(String.format(placeInTopBanner, LabelType.FROM)).getText().split("\\(")[0].trim();
    }

    public String getToPlace() {
        return $x(String.format(placeInTopBanner, LabelType.TO)).getText().split("\\(")[0].trim();
    }

    public Passenger getPassengerInfo() {
        int adults = getPassengerCount(LabelType.ADULT);
        int children = getPassengerCount(LabelType.CHILDREN);
        int infants = getPassengerCount(LabelType.INFANT);

        return Passenger.builder()
                .adultNumber(adults)
                .childrenNumber(children)
                .infantNumber(infants)
                .build();
    }

    private int getPassengerCount(LabelType label) {
        SelenideElement element = $x(String.format(passengerText, label.toString()));
        if (element.exists()) {
            String content = element.getText();
            String regex = "(\\d+)\\s+" + label;
            Matcher matcher = Pattern.compile(regex).matcher(content);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return 0; // Default to 0 if not found
    }

    private TicketData getTicketInfo(boolean isDepartureTicket) {
        String formatter = (String) YamlUtils.getProperty("config.datetime_format");
        String locale = (String) YamlUtils.getProperty("config.locale");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(formatter, new Locale(locale));

        // Locate the section containing either "Departure flight" or "Return flight"
        String flightDirection = isDepartureTicket
                ? (String) YamlUtils.getProperty("label_Passenger_Page.departure_flight")
                : (String) YamlUtils.getProperty("label_Passenger_Page.return_flight");
        ElementsCollection section = $$x(String.format("//div[p[text()='%s']]/following-sibling::div//h5", flightDirection));

        // Extract all <h5> texts within the selected section
        List<String> h5Texts = section.texts();

        String from = h5Texts.get(0);
        String to = h5Texts.get(1);

        // Extract the date part: "31/07/2025" from "Thu, 31/07/2025 | 05:00 - 07:10 | VJ1198 | Deluxe"
        String datePart = h5Texts.get(2).split("\\|")[0].split(",")[1].trim();
        LocalDate date = LocalDate.parse(datePart, dateFormatter);

        return TicketData.builder()
                .from(from)
                .to(to)
                .departureDate(date)
                .build();
    }

    public TicketData getDepartureTicketInfo() {
        return getTicketInfo(true);
    }

    public TicketData getReturnTicketInfo() {
        return getTicketInfo(false);
    }

    public MonthDay getHighlightedMonthDay() {
        String rawText = selectedDate.getText().replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        String dayMonthFormat = (String) YamlUtils.getProperty("config.day_month_format");
        String language = (String) YamlUtils.getProperty("config.locale");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dayMonthFormat, new Locale(language));
        return MonthDay.parse(rawText, formatter);
    }

    private SelenideElement getButtonByText(String text) {
        return $x(String.format(button, text)).shouldBe(Condition.visible);
    }

    /**
     * Close the promotional dialog if it is displayed
     */
    public void closeAdsDialogIfVisible() {
        SelenideElement dialog = $x("//div[@role='dialog']");
        if (dialog.is(Condition.visible, Constants.MEDIUM_TIMEOUT)) {
            SelenideElement closeButton = dialog.$("button[aria-label='close']");
            closeButton.shouldBe(Condition.visible).click();
        }
    }

    private SelenideElement selectedDate = $x("//div[contains(@class, 'slick-current')]//p[2]");
    private ElementsCollection price = $$x("//div[contains(@class, 'MuiGrid-root')]//div[p[contains(@class, 'MuiTypography-h4') and not(@variantlg)]]");
    private String placeInTopBanner = "//span[.='%s']/following-sibling::span";
    private String passengerText = "//p[@variantmd='h3']//span[contains(.,'%s')]";
    private String button = "//button[contains(@class, 'MuiButtonBase-root') and span[.=\"%s\"]]";
    private ElementsCollection prices = $$("p.MuiTypography-body1");
}
