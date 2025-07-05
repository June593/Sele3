package com.sele3.page.general;

import com.sele3.utils.InputParameters;
import com.sele3.utils.PropertiesHelper;

import static com.codeborne.selenide.Selenide.open;

public class GeneralPage {

    public void openPage() {
        String url = switch (InputParameters.LANGUAGE) {
            case "vi" -> PropertiesHelper.getPropValue("url_vi");
            case "en" -> PropertiesHelper.getPropValue("url_en");
            default -> PropertiesHelper.getPropValue("url");
        };
        open(url);
    }
}
