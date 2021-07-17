package com.github.bogdanovmn.graphbuilder.source.java.statefulj;


import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.FilesDataSource;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
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
        Set<Connection> connections = new HashSet<>();
        compilationUnit.accept(new AnnotationPropertiesVisitor(), connections);
        return connections;
    }

    private static class AnnotationPropertiesVisitor extends VoidVisitorAdapter<Set<Connection>> {
        @Override
        public void visit(NormalAnnotationExpr n, Set<Connection> connections) {
            if (n.getNameAsString().equals("Transition")) {
                LOG.debug("Annotation: {}", n.getName());
                Connection.ConnectionBuilder connection = Connection.builder().from("*");
                n.getPairs().forEach(
                    p -> {
                        LOG.trace("{}={}", p.getName(), p.getValue());
                        switch (p.getNameAsString()) {
                            case "from":
                                connection.from(p.getValue().toString());
                                break;
                            case "to":
                                connection.to(p.getValue().toString());
                                break;
                            case "event":
                                connection.note(p.getValue().toString());
                                break;
                        }
                    }
                );
                connections.add(connection.build());
            }
            super.visit(n, connections);
        }
    }

//    private static class MethodVisitor extends VoidVisitorAdapter<String> {
//        @Override
//        public void visit(MethodDeclaration n, String arg) {
//            n.accept(new AnnotationPropertiesVisitor(), n.getName().toString());
//            super.visit(n, arg);
//        }
//    }
}
