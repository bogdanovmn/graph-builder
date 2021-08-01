package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.FilesDataSource;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class StatefuljControllerTransitionConnectedEntities extends ConnectedEntities {
    private final FilesDataSource dataSource;

    public StatefuljControllerTransitionConnectedEntities(String dataSourceId) {
        super(dataSourceId);
        this.dataSource = new StatefuljJavaCodeDataSource(dataSourceId);
    }

    @Override
    public Set<Connection> connections() {
        HashSet<Connection> connections = new HashSet<>();

        dataSource.entities().forEach(file -> {
            LOG.debug(file.toString());
            try {
                connections.addAll(
                    parsedConnections(file)
                );
            } catch (IOException e) {
                LOG.error("File {} parsing error: {}", file, e.getMessage(), e);
            }
        });

        return connections;
    }

    private Set<Connection> parsedConnections(Path file) throws IOException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);
        AnnotationPropertiesCollectorContext context = new AnnotationPropertiesCollectorContext(file);
        compilationUnit.accept(
            new AnnotationPropertiesCollector(),
            context
        );
        return context.connections();
    }

}
