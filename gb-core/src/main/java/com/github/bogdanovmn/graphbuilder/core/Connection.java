package com.github.bogdanovmn.graphbuilder.core;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class Connection {
    @NonNull
    @With
    Node from;
    @With
    Node to;
    String note;

    @Override
    public String toString() {
        return to == null
            ? String.format("%s", from)
            : String.format(
                "%s --> %s", from, to
            );
    }

    @Value
    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class Node {
        @NonNull
        @EqualsAndHashCode.Include
        String id;
        String title;
        @With
        boolean isRoot;
        @With
        boolean isLeaf;

        public static Node of(String id) {
            return Node.builder().id(id).build();
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
            return id + (
                attrs.isEmpty()
                ? ""
                : String.format(
                    "[%s]",
                    String.join(",", attrs)
                )
            );
        }
    }
}
