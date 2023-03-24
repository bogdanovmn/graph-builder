package com.github.bogdanovmn.graphbuilder.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Paths;

public class DirectoryDataSourceId extends DataSourceId {

    public DirectoryDataSourceId(@NonNull String id) {
        super(id);
    }

    public String shortValue() {
        try {
            return Paths.get(id).toAbsolutePath()
                .toRealPath()
                .getFileName().toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
