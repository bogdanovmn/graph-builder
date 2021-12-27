package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.Module;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import lombok.Builder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Profile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Dependency> deps = new ArrayList<>();
        if (pom.getDependencies() != null) {
            deps.addAll(pom.getDependencies());
            deps.addAll(profileDependencies(pom));
        }
        parents.forEach(
            parentPom -> {
                deps.addAll(parentPom.getDependencies());
                deps.addAll(profileDependencies(parentPom));
            }
        );
        return deps.stream()
            .map(d -> ModuleDependency.builder()
                    .artifactId(d.getArtifactId())
                    .groupId(d.getGroupId())
                    .version(d.getVersion())
                .build()
            )
            .collect(Collectors.toSet());
    }

    private static List<Dependency> profileDependencies(Model pom) {
        List<Profile> profiles = pom.getProfiles();
        if (profiles != null) {
            return profiles.stream()
                .map(ModelBase::getDependencies)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
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
