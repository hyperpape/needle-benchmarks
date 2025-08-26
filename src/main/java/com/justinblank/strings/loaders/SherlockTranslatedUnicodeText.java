package com.justinblank.strings.loaders;

import com.justinblank.strings.SherlockSingleShotBenchmark;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This text takes the ascii version of the Complete Adventures of Sherlock Holmes, and translates the code points by
 * 128. The result is gibberish, but has statistical properties paralleling the original text, which works for
 * benchmarks.
 */
public class SherlockTranslatedUnicodeText {

    public static final String TEXT;

    static {
        try (InputStream in = SherlockSingleShotBenchmark.class
                .getClassLoader()
                .getResourceAsStream("sherlockholmestranslatedunicode.txt")) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: sherlockholmestranslatedunicode.txt");
            }
            TEXT = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
