package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.loaders.SherlockText;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

// These tests are essentially pulled from https://github.com/rust-lang/regex/blob/070374f2878f23809a4e5ca0810d523f692c6e7e/bench/src/sherlock.rs
// The updated version lives at https://github.com/BurntSushi/rebar/blob/master/benchmarks/definitions/imported/sherlock.toml
// but the older version was nicer because it included commentary about what each pattern represented
@State(Scope.Benchmark)
public class SherlockBenchmark {

    @Param({"Sherlock",
            "Adler",
            "Sherlock|Holmes",
            // Suffix search where almost all suffix matches will be a full match
            "[Ss]herlock",
            // Suffix search, but most instances will not be valid matches
            "anywhere|somewhere",
            // Search with prefix and suffix--unrealistic example
            "the\\s\\w{1,20}\\s\\agency",
            // Search with prefix and suffix--another unrealistic example
            "Sherlock\\s\\w{1,20}ed",
            "Sherlock|Street",
            "Adler|Watson",
            "([Ss]herlock)|([Hh]olmes)",
            "Sherlock|Holmes|Watson|Irene|Adler|John|Baker",
            "the\\s+\\w+",
            "zqj",
            "aqj",
            "[a-q][^u-z]{13}x",
            "[aeiou][a-z]*ing",
            "[bc][a-z]*ing",
            "[bcdf][a-z]*ing",
            "[zqjxkv][a-z]*ing",
            "[a-zA-Z]+ing",
            // Large regex with two essential factors
            "Holmes.{0,25}Watson|Watson.{0,25}Holmes",
            // Similarly large, but no essential factors
            "Holmes.{0,15}Watson|Watson.{0,15}Holmes|Adler.{0,15}Sherlock|Sherlock.{0,15}Adler",
            "\\s[a-zA-Z]{0,12}ing\\s",
            "[a-z][a-e]{6}z",
            "let us hear a true"})
    String regexString = "shouldn't appear in tests";
    SearchMethod method;
    SearchMethod nfa;
    DFA dfa;
    Pattern pattern;
    java.util.regex.Pattern javaRegex;

    RunAutomaton runAutomaton;

    @Setup()
    public void setup() {
        method = NFA.createNFA(regexString);
        nfa = NFA.createNFANoAhoCorasick(regexString);
        dfa = DFA.createDFA(regexString);
        pattern = DFACompiler.compile(regexString, "myMatcher");
        javaRegex = java.util.regex.Pattern.compile(regexString);
        runAutomaton = new RunAutomaton(new RegExp(regexString).toAutomaton());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int javaRegex() {
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
    public int pattern() {
        int count = 0;
        var m = pattern.matcher(SherlockText.TEXT);
        while (m.find().matched) {
            count++;
        }
        return count;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int brics() {
        int count = 0;
        var m = runAutomaton.newMatcher(SherlockText.TEXT);
        while (m.find()) {
            count++;
        }
        return count;
    }

    // Include cost of searching for regex strings by index. This lets us verify that our pattern implementation handles
    // literal strings by turning them into the equivalent of an "indexOf" search
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int indexOf() {
        int count = -1;
        int patternLength = regexString.length();
        var index = 0;
        while (index != -1) {
            count++;
            index = SherlockText.TEXT.indexOf(regexString, index);
            if (index != -1) {
                index += patternLength;
            }
        }
        return count;
    }
}

