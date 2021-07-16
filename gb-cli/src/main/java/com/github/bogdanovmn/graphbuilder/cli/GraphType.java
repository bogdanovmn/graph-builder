package com.github.bogdanovmn.graphbuilder.cli;

import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.mavenmodules.MavenModuleDependencyConnectedEntities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum GraphType {
    MAVEN_MODULE_DEPENDENCY(MavenModuleDependencyConnectedEntities.class),
    MAVEN_MODULE_PARENT(null),
    STATEFULJ_TRANSITION(null);

    private final Class<? extends ConnectedEntities> connectedEntitiesClass;

    public ConnectedEntities connectedEntitiesInstance(String dataSourceId) {
        try {
            return connectedEntitiesClass.getConstructor(String.class).newInstance(dataSourceId);
        } catch (Exception ex) {
            throw new IllegalStateException(
                String.format("%s must have a constructor with single String argument", connectedEntitiesClass),
                ex
            );
        }
    }
}
