package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

// Tests the backtracking example from Russ Cox's article: https://swtch.com/~rsc/regexp/regexp1.html
@State(Scope.Benchmark)
public class BacktrackingRepetitionBenchmark {

    @Param({"2", "4", "8", "16", "24"})
    int repetitionCount;

    private java.util.regex.Pattern javaPattern;
    private SearchMethod nfa;
    private DFA dfa;
    private Pattern pattern;

    private String string;

    @Setup
    public void setup() {
        string = "a".repeat(repetitionCount);
        String patternString = "a?".repeat(repetitionCount) + "a".repeat(repetitionCount);
        javaPattern = java.util.regex.Pattern.compile(patternString);
        nfa = NFA.createNFA(patternString);
        dfa = DFA.createDFA(patternString);
        pattern = DFACompiler.compile(patternString, "BackTrackingTestRegex");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testRegexMatcher() {
        return javaPattern.matcher(string).matches();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testNFAMatcher()  {
        return nfa.matches(string);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testDFAMatcher() {
        return dfa.matches(string);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testClassMatcher() {
        return pattern.matcher(string).matches();
    }
}
