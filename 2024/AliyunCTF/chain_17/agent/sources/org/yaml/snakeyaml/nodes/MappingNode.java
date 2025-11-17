package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/nodes/MappingNode.class */
public class MappingNode extends CollectionNode<NodeTuple> {
    private List<NodeTuple> value;
    private boolean merged;

    public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(tag, startMark, endMark, flowStyle);
        this.merged = false;
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.resolved = resolved;
    }

    public MappingNode(Tag tag, List<NodeTuple> value, DumperOptions.FlowStyle flowStyle) {
        this(tag, true, value, null, null, flowStyle);
    }

    @Override // org.yaml.snakeyaml.nodes.Node
    public NodeId getNodeId() {
        return NodeId.mapping;
    }

    @Override // org.yaml.snakeyaml.nodes.CollectionNode
    public List<NodeTuple> getValue() {
        return this.value;
    }

    public void setValue(List<NodeTuple> mergedValue) {
        this.value = mergedValue;
    }

    public void setOnlyKeyType(Class<? extends Object> keyType) {
        for (NodeTuple nodes : this.value) {
            nodes.getKeyNode().setType(keyType);
        }
    }

    public void setTypes(Class<? extends Object> keyType, Class<? extends Object> valueType) {
        for (NodeTuple nodes : this.value) {
            nodes.getValueNode().setType(valueType);
            nodes.getKeyNode().setType(keyType);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (NodeTuple node : getValue()) {
            buf.append("{ key=");
            buf.append(node.getKeyNode());
            buf.append("; value=");
            if (node.getValueNode() instanceof CollectionNode) {
                buf.append(System.identityHashCode(node.getValueNode()));
            } else {
                buf.append(node);
            }
            buf.append(" }");
        }
        String values = buf.toString();
        return "<" + getClass().getName() + " (tag=" + getTag() + ", values=" + values + ")>";
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public boolean isMerged() {
        return this.merged;
    }
}
