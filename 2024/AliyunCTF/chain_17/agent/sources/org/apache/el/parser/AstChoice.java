package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstChoice.class */
public final class AstChoice extends SimpleNode {
    public AstChoice(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Object val = getValue(ctx);
        if (val != null) {
            return val.getClass();
        }
        return null;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Boolean b0 = ELSupport.coerceToBoolean(ctx, obj0, true);
        return this.children[b0.booleanValue() ? (char) 1 : (char) 2].getValue(ctx);
    }
}
