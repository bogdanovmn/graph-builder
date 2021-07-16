package com.github.bogdanovmn.graphbuilder.core;

import java.util.Set;

public interface DataSource<T> {
    Set<T> entities();
}
