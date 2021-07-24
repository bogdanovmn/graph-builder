package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import com.github.bogdanovmn.common.stream.StringMap;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.Module;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ProjectModel;
import org.apache.maven.model.Model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MavenProjectModel implements ProjectModel {
    private final StringMap<Model> poms;
    private Set<Module> modules;

    public MavenProjectModel(Set<Model> entities) {
        this.poms = new StringMap<>(
            entities,
            pom -> new MavenDependency(pom).key()
        );
    }

    public Set<Module> modules() {
        if (modules == null) {
            modules = poms.values().stream()
                .map(pom -> {
                    Set<String> parentKeys = new HashSet<>();
                    collectParents(pom, parentKeys);
                    return MavenModule.builder()
                        .pom(pom)
                        .parents(
                            parentKeys.stream()
                                .map(poms::get)
                                .collect(Collectors.toList())
                        )
                        .build();
                }).collect(Collectors.toSet());
        }
        return modules;
    }

    @Override
    public boolean hasModule(ModuleDependency dependency) {
        return poms.containsKey(dependency.key());
    }

    @Override
    public Set<String> allModuleKeys() {
        return new HashSet<>(
            poms.keySet()
        );
    }

    @Override
    public Module moduleByKey(String moduleKey) {
        return modules().stream()
            .filter(x -> x.asDependency().key().equals(moduleKey))
            .findFirst()
            .orElse(null);
    }

    private void collectParents(Model pom, Set<String> parentKeys) {
        if (pom.getParent() != null) {
            MavenDependency parentDependency = new MavenDependency(pom.getParent());
            String parentKey = parentDependency.key();
            if (poms.containsKey(parentKey)) {
                parentKeys.add(parentKey);
                collectParents(poms.get(parentKey), parentKeys);
            }
        }
    }
}
