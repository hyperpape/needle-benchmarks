package com.justinblank.strings;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SherlockAsciiText {

    public static final String TEXT;

    static {
        var text = "";
        try {
            // TODO: This is janky, and almost certainly wrong, but it works. Loading the resource without creating the
            //  filesystem works in junit inside Intellij and inside JMH benchmarks, but fails during maven install.
            //  The solution with zipfs fails inside Intellij and JMH. I need to revisit some stuff about classLoaders
            //  and filesystems.
            try {
                var resource = SherlockSingleShotBenchmark.class.getClassLoader().getResource("sherlockholmesascii.txt");
                text = Files.readString(Path.of(resource.toURI()));
            }
            catch (Exception e) {
                var resource = SherlockSingleShotBenchmark.class.getClassLoader().getResource("sherlockholmesascii.txt");
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                FileSystems.newFileSystem(resource.toURI(), env);
                text = Files.readString(Path.of(resource.toURI()));
            }
            TEXT = text;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
