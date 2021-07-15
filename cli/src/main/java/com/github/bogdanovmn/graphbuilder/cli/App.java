package com.github.bogdanovmn.graphbuilder.cli;

import com.github.bogdanovmn.cmdline.CmdLineAppBuilder;
import com.github.bogdanovmn.graphbuilder.core.Connection;

import java.util.Comparator;

class App {

    public static final String ARG_GRAPH_TYPE = "graph-type";
    public static final String ARG_DATA_SOURCE = "data-source";

    public static void main(String[] args) throws Exception {

        new CmdLineAppBuilder(args)
            .withJarName("graph-builder")
            .withDescription("Generate a graph from a source of data")

            .withRequiredArg(ARG_DATA_SOURCE, "data source directory")
            .withRequiredArg(ARG_GRAPH_TYPE, "data source & target graph type")

            .withEntryPoint(
                cmdLine -> {
                    GraphType.valueOf(
                        cmdLine.getOptionValue(ARG_GRAPH_TYPE)
                    ).connectedEntitiesInstance(
                        cmdLine.getOptionValue(ARG_DATA_SOURCE)
                    ).connections()
                        .stream().sorted(
                            Comparator.comparing(Connection::from)
                        ).forEach(System.out::println);
                }
            ).build().run();
    }
}
