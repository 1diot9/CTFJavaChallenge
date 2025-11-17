package org.apache.el.parser;

import jakarta.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstTrue.class */
public final class AstTrue extends BooleanNode {
    public AstTrue(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return Boolean.TRUE;
    }
}
