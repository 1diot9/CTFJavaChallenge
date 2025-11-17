package org.apache.el.parser;

import java.util.ArrayList;
import java.util.List;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstMethodParameters.class */
public final class AstMethodParameters extends SimpleNode {
    public AstMethodParameters(int id) {
        super(id);
    }

    public Object[] getParameters(EvaluationContext ctx) {
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            params.add(jjtGetChild(i).getValue(ctx));
        }
        return params.toArray(new Object[0]);
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        if (this.children != null) {
            for (Node n : this.children) {
                result.append(n.toString());
                result.append(',');
            }
        }
        result.append(')');
        return result.toString();
    }
}
