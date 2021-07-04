package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// A complicated pattern with many states. Suggestions for a more real world example welcome
@State(Scope.Benchmark)
public class LargeRegexBenchmark {

    static final String CORE_REGEX_STRING = "((123)|(234)|(345)|(456)|(567)|(678)|(789)|(0987)|(9876)|(8765)|(7654)|(6543)|(5432)|(4321)|(3210)){1,";

    static final List<String> STRINGS = new ArrayList<>();
    static final List<String> COMPONENTS = Arrays.asList("123", "234", "345", "456", "567", "678", "789", "0987", "9876", "8765", "7654", "6543", "5432", "4321", "3210");

    com.justinblank.strings.Pattern pattern;
    java.util.regex.Pattern regexPattern;
    SearchMethod nfa;
    DFA dfa;

    @Param({"4", "16", "256"})
    int stringLength;

    @Param({".1", ".9"})
    double successPct;

    @Param({"4", "16"})
    int repetitionCount;

    private int index = -1;

    @Setup
    public void setup() {
        String regexString =  CORE_REGEX_STRING + repetitionCount + "}";
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            addRandomString(random, stringLength, successPct);
        }
        try {
            regexPattern = java.util.regex.Pattern.compile(regexString);
            nfa = NFA.createNFA(regexString);
            dfa = DFA.createDFA(regexString);
            pattern = DFACompiler.compile(regexString, "LargeRegexBenchmarkPattern");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addRandomString(Random random, int length, double successPercent) {
        STRINGS.add(mkRandomString(random, length, successPercent));
    }

    protected static String mkRandomString(Random random, int length, double successPercent) {
        boolean pass = random.nextDouble() < successPercent;
        StringBuilder sb = new StringBuilder();
        if (pass) {
            while (sb.length() < length) {
                int next = random.nextInt(COMPONENTS.size());
                sb.append(COMPONENTS.get(next));
            }
        }
        else {
            sb.append(RandomStringUtils.randomAscii(length));
        }
        return sb.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean pattern() {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        Matcher instance = pattern.matcher(toMatch);
        return instance.matches();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean nfa() {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return nfa.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean dfa() {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return dfa.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean javaRegex() {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return regexPattern.matcher(toMatch).matches();
    }
}
