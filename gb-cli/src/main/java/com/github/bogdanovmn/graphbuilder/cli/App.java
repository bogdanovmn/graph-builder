package com.github.bogdanovmn.graphbuilder.cli;

import com.github.bogdanovmn.graphbuilder.core.ConnectedEntities;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionsGraph;
import com.github.bogdanovmn.graphbuilder.core.GraphOutputOptions;
import com.github.bogdanovmn.graphbuilder.render.graphviz.ConnectionsGraphViz;
import com.github.bogdanovmn.jaclin.CLI;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Set;

@Slf4j
class App {

    public static final String ARG_GRAPH_TYPE = "graph-type";
    public static final String ARG_DATA_SOURCE = "data-source";
    public static final String ARG_OUTPUT_DIR = "output-dir";
    public static final String ARG_OUTPUT_PREFIX = "output-file-prefix";
    public static final String ARG_VERBOSE = "verbose";
    public static final String ARG_HAND_MADE = "rough";
    public static final String ARG_MERGE_LINKS = "merge-links";

    public static void main(String[] args) throws Exception {

        new CLI("graph-builder", "Generate a graph from a source of data")
            .withRequiredOptions()
                .strArg(ARG_DATA_SOURCE, "data source directory")
                .strArg(ARG_OUTPUT_DIR, "where results have to be created")
            .withOptions()
                .enumArg(ARG_GRAPH_TYPE, "data source & target graph type", GraphType.class)
                    .withDefault(GraphType.PROJECT_MODULE_DEPENDENCY)
                .strArg(ARG_OUTPUT_PREFIX, "output file prefix")
                .flag(ARG_HAND_MADE, "make a graph with hand made style")
                .flag(ARG_MERGE_LINKS, "try to decrease a number of links")
                .flag(ARG_VERBOSE, "print additional info")

            .withEntryPoint(
                options -> {
                    GraphType type = (GraphType) options.getEnum(ARG_GRAPH_TYPE);
                    ConnectedEntities connectedEntities = type.connectedEntitiesInstance(
                        options.get(ARG_DATA_SOURCE)
                    );
                    Set<Connection> connections = connectedEntities.processedConnections();

                    if (options.enabled(ARG_VERBOSE)) {
                        connections.stream().sorted(
                            Comparator.comparing(c -> c.from().id())
                        ).forEach(System.out::println);
                    }

                    ConnectionsGraph graph = new ConnectionsGraphViz(
                        connections,
                        GraphOutputOptions.builder()
                            .handMade(options.enabled(ARG_HAND_MADE))
                            .mergeLinks(options.enabled(ARG_MERGE_LINKS))
                        .build()
                    );

                    String timestampSuffix = ZonedDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                    );
                    Path outputFilePath = Paths.get(
                        options.get(ARG_OUTPUT_DIR),
                        type.name().toLowerCase(),
                        connectedEntities.dataSourceId().shortValue(),
                        options.has(ARG_OUTPUT_PREFIX)
                            ? String.format("%s--%s", options.get(ARG_OUTPUT_PREFIX), timestampSuffix)
                            : timestampSuffix
                    );
                    outputFilePath.getParent().toFile().mkdirs();
                    graph.saveAsImage(
                        outputFilePath.toAbsolutePath().toString()
                    );
                    LOG.info("output file: {}", outputFilePath.toAbsolutePath());
                }
            ).run(args);
    }
}
