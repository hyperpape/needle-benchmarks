package com.justinblank.strings;

import com.justinblank.strings.AsciiSearchMethodsBenchmark;
import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.Search.SearchMethods;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsciiSearchMethodsBenchmarkTest {

    @Test
    public void testMkRandomString() {
        List<String> patterns = List.of("abc", "defghij");
        String s = AsciiSearchMethodsBenchmark.mkRandomString(new Random(), patterns, 1, 12);
        SearchMethod method = SearchMethods.makeSearchMethod(patterns);
        int index = method.findIndex(s);
        assertNotEquals(-1, index);
    }
}
