package com.github.bogdanovmn.graphbuilder.cli;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DataSourceId {
    @NonNull
    private final String id;

    public String value() {
        return id;
    }

    public String shortValue() {
        String[] components = id.split("[^a-zA-Z._-]");
        return components[components.length - 1];
    }
}
