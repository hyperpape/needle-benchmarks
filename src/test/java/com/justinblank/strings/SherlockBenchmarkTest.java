package com.justinblank.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SherlockBenchmarkTest {

    @Test
    public void testEqualsCount() {
        var b = new SherlockBenchmark();
        b.setup();
        assertEquals(b.javaRegex(), b.pattern());
    }
}
