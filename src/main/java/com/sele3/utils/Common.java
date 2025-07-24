package com.sele3.utils;

import io.qameta.allure.Allure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sele3.utils.Assertion;

public class Common {
    public static <T> List<T> sort(List<T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }

    public static Integer changeMoneyValueToOnlyMoneyNumber(String moneyText) {
        return Integer.parseInt(moneyText.replaceAll("[^0-9]", ""));
    }

    public static String normalize(String input) {
        if (input == null) return "";
        return input
                .replace("\u00A0", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }
}

