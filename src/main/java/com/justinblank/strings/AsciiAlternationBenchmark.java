package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class AsciiAlternationBenchmark {

    // from cat /usr/share/dict/words | shuf | head -n 30
    final static List<String> PARTS = Arrays.asList(
            "intermediatory",
            "dry-beat",
            "overrigorous",
            "highhandedness",
            "feinter",
            "Pro-prussian",
            "wenzel",
            "ghoul",
            "momentum",
            "enantiomorph",
            "southwests",
            "Holdredge",
            "HCSDS",
            "meneghinite",
            "octic",
            "spike-kill",
            "bacterin",
            "keraunophobia",
            "tropeolin",
            "autoserum",
            "lushy",
            "mundal",
            "frivolized",
            "multigravida",
            "Dyersville",
            "STM",
            "spewed",
            "Wabbaseka",
            "columnize",
            "adequately");

    static final com.justinblank.strings.Pattern PATTERN;
    final List<String> strings = new ArrayList<>();

    static final String REGEX_STRING = PARTS.stream().collect(Collectors.joining("|"));
    static final java.util.regex.Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);
    static final SearchMethod METHOD = NFA.createNFA(REGEX_STRING);
    static final DFA MY_DFA = DFA.createDFA(REGEX_STRING);

    private int index = -1;

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
            sb.append(PARTS.get(new Random().nextInt(PARTS.size())));
        }
        else {
            sb.append(RandomStringUtils.randomAscii(PARTS.get(new Random().nextInt(PARTS.size())).length()));
        }
        strings.add(sb.toString());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyMatcher() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        Matcher instance = PATTERN.matcher(toMatch);
        return instance.matches();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyNFA() throws Exception {
        index = (index + 1) % 10000;
        String toMatch = strings.get(index);
        return METHOD.matcher(toMatch).matches();
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

