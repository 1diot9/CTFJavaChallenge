package org.yaml.snakeyaml.nodes;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/nodes/NodeTuple.class */
public final class NodeTuple {
    private final Node keyNode;
    private final Node valueNode;

    public NodeTuple(Node keyNode, Node valueNode) {
        if (keyNode == null || valueNode == null) {
            throw new NullPointerException("Nodes must be provided.");
        }
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    public Node getKeyNode() {
        return this.keyNode;
    }

    public Node getValueNode() {
        return this.valueNode;
    }

    public String toString() {
        return "<NodeTuple keyNode=" + this.keyNode + "; valueNode=" + this.valueNode + ">";
    }
}
