package com.dionext.utils;

import com.google.common.io.Files;

import java.nio.file.Path;

public class FileUtils {
    private FileUtils() {
    }

    public static String getFileNameWithoutExtension(String path) {

        return Files.getNameWithoutExtension(path);
    }
    public static Path changeFileExtension(Path path, String newExtension) {
        // Get the file name as a string
        String fileName = path.getFileName().toString();

        // Remove the old extension (if any)
        int lastDotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);

        // Append the new extension
        String newFileName = fileNameWithoutExtension + newExtension;

        // Resolve the new file name with the original path's parent
        return path.getParent().resolve(newFileName);
    }
}
