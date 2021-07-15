package com.github.bogdanovmn.graphbuilder.mavenmodules;


import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MavenModuleDependencyConnectedEntities extends ConnectedEntities {
    private final MavenPomFilesDataSource dataSource;

    public MavenModuleDependencyConnectedEntities(String dataSourceId) {
        super(dataSourceId);
        this.dataSource = new MavenPomFilesDataSource(dataSourceId);
    }

    @Override
    public Set<Connection> connections() {
        Set<Connection> result = new HashSet<>();
        MavenProjectModel projectModel = new MavenProjectModel(
            dataSource.entities()
        );
        Set<String> orphanModules = projectModel.allModuleKeys();

        projectModel.modules().stream()
            .filter(module -> !module.isPomPackaging())
            .forEach(module -> {
                module.dependencies().forEach(
                    dependency -> {
                        String depKey = dependency.key();
                        if (projectModel.hasModule(dependency)) {
                            result.add(
                                Connection.builder()
                                    .from(module.artifactId())
                                    .to(dependency.artifactId())
                                .build()
                            );
                            orphanModules.remove(module.asDependency().key());
                            orphanModules.remove(depKey);
                        }
                    }
                );
            });
        orphanModules.stream()
            .map(projectModel::moduleByKey)
            .filter(model -> !MavenModule.builder().pom(model).build().isPomPackaging())
            .forEach(
                pom -> result.add(
                    Connection.builder()
                        .from(pom.getArtifactId())
                    .build()
                )
            );
        return result;
    }
}
