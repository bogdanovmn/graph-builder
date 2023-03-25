package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;

import com.github.bogdanovmn.common.files.Directory;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.gradle.GradleProject;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.gradle.GradleProjectModel;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven.MavenPomFilesDataSource;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven.MavenProjectModel;

import java.io.IOException;

class ProjectRootDir {
    private final Directory root;

    public ProjectRootDir(String projectDir) {
        this.root = new Directory(projectDir);
    }

    public ProjectModel model() throws IOException {
        ProjectModel model;
        if (!root.filesWithName("pom.xml").isEmpty()) {
            model = new MavenProjectModel(
                new MavenPomFilesDataSource(root.toString())
                    .entities()
            );
        }
        else if (!root.files((name) -> name.startsWith("build.gradle")).isEmpty()) {
            model = new GradleProjectModel(
                new GradleProject(
                    root.toString()
                )
            );
        }
        else {
            throw new UnsupportedOperationException(
                String.format("Can't find any supported build system configuration file: %s", root)
            );
        }
        return model;
    }
}
