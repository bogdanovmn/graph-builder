package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public
class ModuleDependency {
    @EqualsAndHashCode.Include
    String artifactId;
    @EqualsAndHashCode.Include
    String groupId;
    String version;

    public String key() {
        return String.format(
            "%s:%s", groupId, artifactId
        );
    }
}
