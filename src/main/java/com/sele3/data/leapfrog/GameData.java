package com.sele3.data.leapfrog;

import com.sele3.utils.ExcelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;

import java.util.Map;

@Data
@AllArgsConstructor
public class GameData {
    public String title;
    public String age;
    public String price;

    public static GameData fromRow(Row row, Map<String, Integer> headerMap) {
        return new GameData(
                ExcelUtil.getCellString(row, headerMap, "Title"),
                ExcelUtil.getCellString(row, headerMap, "Age"),
                ExcelUtil.getCellString(row, headerMap, "Price")
        );
    }
}
