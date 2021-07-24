package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.Module;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import lombok.Builder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
class MavenModule implements Module {
    private static final String PACKAGING_POM = "pom";

    private final Model pom;
    private final List<Model> parents;

    @Override
    public String name() {
        return pom.getArtifactId();
    }

    @Override
    public boolean isMeta() {
        return PACKAGING_POM.equals(pom.getPackaging());
    }

    @Override
    public ModuleDependency asDependency() {
        return ModuleDependency.builder()
            .artifactId(pom.getArtifactId())
            .groupId(groupId())
            .version(pom.getVersion())
        .build();
    }

    @Override
    public Set<ModuleDependency> dependencies() {
        Set<Dependency> deps = new HashSet<>();
        if (pom.getDependencies() != null) {
            deps.addAll(pom.getDependencies());
        }
        parents.forEach(p -> deps.addAll(p.getDependencies()));
        return deps.stream()
            .map(d -> ModuleDependency.builder()
                    .artifactId(d.getArtifactId())
                    .groupId(d.getGroupId())
                    .version(d.getVersion())
                .build()
            )
            .collect(Collectors.toSet());
    }

    private String groupId() {
        String result = null;
        if (pom.getGroupId() == null) {
            if (pom.getParent() != null) {
                result = pom.getParent().getGroupId();
            }
        } else {
            result = pom.getGroupId();
        }
        return result;
    }
}
