package com.github.bogdanovmn.graphbuilder.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ConnectionNodeCluster {
    @NonNull
    String id;
    String title;

    public String title() {
        return title == null ? id : title;
    }
}
