package com.github.bogdanovmn.graphbuilder.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
public class Connection {
    @NonNull
    @With
    ConnectionNode from;
    @With
    ConnectionNode to;
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
