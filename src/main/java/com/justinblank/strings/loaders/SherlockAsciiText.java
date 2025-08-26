package com.justinblank.strings.loaders;

import com.justinblank.strings.SherlockSingleShotBenchmark;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SherlockAsciiText {

    public static final String TEXT;

    static {
        try (InputStream in = SherlockSingleShotBenchmark.class
                .getClassLoader()
                .getResourceAsStream("sherlockholmesascii.txt")) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: sherlockholmesascii.txt");
            }
            TEXT = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
