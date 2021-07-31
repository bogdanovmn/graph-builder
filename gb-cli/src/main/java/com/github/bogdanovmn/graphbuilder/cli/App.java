package com.github.bogdanovmn.graphbuilder.cli;

import com.github.bogdanovmn.cmdline.CmdLineAppBuilder;
import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionsGraph;
import com.github.bogdanovmn.graphbuilder.core.GraphOutputOptions;
import com.github.bogdanovmn.graphbuilder.render.graphviz.ConnectionsGraphViz;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Set;

class App {

    public static final String ARG_GRAPH_TYPE = "graph-type";
    public static final String ARG_DATA_SOURCE = "data-source";
    public static final String ARG_OUTPUT_DIR = "output-dir";
    public static final String ARG_VERBOSE = "verbose";
    public static final String ARG_HAND_MADE = "rough";
    public static final String ARG_MERGE_LINKS = "merge-links";

    public static void main(String[] args) throws Exception {

        new CmdLineAppBuilder(args)
            .withJarName("graph-builder")
            .withDescription("Generate a graph from a source of data")

            .withRequiredArg(ARG_DATA_SOURCE, "data source directory")
            .withRequiredArg(ARG_GRAPH_TYPE, "data source & target graph type")
            .withRequiredArg(ARG_OUTPUT_DIR, "where results have to be created")

            .withFlag(ARG_VERBOSE, "print additional info")
            .withFlag(ARG_HAND_MADE, "make a graph with hand made style")
            .withFlag(ARG_MERGE_LINKS, "try to decrease a number of links")

            .withEntryPoint(
                cmdLine -> {
                    DataSourceId dataSourceId = new DataSourceId(
                        cmdLine.getOptionValue(ARG_DATA_SOURCE)
                    );
                    GraphType type = GraphType.valueOf(
                        cmdLine.getOptionValue(ARG_GRAPH_TYPE)
                    );
                    Set<Connection> connections = type.connectedEntitiesInstance(
                        dataSourceId.value()
                    ).processedConnections();

                    if (cmdLine.hasOption(ARG_VERBOSE)) {
                        connections.stream().sorted(
                            Comparator.comparing(c -> c.from().id())
                        ).forEach(System.out::println);
                    }

                    ConnectionsGraph graph = new ConnectionsGraphViz(
                        connections,
                        GraphOutputOptions.builder()
                            .handMade(cmdLine.hasOption(ARG_HAND_MADE))
                            .mergeLinks(cmdLine.hasOption(ARG_MERGE_LINKS))
                        .build()
                    );
                    graph.saveAsImage(
                        String.format(
                            "%s/%s--%s--%s.png",
                                cmdLine.getOptionValue(ARG_OUTPUT_DIR),
                                type.name().toLowerCase(),
                                dataSourceId.shortValue(),
                                ZonedDateTime.now().format(
                                    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                                )
                        )
                    );
                }
            ).build().run();
    }
}
