package com.justinblank.strings;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailBenchmarkTest {

    @Test
    public void testMkRandomString() {
        String s = EmailBenchmark.mkRandomString(new Random(), 32, 1);
        Pattern pattern = Pattern.compile(EmailBenchmark.REGEX_STRING);
        assertTrue(pattern.matcher(s).matches());

        s = EmailBenchmark.mkRandomString(new Random(), 4096, 0);
        assertFalse(pattern.matcher(s).matches());
    }
}
