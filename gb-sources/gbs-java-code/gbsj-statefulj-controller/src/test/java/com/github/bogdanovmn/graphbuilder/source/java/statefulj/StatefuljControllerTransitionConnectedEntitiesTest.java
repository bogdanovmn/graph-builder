package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatefuljControllerTransitionConnectedEntitiesTest {

    @Test
    void connections() {
        StatefuljControllerTransitionConnectedEntities entities = new StatefuljControllerTransitionConnectedEntities("src/test/resources/source-code");
        Set<Connection> connections = entities.processedConnections();

        assertEquals(4, connections.size());
    }
}