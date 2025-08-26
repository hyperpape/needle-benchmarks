package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.loaders.SherlockText;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SherlockContainsBenchmark {

    @Param({"Sherlock", "Adler", "zqj", "aqj"})
    String regexString = "";
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
    public boolean contains() {
        return SherlockText.TEXT.contains(regexString);
    }
}
