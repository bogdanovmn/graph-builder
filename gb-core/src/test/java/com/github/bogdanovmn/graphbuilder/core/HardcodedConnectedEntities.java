package com.github.bogdanovmn.graphbuilder.core;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class HardcodedConnectedEntities extends ConnectedEntities {
    private final Stream<String> connectionStringViews;

    public HardcodedConnectedEntities(Stream<String> connectionStringViews) {
        super(null);
        this.connectionStringViews = connectionStringViews;
    }

    @Override
    public Set<Connection> connections() {
        return connectionStringViews.map(
            line -> {
                String[] components = line.split(" --> ");
                return Connection.builder()
                    .from(
                        ConnectionNode.of(components[0])
                    )
                    .to(
                        ConnectionNode.of(components[1])
                    )
                    .build();
            }
        ).collect(Collectors.toSet());
    }
}
