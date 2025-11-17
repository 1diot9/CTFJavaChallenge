package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstSemicolon.class */
public class AstSemicolon extends SimpleNode {
    public AstSemicolon(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        this.children[0].getValue(ctx);
        return this.children[1].getValue(ctx);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        this.children[0].getType(ctx);
        return this.children[1].getType(ctx);
    }
}
