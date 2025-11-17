package ch.qos.logback.core.pattern.parser;

import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/SimpleKeywordNode.class */
public class SimpleKeywordNode extends FormattingNode {
    List<String> optionList;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleKeywordNode(Object value) {
        super(1, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SimpleKeywordNode(int type, Object value) {
        super(type, value);
    }

    public List<String> getOptions() {
        return this.optionList;
    }

    public void setOptions(List<String> optionList) {
        this.optionList = optionList;
    }

    @Override // ch.qos.logback.core.pattern.parser.FormattingNode, ch.qos.logback.core.pattern.parser.Node
    public boolean equals(Object o) {
        if (!super.equals(o) || !(o instanceof SimpleKeywordNode)) {
            return false;
        }
        SimpleKeywordNode r = (SimpleKeywordNode) o;
        return this.optionList != null ? this.optionList.equals(r.optionList) : r.optionList == null;
    }

    @Override // ch.qos.logback.core.pattern.parser.FormattingNode, ch.qos.logback.core.pattern.parser.Node
    public int hashCode() {
        return super.hashCode();
    }

    @Override // ch.qos.logback.core.pattern.parser.Node
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.optionList == null) {
            buf.append("KeyWord(" + String.valueOf(this.value) + "," + String.valueOf(this.formatInfo) + ")");
        } else {
            buf.append("KeyWord(" + String.valueOf(this.value) + ", " + String.valueOf(this.formatInfo) + "," + String.valueOf(this.optionList) + ")");
        }
        buf.append(printNext());
        return buf.toString();
    }
}
