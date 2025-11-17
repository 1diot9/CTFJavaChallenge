package org.apache.el.parser;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstLambdaParameters.class */
public class AstLambdaParameters extends SimpleNode {
    public AstLambdaParameters(int id) {
        super(id);
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
        result.append(")->");
        return result.toString();
    }
}
