package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.gradle;


import lombok.extern.slf4j.Slf4j;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.OutputStream;

@Slf4j
public class GradleProject implements Closeable {
    private final String rootDir;
    private final ProjectConnection project;

    public GradleProject(String rootDir) {
        this.rootDir = rootDir;
        this.project = GradleConnector.newConnector()
            .forProjectDirectory(new File(rootDir))
            .connect();
    }

    public String[] runTaskAndReturnOutput(String taskName) {
        return runTaskAndReturnOutput(null, taskName);
    }

    public String[] runTaskAndReturnOutput(String subProject, String taskName) {
        BuildLauncher buildLauncher = project.newBuild();
        buildLauncher.forTasks(
            subProject != null && !subProject.isEmpty()
                ? String.format("%s:%s", subProject, taskName)
                : taskName
        );

        OutputStream outputStream = new ByteArrayOutputStream();
        buildLauncher.setStandardOutput(outputStream).run();
        return outputStream.toString().split("\\n");
    }

    @Override
    public void close() {
        project.close();
    }
}
