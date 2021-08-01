package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
class AnnotationPropertiesCollectorContext {
    private final Set<Connection> connections = new HashSet<>();
    private final Path classFile;

    void addConnection(Connection connection) {
        connections.add(connection);
    }

    String className() {
        return classFile.getFileName().toString().split("\\.")[0];
    }

    String classId() {
        return classFile.toAbsolutePath().toString();
    }

    Set<Connection>  connections() {
        return new HashSet<>(connections);
    }
}
