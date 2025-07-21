package com.sele3.utils;

import com.sele3.data.leapfrog.GameData;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.util.*;

public class GameDataComparator {

    @Step("Compare expected and actual GameData")
    public static void compareGameData(List<GameData> expectedList, List<GameData> actualList) {
        List<GameData> missingTitleList = new ArrayList<>();
        List<String> mismatchDetails = new ArrayList<>();

        for (GameData expected : expectedList) {
            Optional<GameData> actualOpt = actualList.stream()
                    .filter(a -> a.getTitle().equalsIgnoreCase(expected.getTitle()))
                    .findFirst();

            if (actualOpt.isEmpty()) {
                missingTitleList.add(expected);
            } else {
                GameData actual = actualOpt.get();
                List<String> mismatches = new ArrayList<>();

                if (!Objects.equals(expected.getAge(), actual.getAge())) {
                    mismatches.add(String.format("Age mismatch [Expected: %s | Actual: %s]", expected.getAge(), actual.getAge()));
                }
                if (!Objects.equals(expected.getPrice(), actual.getPrice())) {
                    mismatches.add(String.format("Price mismatch [Expected: %s | Actual: %s]", expected.getPrice(), actual.getPrice()));
                }

                if (!mismatches.isEmpty()) {
                    mismatchDetails.add(String.format("Title: %s%n%s%n", expected.getTitle(), String.join("\n", mismatches)));
                }
            }
        }

        if (!missingTitleList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Total missing titles: ").append(missingTitleList.size()).append("\n\n");
            for (GameData data : missingTitleList) {
                sb.append("- ").append(data.getTitle()).append("\n");
            }
            Allure.addAttachment("Missing Titles", sb.toString());
        }

        if (!mismatchDetails.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Total mismatched items: ").append(mismatchDetails.size()).append("\n\n");
            sb.append(String.join("\n\n", mismatchDetails));
            Allure.addAttachment("Mismatched Fields", sb.toString());
        }

        Assertion.assertTrue(missingTitleList.isEmpty(), "There are missing titles");
        Assertion.assertTrue(mismatchDetails.isEmpty(), "There are mismatched fields");
    }
}
