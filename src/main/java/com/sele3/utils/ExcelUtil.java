package com.sele3.utils;

import com.sele3.data.leapfrog.GameData;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class ExcelUtil {

    @FunctionalInterface
    public interface ExcelRowMapper<T> {
        T map(Row row, Map<String, Integer> headerMap);
    }

    public static <T> List<T> readFromExcel(File file, ExcelRowMapper<T> rowMapper) {
        try (InputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return List.of();
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = readHeaderMap(headerRow);

            List<T> result = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                result.add(rowMapper.map(row, headerMap));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read data from Excel", e);
        }
    }

    public static Map<String, Integer> readHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }
        return headerMap;
    }

    @SneakyThrows
    public static List<Map<String, String>> readAsMapList(@NotNull File excelFile) {
        List<Map<String, String>> resultList = new ArrayList<>();

        try (InputStream fis = new FileInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return resultList;

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValueAsString(cell);
                    rowData.put(headers.get(i), value);
                }

                resultList.add(rowData);
            }
        }

        return resultList;
    }


    public static String getCellString(Row row, Map<String, Integer> headerMap, String key) {
        Integer index = headerMap.get(key.toLowerCase());
        if (index == null) return "";
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellValueAsString(cell);
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }

    public static List<GameData> readGameDataListFromExcel(File file) {
        return readFromExcel(file, GameData::fromRow);
    }
}
