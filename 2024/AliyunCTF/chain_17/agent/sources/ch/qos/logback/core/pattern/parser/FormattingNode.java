package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.FormatInfo;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/FormattingNode.class */
public class FormattingNode extends Node {
    FormatInfo formatInfo;

    FormattingNode(int type) {
        super(type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FormattingNode(int type, Object value) {
        super(type, value);
    }

    public FormatInfo getFormatInfo() {
        return this.formatInfo;
    }

    public void setFormatInfo(FormatInfo formatInfo) {
        this.formatInfo = formatInfo;
    }

    @Override // ch.qos.logback.core.pattern.parser.Node
    public boolean equals(Object o) {
        if (!super.equals(o) || !(o instanceof FormattingNode)) {
            return false;
        }
        FormattingNode r = (FormattingNode) o;
        return this.formatInfo != null ? this.formatInfo.equals(r.formatInfo) : r.formatInfo == null;
    }

    @Override // ch.qos.logback.core.pattern.parser.Node
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + (this.formatInfo != null ? this.formatInfo.hashCode() : 0);
    }
}
