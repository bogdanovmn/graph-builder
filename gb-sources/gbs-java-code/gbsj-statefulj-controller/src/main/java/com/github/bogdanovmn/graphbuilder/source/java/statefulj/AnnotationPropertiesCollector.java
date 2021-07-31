package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.Connection.Node;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;
import java.util.Set;

class AnnotationPropertiesCollector extends VoidVisitorAdapter<Set<Connection>> {
    @Override
    public void visit(NormalAnnotationExpr node, Set<Connection> connections) {
        if (node.getNameAsString().equals("Transition")) {
            Connection.ConnectionBuilder connection = Connection.builder();
            Node from = null;
            Node to = null;
            String cluster = null;
            for (MemberValuePair p : node.getPairs()) {
                String attrValue = p.getValue().toString();
                StatusName status = new StatusName(attrValue);
                switch (p.getNameAsString()) {
                    case "from":
                        from = Node.builder()
                            .id(attrValue)
                            .cluster(status.prefix())
                            .title(status.name())
                        .build();
                        cluster = status.prefix();
                        break;
                    case "to":
                        to = Node.builder()
                                .id(attrValue)
                                .cluster(status.prefix())
                                .title(status.name())
                        .build();
                        cluster = status.prefix();
                        break;
                    case "event":
                        connection.note(attrValue);
                        break;
                }
            }
            if (from != null || to != null) {
                connection
                    .from(
                        from == null
                            ? Node.builder()
                                .id(
                                    Optional.ofNullable(cluster).orElse("") + "Any"
                                )
                                .cluster(cluster)
                                .title("*")
                            .build()
                            : from
                    )
                    .to(to);
                connections.add(connection.build());
            }
        }
        super.visit(node, connections);
    }
}
