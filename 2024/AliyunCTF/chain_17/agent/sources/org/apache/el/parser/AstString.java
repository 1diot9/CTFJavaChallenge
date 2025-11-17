package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstString.class */
public final class AstString extends SimpleNode {
    private volatile String string;

    public AstString(int id) {
        super(id);
    }

    public String getString() {
        if (this.string == null) {
            this.string = this.image.substring(1, this.image.length() - 1);
        }
        return this.string;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getString();
    }

    @Override // org.apache.el.parser.SimpleNode
    public void setImage(String image) {
        char c1;
        if (image.indexOf(92) == -1) {
            this.image = image;
            return;
        }
        int size = image.length();
        StringBuilder buf = new StringBuilder(size);
        int i = 0;
        while (i < size) {
            char c = image.charAt(i);
            if (c == '\\' && i + 1 < size && ((c1 = image.charAt(i + 1)) == '\\' || c1 == '\"' || c1 == '\'')) {
                c = c1;
                i++;
            }
            buf.append(c);
            i++;
        }
        this.image = buf.toString();
    }
}
