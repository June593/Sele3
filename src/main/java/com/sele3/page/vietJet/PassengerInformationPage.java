package com.sele3.page.vietJet;


import com.codeborne.selenide.Condition;
import com.sele3.utils.YamlUtils;

import static com.codeborne.selenide.Selenide.$x;

public class PassengerInformationPage extends SelectFlightPage {

    public boolean isPageDisplayed() {
        String label = "//h3[.='%s']";
        return $x(String.format(label, YamlUtils.getProperty("label_Passenger_Page.passenger_info")))
                .shouldBe(Condition.visible)
                .isDisplayed();
    }
}
