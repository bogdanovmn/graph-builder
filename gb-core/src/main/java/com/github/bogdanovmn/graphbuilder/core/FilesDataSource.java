package com.github.bogdanovmn.graphbuilder.core;

import java.nio.file.Path;
import java.util.Set;

public interface FilesDataSource extends DataSource<Path> {
    Set<Path> entities();
}
