package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstCompositeExpression.class */
public final class AstCompositeExpression extends SimpleNode {
    public AstCompositeExpression(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        StringBuilder sb = new StringBuilder(16);
        if (this.children != null) {
            for (Node child : this.children) {
                Object obj = child.getValue(ctx);
                if (obj != null) {
                    sb.append(ELSupport.coerceToString(ctx, obj));
                }
            }
        }
        return sb.toString();
    }
}
