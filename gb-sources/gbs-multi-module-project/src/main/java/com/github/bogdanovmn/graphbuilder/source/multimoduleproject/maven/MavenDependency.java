package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import lombok.Value;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

@Value
class MavenDependency {
    String groupId;
    String artifactId;
    String version;

    MavenDependency(Model model) {
        this.groupId = model.getGroupId() == null
            ? model.getParent().getGroupId()
            : model.getGroupId();
        this.artifactId = model.getArtifactId();
        this.version = model.getVersion() == null
            ? model.getParent().getVersion()
            : model.getVersion();
    }

    MavenDependency(Parent parent) {
        this.groupId = parent.getGroupId();
        this.artifactId = parent.getArtifactId();
        this.version = parent.getVersion();
    }

    String key() {
        return String.format(
            "%s:%s",
            groupId, artifactId
        );
    }
}
