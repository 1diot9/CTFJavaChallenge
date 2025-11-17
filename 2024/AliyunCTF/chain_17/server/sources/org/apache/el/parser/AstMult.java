package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.ELArithmetic;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstMult.class */
public final class AstMult extends ArithmeticNode {
    public AstMult(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Object obj1 = this.children[1].getValue(ctx);
        return ELArithmetic.multiply(obj0, obj1);
    }
}
