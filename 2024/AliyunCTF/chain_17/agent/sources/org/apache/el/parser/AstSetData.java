package org.apache.el.parser;

import jakarta.el.ELException;
import java.util.HashSet;
import java.util.Set;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstSetData.class */
public class AstSetData extends SimpleNode {
    public AstSetData(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Set<Object> result = new HashSet<>();
        if (this.children != null) {
            for (Node child : this.children) {
                result.add(child.getValue(ctx));
            }
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Set.class;
    }
}
