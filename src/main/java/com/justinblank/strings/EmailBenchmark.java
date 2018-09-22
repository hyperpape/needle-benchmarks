package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

// I don't care what a real email address is, it's just a low complexity but non-trivial pattern
@State(Scope.Benchmark)
public class EmailBenchmark {

    static final com.justinblank.strings.Pattern pattern;
    static final String REGEX_STRING = "[A-Za-z]+@[A-Za-z0-9]+.com";
    static final java.util.regex.Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);
    static final SearchMethod NFA_SEARCH_METHOD = NFA.createNFA(REGEX_STRING);
    static final DFA MY_DFA = DFA.createDFA(REGEX_STRING);

    private int index = -1;
    final List<String> strings = new ArrayList<>();


    @Param({"16", "256", "4096"})
    int stringLength = 16;

    @Param({".1", ".5", ".9"})
    double successPct;

    static {
        try {
            pattern = DFACompiler.compile(REGEX_STRING, "EmailBenchmarkPattern");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Setup
    public void setup() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            strings.add(mkRandomString(random, stringLength, successPct));
        }
    }

    public static String mkRandomString(Random random, int targetLength, double successProbability) {
        boolean pass = random.nextDouble() < successProbability;
        // Domain must be at least 1 char and we need .com for success
        StringBuilder sb = new StringBuilder();
        if (pass) {
            // we need six chars for domain, @ and .com, then add 1 to ensure non-zero length string
            int length = random.nextInt(targetLength- 7);
            sb.append(RandomStringUtils.random(1 + length, true, false  ));
            sb.append("@");
            int domainLength = targetLength - length - 7;
            sb.append(RandomStringUtils.random(1 + domainLength, true, false));
            sb.append(".com");
            if (sb.length() != targetLength) {
                throw new IllegalStateException("Incorrect string length, actual=" + sb.length() + ", expected =" + targetLength);
            }
        }
        else {
            sb.append(RandomStringUtils.randomAscii(targetLength));
        }
        return sb.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyMatcher() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        Matcher instance = pattern.matcher(toMatch);
        return instance.matches();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyNFA() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return NFA_SEARCH_METHOD.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyDFA() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return MY_DFA.matches(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testRegexMatcher() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return REGEX_PATTERN.matcher(toMatch).matches();
    }
}
