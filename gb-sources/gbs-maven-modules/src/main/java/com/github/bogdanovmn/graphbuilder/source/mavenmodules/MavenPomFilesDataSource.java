package com.github.bogdanovmn.graphbuilder.source.mavenmodules;


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

@Slf4j
public class MavenPomFilesDataSource implements DataSource<Model> {
    private final String dir;

    public MavenPomFilesDataSource(String dir) {
        this.dir = dir;
    }

    @Override
    public Set<Model> entities() {
        Set<Path> files;
        try {
            files = new Directory(dir).filesWithName("pom.xml");
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
