package com.justinblank.strings;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SherlockMultiBenchmark {

    public static final String[] REGEX_STRINGS = {"Sherlock", "Adler", "Sherlock|Holmes", "Sherlock|Street", "Adler|Watson", "([Ss]herlock)|([Hh]olmes)",
            "Sherlock|Holmes|Watson|Irene|Adler|John|Baker", "the\\s+\\w+", "zqj", "aqj", "[a-q][^u-z]{13}x",
            "[a-zA-Z]+ing", "Holmes.{0,25}Watson|Watson.{0,25}Holmes", "\\s[a-zA-Z]{0,12}ing\\s", "[a-z][a-e]{6}z",
            "let us hear a true"};

    static final List<java.util.regex.Pattern> JAVA_PATTERNS = new ArrayList<>();
    static final List<Pattern> PATTERNS = new ArrayList<>();

    static {
        for (var i = 0; i < REGEX_STRINGS.length; i++) {
            var s = REGEX_STRINGS[i];
            JAVA_PATTERNS.add(java.util.regex.Pattern.compile(s));
            PATTERNS.add(DFACompiler.compile(s, "SherlockMultiBenchmark" + i));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int javaRegex() {
        int count = 0;
        for (var i = 0; i < JAVA_PATTERNS.size(); i++) {
            var m = JAVA_PATTERNS.get(i).matcher(SherlockText.TEXT);
            while (m.find()) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int pattern() {
        int count = 0;
        for (var i = 0; i < PATTERNS.size(); i++) {
            var m = PATTERNS.get(i).matcher(SherlockText.TEXT);
            while (m.find().matched) {
                count++;
            }
        }
        return count;
    }
}
