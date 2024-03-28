package com.dionext.utils;

import com.google.common.io.Files;

public class FileUtils {
    private FileUtils() {
    }

    public static String getFileNameWithoutExtension(String path) {
        return Files.getNameWithoutExtension(path);
    }
}
