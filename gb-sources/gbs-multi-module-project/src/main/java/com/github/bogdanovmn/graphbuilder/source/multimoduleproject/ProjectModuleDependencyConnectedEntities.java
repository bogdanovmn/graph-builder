package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;


import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ProjectModuleDependencyConnectedEntities extends ConnectedEntities {
    private final ProjectModel projectModel;

    public ProjectModuleDependencyConnectedEntities(String projectDir) throws IOException {
        super(projectDir);
        this.projectModel = new ProjectRootDir(projectDir).model();
    }

    @Override
    public Set<Connection> connections() {
        Set<Connection> result = new HashSet<>();
        Set<String> orphanModules = projectModel.allModuleKeys();

        projectModel.modules().stream()
            .filter(module -> !module.isMeta())
            .forEach(module -> {
                module.dependencies().forEach(
                    dependency -> {
                        if (projectModel.hasModule(dependency)) {
                            result.add(
                                Connection.builder()
                                    .from(
                                        ConnectionNode.of(
                                            module.name()
                                        )
                                    )
                                    .to(
                                        ConnectionNode.of(
                                            dependency.artifactId()
                                        )
                                    )
                                .build()
                            );
                            orphanModules.remove(module.name());
                            orphanModules.remove(dependency.key());
                        }
                    }
                );
            });
        orphanModules.stream()
            .map(projectModel::moduleByKey)
            .filter(module -> !module.isMeta())
            .forEach(
                module -> result.add(
                    Connection.builder()
                        .from(
                            ConnectionNode.of(
                                module.name()
                            )
                        )
                    .build()
                )
            );
        return result;
    }
}
