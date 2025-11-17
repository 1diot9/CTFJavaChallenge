package org.apache.el.parser;

import jakarta.el.ELException;
import java.math.BigDecimal;
import org.apache.el.lang.EvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstFloatingPoint.class */
public final class AstFloatingPoint extends SimpleNode {
    private volatile Number number;

    public AstFloatingPoint(int id) {
        super(id);
    }

    public Number getFloatingPoint() {
        if (this.number == null) {
            try {
                Double d = Double.valueOf(this.image);
                if (d.isInfinite() || d.isNaN()) {
                    this.number = new BigDecimal(this.image);
                } else {
                    this.number = d;
                }
            } catch (NumberFormatException e) {
                throw new ELException(e);
            }
        }
        return this.number;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getFloatingPoint();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return getFloatingPoint().getClass();
    }
}
