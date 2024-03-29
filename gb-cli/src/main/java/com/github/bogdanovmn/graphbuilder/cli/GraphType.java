package com.github.bogdanovmn.graphbuilder.cli;

import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.source.java.statefulj.StatefuljControllerTransitionConnectedEntities;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ProjectModuleDependencyConnectedEntities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum GraphType {
    PROJECT_MODULE_DEPENDENCY(ProjectModuleDependencyConnectedEntities.class),
    JAVA_STATEFULJ_TRANSITION(StatefuljControllerTransitionConnectedEntities.class);

    private final Class<? extends ConnectedEntities> connectedEntitiesClass;

    public ConnectedEntities connectedEntitiesInstance(String dataSourceId) {
        try {
            return connectedEntitiesClass.getConstructor(String.class).newInstance(dataSourceId);
        } catch (Exception ex) {
            throw new IllegalStateException(
                String.format("%s must have a constructor with a single String argument", connectedEntitiesClass),
                ex
            );
        }
    }
}
