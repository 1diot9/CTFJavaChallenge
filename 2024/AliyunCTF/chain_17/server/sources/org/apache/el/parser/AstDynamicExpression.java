package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstDynamicExpression.class */
public final class AstDynamicExpression extends SimpleNode {
    public AstDynamicExpression(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return this.children[0].getType(ctx);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.children[0].getValue(ctx);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        return this.children[0].isReadOnly(ctx);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        this.children[0].setValue(ctx, value);
    }
}
