package com.justinblank.strings.loaders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SherlockTextTest {

    @Test
    void canLoad() {
        // This is very slightly different number of characters from the others, but the difference is significantly
        // less than 0.1%, and makes it not worth manipulating these files more to make them match.
        assertEquals(594910, SherlockText.TEXT.length());
    }
}