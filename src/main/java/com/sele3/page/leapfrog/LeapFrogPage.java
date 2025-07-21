package com.sele3.page.leapfrog;

import com.codeborne.selenide.WebDriverRunner;
import com.sele3.data.leapfrog.GameData;
import com.sele3.utils.Common;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;

public class LeapFrogPage {

    public static final String BASE_URL = "https://store.leapfrog.com/en-us/apps/c?p=%d&platforms=197&product_list_dir=asc&product_list_order=name";

    public List<GameData> parseGameDataFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements cards = doc.select("div.resultList div.catalog-product");

        return cards.stream()
                .map(card -> {
                    String title = Common.normalize(card.select(".heading a").text());
                    String age = Common.normalize(card.select(".ageDisplay").text());
                    String priceText = Common.normalize(card.select(".single.price").text());
                    String price = Common.normalize(priceText.contains(":") ? priceText.split(":")[1] : priceText);

                    return new GameData(title, age, price);
                })
                .collect(Collectors.toList());
    }

    @Step("Get actual data from LeapFrog site (total {pages} pages)")
    public List<GameData> crawlActualData(int totalPages) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<List<GameData>>> futures = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            int page = i;
            futures.add(executor.submit(() -> {
                open(String.format(BASE_URL, page));
                String html = WebDriverRunner.source();
                return parseGameDataFromHtml(html);
            }));
        }

        executor.shutdown();

        List<GameData> result = new ArrayList<>();
        for (Future<List<GameData>> f : futures) {
            try {
                result.addAll(f.get());
            } catch (Exception e) {
                throw new RuntimeException("Error getting page data", e);
            }
        }
        return result;
    }

    @Step("Get total number of pages from first page HTML")
    public int getTotalPages() {
        String pageHtml = WebDriverRunner.source();
        Pattern pattern = Pattern.compile("Page \\d+ of (\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pageHtml);

        if (matcher.find()) {
            int totalPages = Integer.parseInt(matcher.group(1));
            Allure.step("Total pages found: " + totalPages);
            return totalPages;
        }
        Allure.step("Failed to detect total pages. Default to 1.");
        return 1;
    }
}
