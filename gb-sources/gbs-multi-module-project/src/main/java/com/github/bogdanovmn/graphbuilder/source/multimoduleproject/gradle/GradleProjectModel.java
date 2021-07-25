package com.github.bogdanovmn.graphbuilder.source.multimoduleproject.gradle;


import com.github.bogdanovmn.common.stream.StringMap;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.Module;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ModuleDependency;
import com.github.bogdanovmn.graphbuilder.source.multimoduleproject.ProjectModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GradleProjectModel implements ProjectModel {
    private final GradleProject project;

    // Root project 'spring' - Spring Framework
    private final static Pattern ROOT_PROJECT_PATTERN = Pattern.compile("^Root project '([^']+)'.*");
    // +--- Project ':framework-bom' - Spring Framework (Bill of Materials)
    private final static Pattern SUB_PROJECT_PATTERN = Pattern.compile(".*--- Project '([^']+)'.*");
    // +--- project :spring-aop
    private final static Pattern PROJECT_DEPENDENCY_PATTERN = Pattern.compile("^\\+--- project (\\S+)(\\s+\\(.\\))?$");

    private final static List<Pattern> DEPENDENCY_PATTERNS = Arrays.asList(
        //+--- io.spring.javaformat:spring-javaformat-checkstyle:0.0.15
        Pattern.compile("^\\+--- ([^:]+):([^:]+)\\s+->\\s+(\\S+)$"),
        //+--- org.junit.jupiter:junit-jupiter-api -> 5.7.2
        Pattern.compile("^\\+--- ([^:]+):([^:]+):([^:]+)$")
    );

    private StringMap<Module> modules;
    private String rootProjectName;

    @Override
    public Set<Module> modules() {
        fetchModules();
        return new HashSet<>(
            modules.values()
        );
    }

    @Override
    public Set<String> allModuleKeys() {
        fetchModules();
        return new HashSet<>(
            modules.keySet()
        );
    }

    @Override
    public boolean hasModule(ModuleDependency dependency) {
        fetchModules();
        return modules.containsKey(dependency.key());
    }

    @Override
    public Module moduleByKey(String key) {
        fetchModules();
        return modules.get(key);
    }

    private void fetchModules() {
        if (modules == null) {
            Set<Module> names = projectNames().stream()
                .map(name ->
                    GradleModule.builder()
                        .name(name)
                        .dependencies(
                            projectDependencies(name)
                        )
                        .build()
                ).collect(Collectors.toSet());
            modules = new StringMap<>(
                names,
                m -> m.asDependency().key()
            );
            modules.put(
                rootProjectName,
                GradleModule.builder()
                    .name(rootProjectName)
                    .dependencies(
                        projectDependencies("")
                    )
                .build()
            );
            project.close();
        }
    }

    private Set<String> projectNames() {
        String[] output = project.runTaskAndReturnOutput("projects");
        Set<String> result = new HashSet<>();
        for (String line : output) {
            System.out.println(line);
            Matcher subProjectMatchingResult = SUB_PROJECT_PATTERN.matcher(line);
            if (subProjectMatchingResult.matches()) {
                result.add(
                    subProjectName(
                        subProjectMatchingResult.group(1)
                    )
                );
            } else {
                Matcher rootProjectMatchingResult = ROOT_PROJECT_PATTERN.matcher(line);
                if (rootProjectMatchingResult.matches()) {
                    this.rootProjectName = rootProjectMatchingResult.group(1);
                }
            }
        }
        LOG.debug("Total modules: {}", result.size());
        return result;
    }

    private Set<ModuleDependency> projectDependencies(String projectName) {
        String[] output = project.runTaskAndReturnOutput(projectName, "dependencies");
        Set<ModuleDependency> result = new HashSet<>();
        for (String line : output) {
            Matcher projectDependencyMatchingResult = PROJECT_DEPENDENCY_PATTERN.matcher(line);
            if (projectDependencyMatchingResult.matches()) {
                String projectDependency = subProjectName(
                    projectDependencyMatchingResult.group(1)
                );
                if (!projectName.equals(projectDependency)) {
                    result.add(
                        ModuleDependency.builder()
                            .artifactId(projectDependency)
                        .build()
                    );
                }
            } else {
                for (Pattern pattern : DEPENDENCY_PATTERNS) {
                    Matcher dependencyMatchingResult = pattern.matcher(line);
                    if (dependencyMatchingResult.matches()) {
                        result.add(
                            ModuleDependency.builder()
                                .groupId(dependencyMatchingResult.group(1))
                                .artifactId(dependencyMatchingResult.group(2))
                                .version(dependencyMatchingResult.group(3))
                                .build()
                        );
                        break;
                    }
                }
            }
        }
        LOG.debug("Module {} total dependencies: {}", projectName, result.size());
        return result;
    }

    private String subProjectName(String rawName) {
        return rawName.startsWith(":")
            ? rawName.substring(1)
            : rawName;
    }
}
