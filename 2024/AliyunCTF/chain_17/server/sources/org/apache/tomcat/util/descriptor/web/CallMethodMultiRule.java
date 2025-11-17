package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.CallMethodRule;
import org.xml.sax.SAXException;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/CallMethodMultiRule.class */
final class CallMethodMultiRule extends CallMethodRule {
    final int multiParamIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallMethodMultiRule(String methodName, int paramCount, int multiParamIndex) {
        super(methodName, paramCount);
        this.multiParamIndex = multiParamIndex;
    }

    @Override // org.apache.tomcat.util.digester.CallMethodRule, org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object[] parameters;
        Object target;
        if (this.paramCount > 0) {
            parameters = (Object[]) this.digester.popParams();
        } else {
            parameters = new Object[0];
            super.end(namespace, name);
        }
        ArrayList<?> multiParams = (ArrayList) parameters[this.multiParamIndex];
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; i++) {
            if (i != this.multiParamIndex) {
                if (parameters[i] == null || ((parameters[i] instanceof String) && !String.class.isAssignableFrom(this.paramTypes[i]))) {
                    paramValues[i] = IntrospectionUtils.convert((String) parameters[i], this.paramTypes[i]);
                } else {
                    paramValues[i] = parameters[i];
                }
            }
        }
        if (this.targetOffset >= 0) {
            target = this.digester.peek(this.targetOffset);
        } else {
            target = this.digester.peek(this.digester.getCount() + this.targetOffset);
        }
        if (target == null) {
            throw new SAXException("[CallMethodRule]{} Call target is null (targetOffset=" + this.targetOffset + ",stackdepth=" + this.digester.getCount() + ')');
        }
        if (multiParams == null) {
            paramValues[this.multiParamIndex] = null;
            IntrospectionUtils.callMethodN(target, this.methodName, paramValues, this.paramTypes);
            return;
        }
        Iterator<?> it = multiParams.iterator();
        while (it.hasNext()) {
            Object param = it.next();
            if (param == null || ((param instanceof String) && !String.class.isAssignableFrom(this.paramTypes[this.multiParamIndex]))) {
                paramValues[this.multiParamIndex] = IntrospectionUtils.convert((String) param, this.paramTypes[this.multiParamIndex]);
            } else {
                paramValues[this.multiParamIndex] = param;
            }
            IntrospectionUtils.callMethodN(target, this.methodName, paramValues, this.paramTypes);
            StringBuilder code = this.digester.getGeneratedCode();
            if (code != null) {
                code.append(this.digester.toVariableName(target)).append('.').append(this.methodName);
                code.append('(');
                for (int i2 = 0; i2 < paramValues.length; i2++) {
                    if (i2 > 0) {
                        code.append(", ");
                    }
                    if (paramValues[i2] instanceof String) {
                        code.append("\"").append(paramValues[i2].toString()).append("\"");
                    } else {
                        code.append(this.digester.toVariableName(paramValues[i2]));
                    }
                }
                code.append(");");
                code.append(System.lineSeparator());
            }
        }
    }
}
