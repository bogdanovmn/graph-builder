package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.FilesDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
class StatefuljJavaCodeDataSource implements FilesDataSource {
    private final JavaCodeDataSource dataSource;

    public StatefuljJavaCodeDataSource(String dir) {
        this.dataSource = new JavaCodeDataSource(dir);
    }

    @Override
    public Set<Path> entities() {
        Set<Path> statefuljFiles = new HashSet<>();
        try {
            Set<Path> allJavaFiles =  dataSource.entities();
            for (Path file : allJavaFiles) {
                Files.readAllLines(file).stream()
                    .filter(line -> line.contains("@StatefulController"))
                    .findFirst()
                    .ifPresent(x -> statefuljFiles.add(file));
            }
        } catch (IOException e) {
            LOG.error("Can't read some files: {}", e.getMessage(), e);
        }
        return statefuljFiles;
    }
}
