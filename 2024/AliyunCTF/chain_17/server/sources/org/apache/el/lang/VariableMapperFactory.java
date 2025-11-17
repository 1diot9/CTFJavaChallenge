package org.apache.el.lang;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import org.apache.el.util.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/VariableMapperFactory.class */
public class VariableMapperFactory extends VariableMapper {
    private final VariableMapper target;
    private VariableMapper momento;

    public VariableMapperFactory(VariableMapper target) {
        if (target == null) {
            throw new NullPointerException(MessageFactory.get("error.noVariableMapperTarget"));
        }
        this.target = target;
    }

    public VariableMapper create() {
        return this.momento;
    }

    @Override // jakarta.el.VariableMapper
    public ValueExpression resolveVariable(String variable) {
        ValueExpression expr = this.target.resolveVariable(variable);
        if (expr != null) {
            if (this.momento == null) {
                this.momento = new VariableMapperImpl();
            }
            this.momento.setVariable(variable, expr);
        }
        return expr;
    }

    @Override // jakarta.el.VariableMapper
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        throw new UnsupportedOperationException(MessageFactory.get("error.cannotSetVariables"));
    }
}
