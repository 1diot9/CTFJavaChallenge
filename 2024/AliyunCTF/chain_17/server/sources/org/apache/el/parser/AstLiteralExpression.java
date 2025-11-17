package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstLiteralExpression.class */
public final class AstLiteralExpression extends SimpleNode {
    public AstLiteralExpression(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.image;
    }

    @Override // org.apache.el.parser.SimpleNode
    public void setImage(String image) {
        if (image.indexOf(92) == -1) {
            this.image = image;
            return;
        }
        int size = image.length();
        StringBuilder buf = new StringBuilder(size);
        int i = 0;
        while (i < size) {
            char c = image.charAt(i);
            if (c == '\\' && i + 2 < size) {
                char c1 = image.charAt(i + 1);
                char c2 = image.charAt(i + 2);
                if ((c1 == '#' || c1 == '$') && c2 == '{') {
                    c = c1;
                    i++;
                }
            }
            buf.append(c);
            i++;
        }
        this.image = buf.toString();
    }
}
