package com.sele3.page.vietJet;


//import com.model.vietjet.DepartureLocation;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class HomePage {
    private final SelenideElement popUpDialog = $("#popup-dialog-description");
    private final SelenideElement acceptDialog = $x("//div[@role='dialog']//span/h5");
    private final String dynamicInput = "//label[contains(@class,'MuiInput') and text()='%s']";
    private final String dynamicLocation = "//div[@id='panel1a-content']//div[text()='%s']";

    public HomePage() {
    }

    public void acceptCookie() {
        popUpDialog.should(exist);
        acceptDialog.click();
    }
}