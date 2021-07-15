package com.github.bogdanovmn.graphbuilder.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Connection {
    @NonNull
    String from;
    String to;
    String note;

    @Override
    public String toString() {
        return to == null
            ? String.format("%s", from)
            : String.format(
                "%s --> %s", from, to
            );
    }
}
