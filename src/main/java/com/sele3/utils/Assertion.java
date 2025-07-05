package com.sele3.utils;

public abstract class Assertion {

    private static SoftAssertion softAssertion = new SoftAssertion();

    public static void assertTrue(boolean condition, String message) {
        softAssertion.assertTrue(condition, message);
    }

    public static void assertFalse(boolean condition, String message) {
        softAssertion.assertFalse(condition, message);
    }

    public static void assertEquals(int actual, int expected, String message) {
        softAssertion.assertEquals(actual, expected, message);
    }

    public static void assertEquals(String actual, String expected, String message) {
        softAssertion.assertEquals(actual, expected, message);
    }

    public static <T> void assertEquals(T actual, T expected, String message) {
        softAssertion.assertEquals(actual, expected, message);
    }

    public static void assertAll(String message) {
        softAssertion.assertAll(message);
    }
}