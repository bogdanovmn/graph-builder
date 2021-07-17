package com.github.bogdanovmn.graphbuilder.source.mavenmodules;

import com.github.bogdanovmn.common.stream.StringMap;
import org.apache.maven.model.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class MavenProjectModel {
    private final StringMap<Model> modules;

    public MavenProjectModel(Set<Model> entities) {
        this.modules = new StringMap<>(
            entities,
            model -> new MavenDependency(model).key()
        );
    }

    public List<MavenModule> modules() {
        return modules.values().stream()
            .map(pom -> {
                Set<String> parentKeys = new HashSet<>();
                collectParents(pom, parentKeys);
                return MavenModule.builder()
                    .pom(pom)
                    .parents(
                        parentKeys.stream()
                            .map(modules::get)
                            .collect(Collectors.toList())
                    )
                    .build();
            }).collect(Collectors.toList());
    }

    public boolean hasModule(MavenDependency dependency) {
        return modules.containsKey(dependency.key());
    }

    public Set<String> allModuleKeys() {
        return new HashSet<>(
            modules.keySet()
        );
    }

    public Model moduleByKey(String moduleKey) {
        return modules.get(moduleKey);
    }

    private void collectParents(Model pom, Set<String> parentKeys) {
        if (pom.getParent() != null) {
            MavenDependency parentDependency = new MavenDependency(pom.getParent());
            String parentKey = parentDependency.key();
            if (modules.containsKey(parentKey)) {
                parentKeys.add(parentKey);
                collectParents(modules.get(parentKey), parentKeys);
            }
        }
    }
}
