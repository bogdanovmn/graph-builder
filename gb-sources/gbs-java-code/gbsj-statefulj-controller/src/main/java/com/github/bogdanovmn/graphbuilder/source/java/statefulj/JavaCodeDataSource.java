package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.Directory;
import com.github.bogdanovmn.graphbuilder.core.FilesDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
class JavaCodeDataSource implements FilesDataSource {
    private final String dir;

    public JavaCodeDataSource(String dir) {
        this.dir = dir;
    }

    @Override
    public Set<Path> entities() {
        try {
            return new Directory(dir).filesWithExt("java");
        } catch (IOException e) {
            LOG.error("Can't read some files: {}", e.getMessage(), e);
        }
        return null;
    }
}
