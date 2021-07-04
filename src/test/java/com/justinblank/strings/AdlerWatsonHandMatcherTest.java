package com.justinblank.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdlerWatsonHandMatcherTest {

    @Test
    public void testHandMatcher() {
        assertTrue(new AdlerWatsonHandMatcher("Adler").containedIn());
        assertTrue(new AdlerWatsonHandMatcher("No Adler").containedIn());
        assertTrue(new AdlerWatsonHandMatcher("Watson").containedIn());
        assertTrue(new AdlerWatsonHandMatcher("No Watson").containedIn());
        assertFalse(new AdlerWatsonHandMatcher("GarbWage").containedIn());
        assertFalse(new AdlerWatsonHandMatcher("dler").containedIn());
        assertFalse(new AdlerWatsonHandMatcher("Adle").containedIn());
    }

}
