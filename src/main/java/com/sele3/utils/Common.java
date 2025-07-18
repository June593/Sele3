package com.sele3.utils;

import java.util.List;
import java.util.stream.Collectors;

public class Common {
    public static <T> List<T> sort(List<T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }

    public static Integer changeMoneyValueToOnlyMoneyNumber(String moneyText) {
        return Integer.parseInt(moneyText.replaceAll("[^0-9]", ""));
    }
}
