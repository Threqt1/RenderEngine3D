package com.krish.core.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Utils {
    public Utils() {

    }

    /**
     * Read a file and return its contents
     * @param filePath The file path (in the resources folder)
     * @return The file data
     */
    public static String readFile(String filePath) {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(Utils.class.getResource(filePath)).toURI())));
        } catch(Exception e) {
            throw new RuntimeException("Error reading file [" + filePath + "]");
        }
        return str;
    }
}
