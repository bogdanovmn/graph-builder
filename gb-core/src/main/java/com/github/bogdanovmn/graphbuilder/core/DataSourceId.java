package com.github.bogdanovmn.graphbuilder.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DataSourceId {
    @NonNull
    protected final String id;

    public String value() {
        return id;
    }

    public abstract String shortValue();
}
