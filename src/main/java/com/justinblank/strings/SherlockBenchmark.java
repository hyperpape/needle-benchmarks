package com.justinblank.strings;

import com.justinblank.strings.Search.SearchMethod;
import org.openjdk.jmh.annotations.*;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SherlockBenchmark {

    private static final String SHERLOCK_TEXT;

    // Note: The options featuring Adler are structurally equivalent to the others but will consume much more input before being
    // matched

    @Param({"Sherlock", "Adler", "Sherlock|Holmes", "Sherlock|Street", "Adler|Watson", "([Ss]herlock)|([Hh]olmes)",
            "Sherlock|Holmes|Watson|Irene|Adler|John|Baker", "the\\s+\\w+", "zqj", "aqj", "[a-q][^u-z]{13}x",
            "[a-zA-Z]+ing", "Holmes.{0,25}Watson|Watson.{0,25}Holmes", "\\s[a-zA-Z]{0,12}ing\\s"})
    String regexString = "([Ss]herlock)|(Hholmes)";
    SearchMethod method;
    SearchMethod nfa;
    DFA dfa;
    Pattern pattern;
    java.util.regex.Pattern javaRegex;

    static {
        try {
            var resource = SherlockBenchmark.class.getClassLoader().getResource("sherlock.txt");

            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystem zipfs = FileSystems.newFileSystem(resource.toURI(), env);
            SHERLOCK_TEXT = Files.readString(Path.of(resource.toURI()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
    public boolean testMySearchMethod() {
        return method.matcher(SHERLOCK_TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyNFA() {
        return nfa.matcher(SHERLOCK_TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyDFA() {
        return dfa.search(SHERLOCK_TEXT).matched;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testJavaRegex() {
        return javaRegex.matcher(SHERLOCK_TEXT).find();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testMyPattern() {
        return pattern.matcher(SHERLOCK_TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testAdlerWatsonHandMatcher() {
        return new AdlerWatsonHandMatcher(SHERLOCK_TEXT).containedIn();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testAdlerContains() {
        return SHERLOCK_TEXT.contains("Adler");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean testSherlockContains() {
        return SHERLOCK_TEXT.contains("Sherlock");
    }

    public static void main(String[] args) {
        Pattern pattern = DFACompiler.compile("Sherlock", "SherlockPattern");
        int counter = 0;
        for (int i = 0; i < 1000000; i++) {
            String s;
            if (new Random().nextBoolean()) {
                s = "Abcdefer Sherlock";
            }
            else {
                if (new Random().nextBoolean()) {
                    s = "Abcdefer Watson";
                }
                else {
                    s = "Abcdefer Adler";
                }
            }
            if (pattern.matcher(s).containedIn()) {
                counter++;
            }
            if (new AdlerWatsonHandMatcher(s).containedIn()) {
                counter++;
            }
        }
        System.out.println(counter);
    }
}
