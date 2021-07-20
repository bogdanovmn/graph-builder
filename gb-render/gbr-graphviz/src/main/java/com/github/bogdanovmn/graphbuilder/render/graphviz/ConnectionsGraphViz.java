package com.github.bogdanovmn.graphbuilder.render.graphviz;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionsGraph;
import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class ConnectionsGraphViz implements ConnectionsGraph {
    private final Set<Connection> connections;

    public ConnectionsGraphViz(Set<Connection> connections) {
        this.connections = connections;
        System.out.println(connections.toString());
    }

    @Override
    public void saveAsImage(String fileName) throws IOException {
        Graph g = graph("connections")
            .directed()
            .strict()
            .graphAttr()
                .with(
                    Attributes.attrs(
                        Rank.dir(TOP_TO_BOTTOM),
                        Attributes.attr("dpi", "150")
                    )
                )
            .nodeAttr()
                .with(
                    Attributes.attrs(
                        Font.size(16)
                    )
                )
            .linkAttr().with(Color.GRAY)
            .with(
                connections.stream()
                    .map(
                        connection -> {
                            Node fromNode = node(connection.from().id()).with(
                                connection.from().isRoot()
                                    ? Color.BROWN
                                    : Color.BLACK
                            );
                            if (connection.to() != null) {
                                Node toNode = node(connection.to().id()).with(
                                    connection.to().isLeaf()
                                        ? Color.GREEN1
                                        : Color.BLACK
                                );
                                return fromNode.link(toNode);
                            }
                            return fromNode;
                    }
                ).collect(Collectors.toList())
            );
        Graphviz.fromGraph(g)
            .render(Format.PNG)
            .toFile(new File(fileName));
    }
}
