package com.sele3.page.general;

import com.sele3.utils.PropertiesHelper;

import static com.codeborne.selenide.Selenide.open;

public class GeneralPage {

    public void openPage() {
        String url;
        String env = System.getProperty("language");
        switch (env) {
            case "vi":
                url = PropertiesHelper.getPropValue("url_vi");
                break;
            case "en":
                url = PropertiesHelper.getPropValue("url_en");
                break;
            default:
                url = PropertiesHelper.getPropValue("url");
                break;
        }
        open(url);
    }
}
