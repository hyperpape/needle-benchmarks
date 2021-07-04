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
public class DigitBenchmark {

    static final com.justinblank.strings.Pattern PATTERN;
    final List<String> strings = new ArrayList<>();
    static final String REGEX_STRING = "[0-9]+";
    static final java.util.regex.Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);
    static final SearchMethod SEARCH_METHOD_NFA = NFA.createNFA(REGEX_STRING);
    static final DFA MY_DFA = DFA.createDFA(REGEX_STRING);

    private int index = -1;

    @Param({"1", "16", "256"})
    int stringLength;

    @Param({".1", ".5", ".9"})
    double successPct;

    @Param({"0", ".5"})
    double failureMatchPct;

    static {
        try {
            PATTERN = DFACompiler.compile(REGEX_STRING, "testClass");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Setup
    public void setup() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            addRandomString(random);
        }
    }

    private void addRandomString(Random random) {
        boolean pass = random.nextDouble() < successPct;
        StringBuilder sb = new StringBuilder();
        if (pass) {
            sb.append(RandomStringUtils.randomNumeric(stringLength));
        }
        else {
            int subLength = (int) (failureMatchPct * stringLength);
            if (subLength == stringLength) {
                subLength--;
            }
            sb.append(RandomStringUtils.randomNumeric(subLength));
            sb.append(RandomStringUtils.randomAscii(stringLength - subLength));
        }
        strings.add(sb.toString());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean pattern() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        Matcher instance = PATTERN.matcher(toMatch);
        return instance.matches();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean nfa() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return SEARCH_METHOD_NFA.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean dfa() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return MY_DFA.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean javaRegex() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return REGEX_PATTERN.matcher(toMatch).matches();
    }
}
