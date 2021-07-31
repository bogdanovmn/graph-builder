package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusNameTest {

    @Test
    void prefix() {
        assertEquals(
            "Blabla",
            new StatusName("Blabla.Name").prefix()
        );
    }

    @Test
    void name() {
        assertEquals(
            "Name",
            new StatusName("Blabla.Name").name()
        );
    }
}