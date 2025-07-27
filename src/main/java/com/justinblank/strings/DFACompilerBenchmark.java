package com.justinblank.strings;

import com.justinblank.strings.RegexAST.Node;
import com.justinblank.strings.Search.SearchMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class DFACompilerBenchmark {

    private static final String DIGIT_REGEX = "[0-9]+";
    private static final String LARGE_REGEX_CORE = "((123)|(234)|(345)|(456)|(567)|(678)|(789)|(0987)|(9876)|(8765)|(7654)|(6543)|(5432)|(4321)|(3210))";
    private static final String LARGE_REGEX = LARGE_REGEX_CORE + "{1,24}";
    private static final String HUGE_REGEX = LARGE_REGEX_CORE + "{1,128}";
    private static final String HOLMES_WATSON_15 = "Holmes.{0,15}Watson|Watson.{0,15}Holmes|Adler.{0,15}Sherlock|Sherlock.{0,15}Adler";

    private static final String HOLMES_WATSON_25 = "Holmes.{0,25}Watson|Watson.{0,25}Holmes|Adler.{0,25}Sherlock|Sherlock.{0,25}Adler";

    private int count = 0;

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void byteCompileSimpleRegex() {
        DFACompiler.compile(DIGIT_REGEX, "DigitBenchmarkTestClass" + count++);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void byteCompileLargeRegex() {
        DFACompiler.compile(LARGE_REGEX, "LargeRegexBenchmarkTestClass" + count++);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void byteCompileHugeRegex() {
        DFACompiler.compile(HUGE_REGEX, "HugeRegexBenchmarkTestClass" + count++);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public DFA compileSimpleDFA() {
        return DFA.createDFA(DIGIT_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public DFA compileLargeDFA() {
        return DFA.createDFA(LARGE_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public DFA compileHugeDFA() {
        return DFA.createDFA(HUGE_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public DFA compileHolmesWatson15DFA() {
        return DFA.createDFA(HOLMES_WATSON_15);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public DFA compileHolmesWatson25DFA() {
        return DFA.createDFA(HOLMES_WATSON_25);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public SearchMethod compileSimpleNFA() {
        return NFA.createNFA(DIGIT_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public SearchMethod compileLargeNFA() {
        return NFA.createNFA(LARGE_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public SearchMethod compileHugeNFA() {
        return NFA.createNFA(HUGE_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public Node parseLargePattern() { return RegexParser.parse(LARGE_REGEX); }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public Node parseSmallPattern() { return RegexParser.parse(DIGIT_REGEX); }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public java.util.regex.Pattern javaCompileSimplePattern() {
        return java.util.regex.Pattern.compile(DIGIT_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public java.util.regex.Pattern javaCompileLargePattern() {
        return java.util.regex.Pattern.compile(LARGE_REGEX);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public java.util.regex.Pattern javaCompileHugePattern() {
        return java.util.regex.Pattern.compile(HUGE_REGEX);
    }

}
