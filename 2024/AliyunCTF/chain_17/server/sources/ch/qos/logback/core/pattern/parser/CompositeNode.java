package ch.qos.logback.core.pattern.parser;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/CompositeNode.class */
public class CompositeNode extends SimpleKeywordNode {
    Node childNode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompositeNode(String keyword) {
        super(2, keyword);
    }

    public Node getChildNode() {
        return this.childNode;
    }

    public void setChildNode(Node childNode) {
        this.childNode = childNode;
    }

    @Override // ch.qos.logback.core.pattern.parser.SimpleKeywordNode, ch.qos.logback.core.pattern.parser.FormattingNode, ch.qos.logback.core.pattern.parser.Node
    public boolean equals(Object o) {
        if (!super.equals(o) || !(o instanceof CompositeNode)) {
            return false;
        }
        CompositeNode r = (CompositeNode) o;
        return this.childNode != null ? this.childNode.equals(r.childNode) : r.childNode == null;
    }

    @Override // ch.qos.logback.core.pattern.parser.SimpleKeywordNode, ch.qos.logback.core.pattern.parser.FormattingNode, ch.qos.logback.core.pattern.parser.Node
    public int hashCode() {
        return super.hashCode();
    }

    @Override // ch.qos.logback.core.pattern.parser.SimpleKeywordNode, ch.qos.logback.core.pattern.parser.Node
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.childNode != null) {
            buf.append("CompositeNode(" + String.valueOf(this.childNode) + ")");
        } else {
            buf.append("CompositeNode(no child)");
        }
        buf.append(printNext());
        return buf.toString();
    }
}
