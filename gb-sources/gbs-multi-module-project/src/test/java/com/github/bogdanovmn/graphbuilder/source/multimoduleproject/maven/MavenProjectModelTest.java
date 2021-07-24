package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.maven;

import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ProjectModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MavenProjectModelTest {

    private static ProjectModel projectModel;

    @BeforeAll
    static void init() {
        projectModel =  new MavenProjectModel(
            new MavenPomFilesDataSource("src/test/resources/maven-project").entities()
        );
    }
    @Test
    void modules() {
        assertEquals(8, projectModel.modules().size());
    }

    @Test
    void hasModule() {
        assertTrue(projectModel.hasModule(
            ModuleDependency.builder()
                .groupId("com.github.bogdanovmn.graphbuilder")
                .artifactId("gb-core")
            .build()
        ));

        assertFalse(projectModel.hasModule(
            ModuleDependency.builder()
                .groupId("com.github.bogdanovmn.graphbuilder")
                .artifactId("fake")
                .build()
        ));
    }

    @Test
    void allModuleKeys() {
        assertThat(
            projectModel.allModuleKeys()
        ).containsOnly(
            "com.github.bogdanovmn.graphbuilder:graph-builder",
            "com.github.bogdanovmn.graphbuilder:gb-sources",
            "com.github.bogdanovmn.graphbuilder:gb-cli",
            "com.github.bogdanovmn.graphbuilder:gb-core",
            "com.github.bogdanovmn.graphbuilder:gb-render",
            "com.github.bogdanovmn.graphbuilder:gbs-multi-module-project",
            "com.github.bogdanovmn.graphbuilder:gbs-java-code",
            "com.github.bogdanovmn.graphbuilder:gbr-graphviz"
        );
    }

    @Test
    void moduleByKey() {
        assertEquals(
            "gb-cli",
            projectModel.moduleByKey("com.github.bogdanovmn.graphbuilder:gb-cli").name()
        );
    }
}