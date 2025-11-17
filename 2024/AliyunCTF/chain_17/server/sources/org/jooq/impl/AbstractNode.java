package org.jooq.impl;

import java.util.HashMap;
import java.util.Map;
import org.jooq.Configuration;
import org.jooq.Node;
import org.jooq.exception.DataDefinitionException;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractNode.class */
abstract class AbstractNode<N extends Node<N>> extends AbstractScope implements Node<N> {
    final N root;
    final String id;
    final String message;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractNode(Configuration configuration, String id, String message, N root) {
        super(configuration);
        this.root = root != null ? root : this;
        this.id = id;
        this.message = (String) StringUtils.defaultIfNull(message, "");
    }

    @Override // org.jooq.Node
    public final String id() {
        return this.id;
    }

    @Override // org.jooq.Node
    public final String message() {
        return this.message;
    }

    @Override // org.jooq.Node
    public final N root() {
        return this.root;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final N commonAncestor(N other) {
        if (id().equals(other.id())) {
            return this;
        }
        Map<N, Integer> a1 = ancestors(this, new HashMap(), 1);
        Map<N, Integer> a2 = ancestors(other, new HashMap(), 1);
        N node = null;
        Integer distance = null;
        for (Map.Entry<N, Integer> entry : a1.entrySet()) {
            if (a2.containsKey(entry.getKey()) && (distance == null || distance.intValue() > entry.getValue().intValue())) {
                node = entry.getKey();
                distance = entry.getValue();
            }
        }
        if (node == null) {
            throw new DataDefinitionException("Versions " + id() + " and " + other.id() + " do not have a common ancestor");
        }
        return node;
    }

    private Map<N, Integer> ancestors(N node, Map<N, Integer> result, int distance) {
        Integer previous = result.get(node);
        if (previous == null || previous.intValue() > distance) {
            result.put(node, Integer.valueOf(distance));
            for (N parent : node.parents()) {
                ancestors(parent, result, distance + 1);
            }
        }
        return result;
    }
}
