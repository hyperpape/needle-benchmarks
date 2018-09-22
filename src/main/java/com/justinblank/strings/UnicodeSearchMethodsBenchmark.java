package com.justinblank.strings;

import com.justinblank.strings.DFA;
import com.justinblank.strings.DFACompiler;
import com.justinblank.strings.Pattern;
import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.Search.SearchMethods;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class UnicodeSearchMethodsBenchmark {

    private final List<String> strings = new ArrayList<>();
    private final List<String> patternStrings = new ArrayList<>();
    private SearchMethod method;
    private java.util.regex.Pattern regex;
    private DFA dfa;
    private Pattern pattern;
    private int index;

    @Param({"4", "16", "256"})
    int patternStringMaxLength;

    @Param({"4", "16", "256"})
    int patternStringCount;

    @Param({"16", "256", "4096"})
    int stringLength;

    @Param({".1", ".5", ".9"})
    double successPct;

    @Setup
    public void setup() {
        if (patternStringMaxLength > stringLength) {
            System.exit(0);
        }
        Random random = new Random();
        for (int i = 0; i < patternStringCount; i++) {
            addPatternString(random);
        }
        method = SearchMethods.makeSearchMethod(patternStrings);
        String regexString = makeRegexString(patternStrings);
        regex = java.util.regex.Pattern.compile(regexString);
        dfa = DFA.createDFA(regexString);
        try {
            pattern = DFACompiler.compile(regexString, "SearchMethodsBenchmarkTestClass");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 10000; i++) {
            addRandomString(random);
        }
    }

    protected static String makeRegexString(List<String> patterns) {
        return "(" + StringUtils.join(patterns, ")|(") + ")";
    }

    private void addPatternString(Random random) {
        RandomStringGenerator.Builder builder = new RandomStringGenerator.Builder();
        builder.withinRange(0x370, 0xA00);
        patternStrings.add(builder.build().generate(1, patternStringMaxLength));
    }

    private void addRandomString(Random random) {
        strings.add(mkRandomString(random, patternStrings, successPct, stringLength));
    }

    protected static String mkRandomString(Random random, List<String> patternStrings, double successPct, int stringLength) {
        boolean pass = random.nextDouble() < successPct;
        if (pass) {
            StringBuilder sb = new StringBuilder();
            String patternToMatch = patternStrings.get(random.nextInt(patternStrings.size()));
            int prefixLength = 0;
            if (stringLength > patternToMatch.length()) {
                prefixLength =  random.nextInt(stringLength - patternToMatch.length());
            }
            sb.append(RandomStringUtils.random(prefixLength));
            sb.append(patternToMatch);
            sb.append(RandomStringUtils.random(stringLength - sb.length()));
            return sb.toString();
        } else {
            return RandomStringUtils.randomAscii(stringLength);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int testAsciiAhoCorasick() {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return method.findIndex(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int testIteration() {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        int found = -1;
        for (String s : patternStrings) {
            int current = toMatch.indexOf(s);
            if (current != -1) {
                if (found == -1 || current < found) {
                    found = current;
                }
            }
        }
        return found;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int testRegex() {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        java.util.regex.Matcher matcher = regex.matcher(toMatch);
        return matcher.find() ? matcher.start() : -1;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int testMyDFA() {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return dfa.search(toMatch).start;
    }
}