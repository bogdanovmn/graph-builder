package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.gradle;

import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.Module;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
class GradleModule implements Module {
    String name;
    Set<ModuleDependency> dependencies;

    @Override
    public boolean isMeta() {
        return false;
    }

    @Override
    public ModuleDependency asDependency() {
        return ModuleDependency.builder()
            .artifactId(name())
        .build();
    }
}
