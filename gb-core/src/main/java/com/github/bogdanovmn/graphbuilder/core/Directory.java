package com.github.bogdanovmn.graphbuilder.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Directory {
    private final Path dir;

    public Directory(String dir) {
        this.dir = Paths.get(dir);
    }

    private Set<Path> filesRecursively(Predicate<String> nameRule) throws IOException {
        return Files.walk(dir)
            .filter(Files::isRegularFile)
            .filter(f -> nameRule.test(f.getFileName().toString()))
            .collect(Collectors.toSet());
    }

    public Set<Path> files(Predicate<String> nameRule) throws IOException {
        return Files.list(dir)
            .filter(Files::isRegularFile)
            .filter(f -> nameRule.test(f.getFileName().toString()))
            .collect(Collectors.toSet());
    }

    public Set<Path> filesWithExtRecursively(String extension) throws IOException {
        return filesRecursively(name -> name.endsWith("." + extension));
    }

    public Set<Path> filesWithNameRecursively(String fileName) throws IOException {
        return filesRecursively(name -> name.equals(fileName));
    }

    public Set<Path> filesWithExt(String extension) throws IOException {
        return files(name -> name.endsWith("." + extension));
    }

    public Set<Path> filesWithName(String fileName) throws IOException {
        return files(name -> name.equals(fileName));
    }

    @Override
    public String toString() {
        return dir.toString();
    }
}
