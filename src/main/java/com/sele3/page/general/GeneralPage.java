package com.sele3.page.general;

import static com.codeborne.selenide.Selenide.open;

public class GeneralPage {

    public void gotoURL(String url) {
        open(url);
    }
}
