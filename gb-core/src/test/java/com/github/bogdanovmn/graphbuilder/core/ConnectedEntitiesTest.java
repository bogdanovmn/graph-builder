package com.github.bogdanovmn.graphbuilder.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectedEntitiesTest {

    @Test
    void processedConnections() {
        Set<Connection> connections = new HardcodedConnectedEntities(
            Stream.of(
                "translator-cli-parser --> translator-core",
                "translator-cli-word-definition --> translator-lib-oxforddictionaries",
                "translator-etl-allitbooks --> translator-etl-allitbooks-orm",
                "translator-etl-allitbooks --> translator-web-orm",
                "translator-etl-allitbooks-orm --> translator-core",
                "translator-lib-oxforddictionaries --> translator-core",
                "translator-web-app --> translator-lib-oxforddictionaries",
                "translator-web-app --> translator-web-orm",
                "translator-web-app --> translator-etl-allitbooks-orm",
                "translator-web-app --> translator-core"
            )
        ).processedConnections();

        assertEquals(10, connections.size());

        Set<String> roots = connections.stream()
            .map(Connection::from)
            .filter(Connection.Node::isRoot)
            .map(Connection.Node::id)
            .collect(Collectors.toSet());

        Set<String> leaves = connections.stream()
            .map(Connection::to)
            .filter(Connection.Node::isLeaf)
            .map(Connection.Node::id)
            .collect(Collectors.toSet());

        assertEquals(2, leaves.size());
        assertEquals(4, roots.size());

        assertTrue(
            leaves.containsAll(
                Arrays.asList(
                    "translator-core",
                    "translator-web-orm"
                )
            )
        );

        assertTrue(
            roots.containsAll(
                Arrays.asList(
                    "translator-web-app",
                    "translator-cli-parser",
                    "translator-cli-word-definition",
                    "translator-etl-allitbooks"
                )
            )
        );
    }
}