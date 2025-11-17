package org.apache.catalina.startup;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/SetNextNamingRule.class */
public class SetNextNamingRule extends Rule {
    protected final String methodName;
    protected final String paramType;

    public SetNextNamingRule(String methodName, String paramType) {
        this.methodName = methodName;
        this.paramType = paramType;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        NamingResourcesImpl namingResources;
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        boolean context = false;
        if (parent instanceof Context) {
            namingResources = ((Context) parent).getNamingResources();
            context = true;
        } else {
            namingResources = (NamingResourcesImpl) parent;
        }
        IntrospectionUtils.callMethod1(namingResources, this.methodName, child, this.paramType, this.digester.getClassLoader());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            if (context) {
                code.append(this.digester.toVariableName(parent)).append(".getNamingResources()");
            } else {
                code.append(this.digester.toVariableName(namingResources));
            }
            code.append('.').append(this.methodName).append('(');
            code.append(this.digester.toVariableName(child)).append(");");
            code.append(System.lineSeparator());
        }
    }

    public String toString() {
        return "SetNextRule[methodName=" + this.methodName + ", paramType=" + this.paramType + ']';
    }
}
