package com.dionext.utils;

import java.util.List;

public class Utils {
    private Utils() {
    }

    public static <T> T firstOrNull(List<T> l) {
        // Return null if list is null or empty
        if (l == null || l.isEmpty())
            return null;

        return l.get(0);
    }

    public static String restrictText(String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
