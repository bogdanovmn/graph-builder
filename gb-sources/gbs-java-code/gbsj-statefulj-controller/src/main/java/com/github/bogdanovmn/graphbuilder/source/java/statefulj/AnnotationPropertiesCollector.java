package com.github.bogdanovmn.graphbuilder.source.java.statefulj;

import com.github.bogdanovmn.graphbuilder.core.Connection;
import com.github.bogdanovmn.graphbuilder.core.ConnectionNode;
import com.github.bogdanovmn.graphbuilder.core.ConnectionNodeCluster;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

class AnnotationPropertiesCollector extends VoidVisitorAdapter<AnnotationPropertiesCollectorContext> {
    @Override
    public void visit(NormalAnnotationExpr node, AnnotationPropertiesCollectorContext context) {
        if (node.getNameAsString().equals("Transition")) {
            Connection.ConnectionBuilder connection = Connection.builder();
            ConnectionNode from = null;
            ConnectionNode to = null;
            final ConnectionNodeCluster cluster = ConnectionNodeCluster.builder()
                .id(context.classId())
                .title(context.className())
            .build();
            for (MemberValuePair p : node.getPairs()) {
                String attrValue = p.getValue().toString();
                StatusName status = new StatusName(attrValue);
                switch (p.getNameAsString()) {
                    case "from":
                        from = ConnectionNode.builder()
                            .id(cluster.id() + status.name())
                            .cluster(cluster)
                            .title(status.name())
                        .build();
                        break;
                    case "to":
                        to = ConnectionNode.builder()
                                .id(cluster.id() + status.name())
                                .cluster(cluster)
                                .title(status.name())
                        .build();
                        break;
                    case "event":
                        connection.note(attrValue);
                        break;
                }
            }
            if (from != null || to != null) {
                connection
                    .from(
                        from == null ? anyNodeWithinCluster(cluster) : from
                    )
                    .to(
                        to == null ? anyNodeWithinCluster(cluster) : to
                    );
                context.addConnection(connection.build());
            }
        }
        super.visit(node, context);
    }

    private ConnectionNode anyNodeWithinCluster(ConnectionNodeCluster cluster) {
        return ConnectionNode.builder()
            .id(cluster.id() + "Any")
            .cluster(cluster)
            .title("*")
        .build();
    }
}
