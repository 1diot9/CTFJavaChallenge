package org.apache.el.parser;

import jakarta.el.ELException;
import java.util.Collection;
import java.util.Map;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstEmpty.class */
public final class AstEmpty extends SimpleNode {
    public AstEmpty(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Boolean.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        if (obj == null) {
            return Boolean.TRUE;
        }
        if (obj instanceof String) {
            return Boolean.valueOf(((String) obj).length() == 0);
        }
        if (obj instanceof Object[]) {
            return Boolean.valueOf(((Object[]) obj).length == 0);
        }
        if (obj instanceof Collection) {
            return Boolean.valueOf(((Collection) obj).isEmpty());
        }
        if (obj instanceof Map) {
            return Boolean.valueOf(((Map) obj).isEmpty());
        }
        return Boolean.FALSE;
    }
}
