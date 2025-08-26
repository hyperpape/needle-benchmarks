package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.loaders.SherlockAsciiText;
import com.justinblank.strings.loaders.SherlockText;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

// These tests are essentially pulled from https://github.com/rust-lang/regex/blob/070374f2878f23809a4e5ca0810d523f692c6e7e/bench/src/sherlock.rs
// The updated version lives at https://github.com/BurntSushi/rebar/blob/master/benchmarks/definitions/imported/sherlock.toml
// but the older version was nicer because it included commentary about what each pattern represented
@State(Scope.Benchmark)
public class SherlockAsciiUnicodeMixed {

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
            "[a-zA-Z]+ing",
            // Large regex with two essential factors
            "Holmes.{0,25}Watson|Watson.{0,25}Holmes",
            // Similarly large, but no essential factors
            "Holmes.{0,15}Watson|Watson.{0,15}Holmes|Adler.{0,15}Sherlock|Sherlock.{0,15}Adler",
            "\\s[a-zA-Z]{0,12}ing\\s",
            "[a-z][a-e]{6}z",
            "let us hear a true"})
    String regexString = "shouldn't appear in tests";

    @Param({"100", "90", "75", "50", "25", "10", "0"})
    int asciiPct;

    SearchMethod method;
    SearchMethod nfa;
    DFA dfa;
    Pattern pattern;
    java.util.regex.Pattern javaRegex;

    RunAutomaton runAutomaton;
    int iter = 0;

    String[] stringArgs = new String[100];

    @Setup()
    public void setup() {

        method = NFA.createNFA(regexString);
        nfa = NFA.createNFANoAhoCorasick(regexString);
        dfa = DFA.createDFA(regexString);
        pattern = DFACompiler.compile(regexString, "myMatcher");
        javaRegex = java.util.regex.Pattern.compile(regexString);
        runAutomaton = new RunAutomaton(new RegExp(regexString).toAutomaton());

        for (int i = 0; i < asciiPct; i++) {
            stringArgs[i] = SherlockAsciiText.TEXT;
        }
        for (int i = asciiPct; i < 100; i++) {
            stringArgs[i] = SherlockText.TEXT;
        }
        List<String> stringList = Arrays.asList(stringArgs);
        Collections.shuffle(stringList, new Random(1234L));
        stringArgs = stringList.toArray(new String[0]);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int javaRegex() {
        int count = 0;
        java.util.regex.Matcher m;
        m = javaRegex.matcher(stringArgs[iter++]);
        if (iter == 100) {
            iter = 0;
        }
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
        Matcher m = pattern.matcher(stringArgs[iter++]);
        if (iter == 100) {
            iter = 0;
        }
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
        AutomatonMatcher m = runAutomaton.newMatcher(stringArgs[iter++]);
        if (iter == 100) {
            iter = 0;
        }
        while (m.find()) {
            count++;
        }
        return count;
    }
}


