package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.loaders.SherlockText;
import org.openjdk.jmh.annotations.*;

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
    public boolean searchMethod() {
        return method.matcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean nfa() {
        return nfa.matcher(SherlockText.TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean dfa() {
        return dfa.search(SherlockText.TEXT).matched;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean javaRegex() {
        return javaRegex.matcher(SherlockText.TEXT).find();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean pattern() {
        return pattern.matcher(SherlockText.TEXT).containedIn();
    }

}
