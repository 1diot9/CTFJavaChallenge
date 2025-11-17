package org.apache.el.parser;

import jakarta.el.ELException;
import java.math.BigInteger;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstInteger.class */
public final class AstInteger extends SimpleNode {
    private volatile Number number;

    public AstInteger(int id) {
        super(id);
    }

    protected Number getInteger() {
        if (this.number == null) {
            try {
                try {
                    this.number = Long.valueOf(this.image);
                } catch (NumberFormatException e) {
                    this.number = new BigInteger(this.image);
                }
            } catch (ArithmeticException | NumberFormatException e2) {
                throw new ELException(e2);
            }
        }
        return this.number;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return getInteger().getClass();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getInteger();
    }
}
