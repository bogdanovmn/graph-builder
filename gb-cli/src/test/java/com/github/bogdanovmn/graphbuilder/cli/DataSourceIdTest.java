package com.github.bogdanovmn.graphbuilder.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSourceIdTest {

    @ParameterizedTest
    @CsvSource({
        "/path/to/files,files",
        "/path/to/files/some.java,some.java",
        "/path_to-file.txt,path_to-file.txt"
    })
    void shortValue(String id, String expectedShortValue) {
        assertEquals(
            expectedShortValue,
            new DataSourceId(id).shortValue()
        );
    }
}