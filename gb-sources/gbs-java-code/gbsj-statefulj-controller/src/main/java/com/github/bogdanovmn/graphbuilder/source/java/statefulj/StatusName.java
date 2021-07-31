package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

class StatusName {
    private final String fullName;

    StatusName(String fullName) {
        this.fullName = fullName;
    }

    String prefix() {
        return fullName.contains(".")
            ? fullName.substring(0, fullName.indexOf("."))
            : null;
    }

    String name() {
        return fullName.contains(".")
            ? fullName.substring(fullName.indexOf(".") + 1)
            : fullName;
    }
}
