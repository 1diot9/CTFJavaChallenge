package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstOr.class */
public final class AstOr extends BooleanNode {
    public AstOr(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        Boolean b = ELSupport.coerceToBoolean(ctx, obj, true);
        if (b.booleanValue()) {
            return b;
        }
        Object obj2 = this.children[1].getValue(ctx);
        return ELSupport.coerceToBoolean(ctx, obj2, true);
    }
}
