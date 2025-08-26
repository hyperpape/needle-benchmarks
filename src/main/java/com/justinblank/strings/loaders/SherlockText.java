package com.justinblank.strings.loaders;

import com.justinblank.strings.SherlockSingleShotBenchmark;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SherlockText {

    public static final String TEXT;

    static {
        try (InputStream in = SherlockSingleShotBenchmark.class
                .getClassLoader()
                .getResourceAsStream("sherlock.txt")) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: sherlock.txt");
            }
            var text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            TEXT = text;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
