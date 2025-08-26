package com.justinblank.strings.loaders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SherlockTranslatedUnicodeTextTest {

    @Test
    void canLoadText() {
        assertEquals(594927, SherlockTranslatedUnicodeText.TEXT.length());
    }

}