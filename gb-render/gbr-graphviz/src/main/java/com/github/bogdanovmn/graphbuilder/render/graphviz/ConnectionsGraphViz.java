package com.github.bogdanovmn.graphbuilder.render.graphviz;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionsGraph;
import com.github.bogdanovmn.graphbuilder.core.GraphOutputOptions;
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
import java.util.Set;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

@RequiredArgsConstructor
public class ConnectionsGraphViz implements ConnectionsGraph {
    private final Set<Connection> connections;
    private final GraphOutputOptions options;

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
                        Font.size(16), Shape.BOX, Style.ROUNDED
                    )
                )
            .linkAttr().with(Color.GRAY)
            .with(
                connections.stream()
                    .map(
                        connection -> {
                            Node fromNode = node(connection.from().id());
                            Color linkColor = Color.GRAY;
                            if (connection.from().isRoot()) {
                                fromNode = fromNode.with(
                                    Attributes.attrs(
                                        Attributes.attr("color", "sandybrown"),
                                        Attributes.attr("style", "rounded,filled"),
                                        Attributes.attr("fillcolor", "sandybrown"),
                                        Attributes.attr("shape", "box")
                                    )
                                );
                                linkColor = Color.SANDYBROWN;
                            }

                            if (connection.to() != null) {
                                Node toNode = node(connection.to().id());
                                if (connection.to().isLeaf()) {
                                    toNode = toNode.with(
                                        Attributes.attrs(
                                            Attributes.attr("color", "green"),
                                            Attributes.attr("style", "rounded,filled"),
                                            Attributes.attr("fillcolor", "lightgreen"),
                                            Attributes.attr("shape", "box")
                                        )
                                    );
                                    linkColor = Color.GREEN;
                                }
                                return fromNode.link(to(toNode).with(linkColor));
                            }
                            return fromNode;
                    }
                ).collect(Collectors.toList())
            );
        Graphviz graphviz = Graphviz.fromGraph(g);
        if (options.handMade()) {
            graphviz = graphviz.processor(
                new Roughifyer()
                    .bowing(1)
                    .curveStepCount(1)
                    .roughness(1)
                    .fillStyle(
                        FillStyle.crossHatch().width(3).gap(3).angle(0))
//                    .font("*serif", "Comic Sans MS") // doesn't work correctly
            );
        }
        graphviz.render(Format.PNG)
            .toFile(new File(fileName));
    }
}
