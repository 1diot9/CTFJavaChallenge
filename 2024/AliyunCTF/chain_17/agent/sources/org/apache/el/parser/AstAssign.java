package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstAssign.class */
public class AstAssign extends SimpleNode {
    public AstAssign(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return value;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return this.children[1].getType(ctx);
    }
}
