package com.sele3.page.leapfrog;

import com.codeborne.selenide.WebDriverRunner;
import com.sele3.data.leapfrog.GameData;
import com.sele3.utils.Assertion;
import com.sele3.utils.Common;
import com.sele3.utils.ExcelUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.open;

public class LeapFrogPage {

    private final String BASE_URL = "https://store.leapfrog.com/en-us/apps/c?p=%d&platforms=197&product_list_dir=asc&product_list_order=name";

    public static List<GameData> parseGameDataFromHtml(String html) {
        List<GameData> gameList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements cards = doc.select("div.resultList div.catalog-product");
        for (Element card : cards) {
            String title = Common.normalize(card.select(".heading a").text());          // (French) Une belle journée pour rugir
            String age = Common.normalize(card.select(".ageDisplay").text());           // Ages 4-6 years
            String price = Common.normalize(card.select(".single.price").text().split(":")[1]);       // $7.50

            gameList.add(new GameData(title.trim(), age.trim(), price.trim()));
        }
        return gameList;
    }

    @SneakyThrows
    @Step("Read expected data from Excel")
    public List<GameData> readExpectedData(String excelPath) {
        List<GameData> gameDataList = new ArrayList<>();

        try (InputStream fis = new FileInputStream(excelPath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return gameDataList;

            // Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            // Required column names
            String TITLE = "title";
            String AGE = "age";
            String PRICE = "price";

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String title = row.getCell(headerMap.get(TITLE)).getStringCellValue();
                String age = row.getCell(headerMap.get(AGE)).getStringCellValue();
                String price = row.getCell(headerMap.get(PRICE)).getStringCellValue();
                gameDataList.add(new GameData(title, age, price));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel file: " + excelPath, e);
        }

        return gameDataList;
    }


    @Step("Get actual data from LeapFrog site (total {pages} pages)")
    public List<GameData> crawlActualData() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<List<GameData>>> futures = new ArrayList<>();
        int totalPages = getTotalPages();
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
        open(String.format(BASE_URL, 1));
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

    @Step("Compare expected and actual data")
    public void compareData(List<GameData> expectedList, List<GameData> actualList) {
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
            for (GameData data : missingTitleList) {
                sb.append("- Missing title: ").append(data.getTitle()).append("\n");
            }
            Allure.addAttachment("Missing Titles", sb.toString());
        }

        if (!mismatchDetails.isEmpty()) {
            Allure.addAttachment("Mismatched fields", String.join("\n\n", mismatchDetails));
        }

        Assertion.assertTrue(missingTitleList.isEmpty() || mismatchDetails.isEmpty(), "There are data mismatch");
    }
}
