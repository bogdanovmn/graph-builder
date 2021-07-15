package com.github.bogdanovmn.graphbuilder.mavenmodules;

import lombok.Builder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
class MavenModule {
    public static final String PACKAGING_POM = "pom";

    private final Model pom;
    private final List<Model> parents;

    public Set<MavenDependency> dependencies() {
        Set<Dependency> deps = new HashSet<>();
        if (pom.getDependencies() != null) {
            deps.addAll(pom.getDependencies());
        }
        parents.forEach(p -> deps.addAll(p.getDependencies()));
        return deps.stream()
            .map(MavenDependency::new)
            .collect(Collectors.toSet());
    }

    public String artifactId() {
        return pom.getArtifactId();
    }

    public MavenDependency asDependency() {
        return new MavenDependency(pom);
    }

    public boolean isPomPackaging() {
        return PACKAGING_POM.equals(pom.getPackaging());
    }
}
