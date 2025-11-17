package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstConcatenation.class */
public class AstConcatenation extends SimpleNode {
    public AstConcatenation(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        String s1 = ELSupport.coerceToString(ctx, this.children[0].getValue(ctx));
        String s2 = ELSupport.coerceToString(ctx, this.children[1].getValue(ctx));
        return s1 + s2;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }
}
