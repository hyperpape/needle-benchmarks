package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.loaders.SherlockText;
import com.justinblank.strings.loaders.SherlockTranslatedUnicodeText;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

// These tests are essentially pulled from https://github.com/rust-lang/regex/blob/070374f2878f23809a4e5ca0810d523f692c6e7e/bench/src/sherlock.rs
// The updated version lives at https://github.com/BurntSushi/rebar/blob/master/benchmarks/definitions/imported/sherlock.toml
// but the older version was nicer because it included commentary about what each pattern represented
@State(Scope.Benchmark)
public class SherlockTranslatedUnicodeBenchmark {

    @Param({
            // Sherlock|Adler|Holmes|Watson|Irene|John|Baker
            "Óèåòìïãë|Áäìåò|Èïìíåó|×áôóïî|Éòåîå|Êïèî|Âáëåò",
            // Holmes.{0,25}Watson|Watson.{0,25}Holmes
            "Èïìíåó.{0,25}×áôóïî|×áôóïî.{0,25}Èïìíåó",
            // Holmes.{0,15}Watson|Watson.{0,15}Holmes|Adler.{0,15}Sherlock|Sherlock.{0,15}Adler
            "Èïìíåó.{0,15}×áôóïî|×áôóïî.{0,15}Èïìíåó|Áäìåò.{0,15}Óèåòìïãë|Óèåòìïãë.{0,15}Áäìåò",
            // [a-zA-z]+ing
            "[á-úÁ-Ú]+éîç"})
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
        var m = pattern.matcher(SherlockTranslatedUnicodeText.TEXT);
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
        var m = runAutomaton.newMatcher(SherlockTranslatedUnicodeText.TEXT);
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
            index = SherlockTranslatedUnicodeText.TEXT.indexOf(regexString, index);
            if (index != -1) {
                index += patternLength;
            }
        }
        return count;
    }
}


