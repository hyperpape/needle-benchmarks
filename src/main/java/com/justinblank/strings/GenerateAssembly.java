package com.justinblank.strings;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateAssembly {

    static final Pattern PATTERN = DFACompiler.compile("(Sherlock)|(Street)", "SherlockStreet");
    static final Path PATH = Paths.get("/home/justin/code/needle/needle-compiler/src/test/resources/sherlockholmesascii.txt");
    static String HAYSTACK;

    static {
        try {
            generateFile();
            HAYSTACK = Files.readAllLines(PATH).get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void generateFile() throws Exception {
        StringBuilder s = new StringBuilder();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            names.add("Watson");
            names.add("Adler");
            names.add("Moriarty");
            names.add("Sarah");
        }
        names.add("Sherlock");
        names.add("Street");
        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            String name = names.get(random.nextInt(names.size()));
            s.append(' ').append(name);
        }
        Files.write(PATH, s.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void main(String[] args) {
        try {
            new GenerateAssembly().foo();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }

    public void foo() throws Exception {
        int count = 0;

        for (int i = 0; i < 1000; i++) {
            Matcher matcher = PATTERN.matcher(HAYSTACK);
            while (matcher.find().matched) {
                count++;
            }
        }
        Thread.sleep(10000L);
        System.out.println("Count is " + count);
    }
}
