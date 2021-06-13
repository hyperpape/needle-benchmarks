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

// These tests are essentially pulled from https://github.com/rust-lang/regex/blob/master/bench/src/sherlock.rs

@State(Scope.Benchmark)
public class SherlockBenchmark {

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
    public int testJavaRegex() {
        int count = 0;
        var m = javaRegex.matcher(SherlockText.TEXT);
        while (m.find()) {
            count++;
        }
        return count;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int testMyPattern() {
        int count = 0;
        var m = pattern.matcher(SherlockText.TEXT);
        while (m.find().matched) {
            count++;
        }
        return count;
    }
}

