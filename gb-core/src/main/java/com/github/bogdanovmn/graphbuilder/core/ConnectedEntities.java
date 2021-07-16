package com.github.bogdanovmn.graphbuilder.core;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public abstract class ConnectedEntities {
    private final String dataSourceId;

    abstract public Set<Connection> connections();
}
