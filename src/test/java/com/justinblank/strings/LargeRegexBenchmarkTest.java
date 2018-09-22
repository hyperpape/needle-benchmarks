package com.justinblank.strings;


import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LargeRegexBenchmarkTest {

    @Test
    public void testMkRandomString() {
        String s = LargeRegexBenchmark.mkRandomString(new Random(), 32, 1);
        assertTrue(java.util.regex.Pattern.compile(LargeRegexBenchmark.CORE_REGEX_STRING + "24}").matcher("789").matches());
        assertTrue(java.util.regex.Pattern.compile(LargeRegexBenchmark.CORE_REGEX_STRING + "24}").matcher(s).matches());
    }
}