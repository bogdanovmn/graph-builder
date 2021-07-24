package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public
class ModuleDependency {
    String artifactId;
    String groupId;
    String version;

    public String key() {
        return String.format(
            "%s:%s", groupId, artifactId
        );
    }
}
