package com.justinblank.strings.loaders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SherlockAsciiTextTest {

    @Test
    void canLoad() {
        assertEquals(594927, SherlockAsciiText.TEXT.length());
    }

}