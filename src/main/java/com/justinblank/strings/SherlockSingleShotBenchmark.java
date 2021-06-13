package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.openjdk.jmh.annotations.*;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// These tests are essentially pulled from https://github.com/rust-lang/regex/blob/master/bench/src/sherlock.rs, but do
// slightly less work, because the engine currently doesn't support getting all matches within a string

@State(Scope.Benchmark)
public class SherlockSingleShotBenchmark {

    // Note: The options featuring Adler are structurally equivalent to the others but will consume much more input before being
    // matched

    @Param({"Sherlock", "Adler", "Sherlock|Holmes", "Sherlock|Street", "Adler|Watson", "([Ss]herlock)|([Hh]olmes)",
            "Sherlock|Holmes|Watson|Irene|Adler|John|Baker", "the\\s+\\w+", "zqj", "aqj", "[a-q][^u-z]{13}x",
            "[a-zA-Z]+ing", "Holmes.{0,25}Watson|Watson.{0,25}Holmes", "\\s[a-zA-Z]{0,12}ing\\s"})
    String regexString = "([Ss]herlock)|(Hholmes)";
    SearchMethod method;
    SearchMethod nfa;
    DFA dfa;
    Pattern pattern;
    java.util.regex.Pattern javaRegex;

    @Setup()
    public void setup() {
        method = NFA.createNFA(regexString);
        nfa = NFA.createNFANoAhoCorasick(regexString);
        dfa = DFA.createDFA(regexString);
        pattern = DFACompiler.compile(regexString, "myMatcher");
        javaRegex = java.util.regex.Pattern.compile(regexString);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMySearchMethod() {
        return method.matcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyNFA() {
        return nfa.matcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyDFA() {
        return dfa.search(SherlockText.TEXT).matched;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testJavaRegex() {
        return javaRegex.matcher(SherlockText.TEXT).find();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyPattern() {
        return pattern.matcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testAdlerWatsonHandMatcher() {
        return new AdlerWatsonHandMatcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testAdlerContains() {
        return SherlockText.TEXT.contains("Adler");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testSherlockContains() {
        return SherlockText.TEXT.contains("Sherlock");
    }

    public static void main(String[] args) {
        Pattern pattern = DFACompiler.compile("Sherlock", "SherlockPattern");
        int counter = 0;
        for (int i = 0; i < 1000000; i++) {
            String s;
            if (new Random().nextBoolean()) {
                s = "Abcdefer Sherlock";
            }
            else {
                if (new Random().nextBoolean()) {
                    s = "Abcdefer Watson";
                }
                else {
                    s = "Abcdefer Adler";
                }
            }
            if (pattern.matcher(s).containedIn()) {
                counter++;
            }
            if (new AdlerWatsonHandMatcher(s).containedIn()) {
                counter++;
            }
        }
        System.out.println(counter);
    }
}
