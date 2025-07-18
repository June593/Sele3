package com.sele3.data.leapfrog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;

import java.util.Objects;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class GameData {
    public String title;
    public String age;
    public String price;
}
