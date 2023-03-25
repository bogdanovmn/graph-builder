package com.github.bogdanovmn.graphbuilder.render.graphviz;

import com.github.bogdanovmn.common.random.UniqRandomValue;
import com.github.bogdanovmn.graphbuilder.core.*;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import guru.nidi.graphviz.rough.FillStyle;
import guru.nidi.graphviz.rough.Roughifyer;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

@RequiredArgsConstructor
public class ConnectionsGraphViz implements ConnectionsGraph {
    private final Set<Connection> connections;
    private final GraphOutputOptions options;

    private static final UniqRandomValue<Color> CLUSTER_BG_COLORS = new UniqRandomValue<>(
        Color.HONEYDEW2,
        Color.IVORY2,
        Color.LAVENDER,
        Color.LIGHTCYAN,
        Color.LIGHTYELLOW,
        Color.PAPAYAWHIP,
        Color.SNOW2
    );

    @Override
    public void saveAsImage(String fileName) throws IOException {
        Graph g = graph("connections")
            .directed()
            .strict()
            .graphAttr()
                .with(
                    Attributes.attrs(
                        Rank.dir(TOP_TO_BOTTOM),
                        Attributes.attr("dpi", "150"),
                        Attributes.attr("concentrate", options.mergeLinks())
                    )
                )
            .nodeAttr()
                .with(
                    Attributes.attrs(
                        Font.size(16), Shape.BOX, Style.ROUNDED
                    )
                )
            .linkAttr().with(Color.GRAY)
            .with(nodes())
            .with(clusters());

        Graphviz graphviz = Graphviz.fromGraph(g);
        if (options.handMade()) {
            graphviz = graphviz.processor(
                new Roughifyer()
                    .bowing(1)
                    .curveStepCount(1)
                    .roughness(1)
                    .fillStyle(
                        FillStyle.crossHatch().width(3).gap(3).angle(0))
            );
        }
        graphviz.render(Format.PNG)
            .toFile(new File(fileName));
    }

    private List<Graph> clusters() {
        Map<ConnectionNodeCluster, List<ConnectionNode>> nodesByCluster = connections.stream()
            .flatMap(connection -> Stream.of(connection.from(), connection.to()))
            .filter(n -> n != null)
            .filter(n -> n.cluster() != null)
            .collect(
                Collectors.groupingBy(
                    ConnectionNode::cluster,
                    Collectors.toList()
                )
            );

        List<Graph> result = new ArrayList<>();
        for (ConnectionNodeCluster cluster : nodesByCluster.keySet()) {
            result.add(
                graph(cluster.id()).cluster()
                    .graphAttr()
                        .with(
                            Style.FILLED,
                            CLUSTER_BG_COLORS.next(),
                            Label.of(cluster.title()),
                            Attributes.attr("margin", "10.0")
                        )
                    .with(
                        nodesByCluster.get(cluster).stream()
                            .map(x -> node(x.id()))
                            .collect(Collectors.toList())
                    )
            );
        }
        return result;
    }

    private List<Node> nodes() {
        return connections.stream()
            .map(
                connection -> {
                    Attributes<ForNode> defaultNodeAttrs = nodeAttrs("gray13", "white");
                    Color linkColor = Color.GRAY;

                    Node fromNode = node(
                        connection.from().id()
                    ).with(
                        Label.of(
                            connection.from().title()
                        ),
                        defaultNodeAttrs
                    );

                    if (connection.from().isRoot()) {
                        fromNode = fromNode.with(
                            nodeAttrs("sandybrown")
                        );
                        linkColor = Color.SANDYBROWN;
                    }

                    if (connection.to() != null) {
                        Node toNode = node(
                            connection.to().id()
                        ).with(
                            Label.of(
                                connection.to().title()
                            ),
                            defaultNodeAttrs
                        );

                        if (connection.to().isLeaf()) {
                            toNode = toNode.with(
                                nodeAttrs("lightgreen")
                            );
                            linkColor = Color.GREEN;
                        }
                        return fromNode.link(to(toNode).with(linkColor));
                    }
                    return fromNode;
                }
            ).collect(Collectors.toList());
    }

    private Attributes<ForNode> nodeAttrs(String color) {
        return nodeAttrs(color, color);
    }

    private Attributes<ForNode> nodeAttrs(String borderColor, String fillColor) {
        return Attributes.attrs(
            Attributes.attr("color", borderColor),
            Attributes.attr("style", "rounded,filled"),
            Attributes.attr("fillcolor", fillColor),
            Attributes.attr("shape", "box")
        );
    }
}
