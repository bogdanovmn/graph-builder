package com.github.bogdanovmn.graphbuilder.source.multimoduleproject;

import java.util.Set;

public interface Module {
    String name();
    boolean isMeta();

    ModuleDependency asDependency();

    Set<ModuleDependency> dependencies();
}
