package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import com.github.bogdanovmn.graphbuilder.core.DataSource;
import com.github.bogdanovmn.graphbuilder.core.Directory;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MavenPomFilesDataSource implements DataSource<Model> {
    private final String dir;

    private final static Pattern TEST_RESOURCES_PATH_PATTERN = Pattern.compile(".*/test/resources/.*");
    private final static Pattern TARGET_PATH_PATTERN = Pattern.compile(".*/target/.*");

    public MavenPomFilesDataSource(String dir) {
        this.dir = dir;
    }

    @Override
    public Set<Model> entities() {
        Set<Path> files;
        try {
            files = new Directory(dir).filesWithNameRecursively("pom.xml").stream()
                .filter(
                    path -> TEST_RESOURCES_PATH_PATTERN.matcher(dir).matches()
                         || !TEST_RESOURCES_PATH_PATTERN.matcher(path.toString()).matches()
                )
                .filter(path -> !TARGET_PATH_PATTERN.matcher(path.toString()).matches())
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException("Can't read all entities", e);
        }
        LOG.debug("Found {} file(s)", files.size());
        Set<Model> modules = new HashSet<>();
        for (Path filePath : files) {
            LOG.debug("Looking at {}", filePath);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                modules.add(
                    reader.read(
                        new FileReader(filePath.toFile())
                    )
                );
            } catch (Exception e) {
                LOG.error("Processing file error: {} - {}", filePath.getFileName(), e.getMessage(), e);
            }
        }
        return modules;
    }
}
