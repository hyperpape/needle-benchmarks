package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@State(Scope.Benchmark)
public class DigitSearchBenchmark {

    static final com.justinblank.strings.Pattern PATTERN;
    static final List<String> STRINGS = new ArrayList<>();
    static final String REGEX_STRING = "[0-9]+";
    static final java.util.regex.Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);
    static final SearchMethod NFA_SEARCH_METHOD = NFA.createNFA(REGEX_STRING);
    static final DFA MY_DFA = DFA.createDFA(REGEX_STRING);
    private int index = -1;

    @Param({"1", "16", "256", "4096"})
    int stringLength;

    @Param({".1", ".5", ".9"})
    double successPct;

    static {
        try {
            PATTERN = DFACompiler.compile(REGEX_STRING, "testClass");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setup() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            addRandomString(random, stringLength, successPct);
        }
    }

    protected static void addRandomString(Random random, int length, double successPercent) {
        boolean pass = random.nextDouble() < successPercent;
        StringBuilder sb = new StringBuilder();
        if (pass) {
            int affixLength = random.nextInt((length - 1) / 2);
            sb.append(RandomStringUtils.randomAlphabetic(affixLength));
            sb.append(RandomStringUtils.randomNumeric(length - affixLength * 2));
            sb.append(RandomStringUtils.randomAlphabetic(affixLength));
        }
        else {
            sb.append(RandomStringUtils.randomAlphabetic(length));
        }
        STRINGS.add(sb.toString());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public MatchResult testMyNFASearch() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return NFA_SEARCH_METHOD.find(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public MatchResult testMyDFASearch() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return MY_DFA.search(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testRegexSearch() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = STRINGS.get(index);
        return REGEX_PATTERN.matcher(toMatch).find();
    }
}

