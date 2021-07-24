package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;

import java.util.Set;

public interface ProjectModel {
    Set<Module> modules();

    Set<String> allModuleKeys();

    boolean hasModule(ModuleDependency dependency);

    Module moduleByKey(String key);
}
