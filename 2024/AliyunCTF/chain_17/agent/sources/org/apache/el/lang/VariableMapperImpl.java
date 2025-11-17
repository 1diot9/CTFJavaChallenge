package org.apache.el.lang;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/VariableMapperImpl.class */
public class VariableMapperImpl extends VariableMapper implements Externalizable {
    private static final long serialVersionUID = 1;
    private Map<String, ValueExpression> vars = new HashMap();

    @Override // jakarta.el.VariableMapper
    public ValueExpression resolveVariable(String variable) {
        return this.vars.get(variable);
    }

    @Override // jakarta.el.VariableMapper
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (expression == null) {
            return this.vars.remove(variable);
        }
        return this.vars.put(variable, expression);
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.vars = (Map) in.readObject();
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.vars);
    }
}
