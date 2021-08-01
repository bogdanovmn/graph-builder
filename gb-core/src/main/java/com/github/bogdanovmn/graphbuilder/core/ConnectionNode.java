package com.github.bogdanovmn.graphbuilder.core;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@With
public class ConnectionNode {
    @NonNull
    @EqualsAndHashCode.Include
    String id;
    String title;
    boolean isRoot;
    boolean isLeaf;
    ConnectionNodeCluster cluster;

    public static ConnectionNode of(String id) {
        return ConnectionNode.builder().id(id).build();
    }

    public String title() {
        return title == null ? id : title;
    }

    @Override
    public String toString() {
        List<String> attrs = new ArrayList<>(2);
        if (isLeaf) {
            attrs.add("L");
        }
        if (isRoot) {
            attrs.add("R");
        }
        return (cluster != null ? cluster.id() + ":: " : "") + id + (
            attrs.isEmpty()
                ? ""
                : String.format(
                "[%s]",
                String.join(",", attrs)
            )
        );
    }
}
