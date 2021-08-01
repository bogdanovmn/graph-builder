package com.github.bogdanovmn.graphbuilder.core;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class ConnectedEntities {
    protected final String dataSourceId;

    abstract public Set<Connection> connections();

    public final Set<Connection> processedConnections() {
        Set<Connection> connections = connections();
        Set<ConnectionNode> roots = connections.stream()
            .flatMap(c -> Stream.of(c.from(), c.to()))
            .collect(Collectors.toSet());
        Set<ConnectionNode> leaves = new HashSet<>(roots);

        connections.forEach(c -> {
            leaves.remove(c.from());
            if (c.to() != null) {
                roots.remove(c.to());
            }
        });

        return connections.stream()
            .map(c -> {
                if (roots.contains(c.from())) {
                    c = c.withFrom(
                        c.from().withRoot(true)
                    );
                }
                if (c.to() != null && leaves.contains(c.to())) {
                    c = c.withTo(
                        c.to().withLeaf(true)
                    );
                }
                return c;
            }).collect(Collectors.toSet());
    }
}
