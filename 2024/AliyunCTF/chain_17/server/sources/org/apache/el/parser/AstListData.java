package org.apache.el.parser;

import jakarta.el.ELException;
import java.util.ArrayList;
import java.util.List;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstListData.class */
public class AstListData extends SimpleNode {
    public AstListData(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        List<Object> result = new ArrayList<>();
        if (this.children != null) {
            for (Node child : this.children) {
                result.add(child.getValue(ctx));
            }
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return List.class;
    }
}
