package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import com.justinblank.strings.Search.SearchMethods;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class AsciiSearchMethodsBenchmark {

    private final List<String> strings = new ArrayList<>();
    private final List<String> patternStrings = new ArrayList<>();
    private SearchMethod method;
    private java.util.regex.Pattern regex;
    private Pattern pattern;
    private SearchMethod nfa;
    private DFA dfa;
    private int index;

    @Param({"4", "16", "256"})
    int patternStringMaxLength;

    @Param({"4", "16", "256"})
    int patternStringCount;

    @Param({"16", "256", "4096"})
    int stringLength;

    @Param({".1", ".5", ".9"})
    double successPct;

    private int iterCount = 10000;

    @Setup
    public void setup() {
        if (patternStringMaxLength > stringLength) {
            System.exit(0);
        }
        // no reason you couldn't look at this data, but the benchmark takes forever to run
        else if (stringLength < 4096 && successPct != .5) {
            System.exit(0);
        }
        if (stringLength > 256) {
            iterCount = 100;
        }
        Random random = new Random();
        for (int i = 0; i < patternStringCount; i++) {
            addPatternString(random);
        }
        method = SearchMethods.makeSearchMethod(patternStrings);
        String regexString = makeRegexString(patternStrings);
        regex = java.util.regex.Pattern.compile(regexString);
        nfa = NFA.createNFA(regexString);
        dfa = DFA.createDFA(regexString);
        try {
            pattern = DFACompiler.compile(regexString, "SearchMethodsBenchmarkTestClass");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < iterCount; i++) {
            addRandomString(random);
        }
    }

    protected static String makeRegexString(List<String> patterns)  {
        return "(" + StringUtils.join(patterns, ")|(") + ")".replaceAll("\\\\", "\\\\");
    }

    private void addPatternString(Random random) {
        int length = Math.max(patternStringMaxLength / 2, random.nextInt(patternStringMaxLength));
        patternStrings.add(RandomStringUtils.randomAlphanumeric(length));
    }

    private void addRandomString(Random random) {
        strings.add(mkRandomString(random, patternStrings, successPct, stringLength));
    }

    protected static String mkRandomString(Random random, List<String> patternStrings, double successPct, int stringLength) {
        boolean pass = random.nextDouble() < successPct;
        if (pass) {
            StringBuilder sb = new StringBuilder();
            String patternToMatch = patternStrings.get(random.nextInt(patternStrings.size()));
            int prefixLength = Math.max(0, random.nextInt(stringLength - patternToMatch.length()));
            sb.append(RandomStringUtils.randomAscii(prefixLength));
            sb.append(patternToMatch);
            sb.append(RandomStringUtils.randomAscii(stringLength - sb.length()));
            return sb.toString();
        } else {
            return RandomStringUtils.randomAscii(stringLength);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testAsciiAhoCorasick() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        return method.containedIn(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testIteration() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        for (String s : patternStrings) {
            int current = toMatch.indexOf(s);
            if (current != -1) {
                return true;
            }
        }
        return false;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testRegex() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        java.util.regex.Matcher matcher = regex.matcher(toMatch);
        return matcher.find();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyNFA() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        return nfa.containedIn(toMatch);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyDFA() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        // TODO: replace with containedIn method
        return dfa.search(toMatch).matched;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyPattern() {
        index = (index + 1) % iterCount;
        String toMatch = strings.get(index);
        return pattern.matcher(toMatch).containedIn();
    }

    public static void main(String[] args){
        try {
            AsciiSearchMethodsBenchmark bench = new AsciiSearchMethodsBenchmark();
            bench.successPct = .5;
            bench.stringLength = 4096;
            bench.patternStringMaxLength = 16;
            bench.patternStringCount = 16;
            bench.setup();
           for (int i = 0; i < 1000; i++) {
                bench.testMyDFA();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
