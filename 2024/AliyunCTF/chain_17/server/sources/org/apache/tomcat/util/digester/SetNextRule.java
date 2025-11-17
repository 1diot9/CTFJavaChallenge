package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/SetNextRule.class */
public class SetNextRule extends Rule {
    protected String methodName;
    protected String paramType;
    protected boolean useExactMatch = false;

    public SetNextRule(String methodName, String paramType) {
        this.methodName = null;
        this.paramType = null;
        this.methodName = methodName;
        this.paramType = paramType;
    }

    public boolean isExactMatch() {
        return this.useExactMatch;
    }

    public void setExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (parent == null) {
                this.digester.log.debug("[SetNextRule]{" + this.digester.match + "} Call [NULL PARENT]." + this.methodName + "(" + child + ")");
            } else {
                this.digester.log.debug("[SetNextRule]{" + this.digester.match + "} Call " + parent.getClass().getName() + "." + this.methodName + "(" + child + ")");
            }
        }
        IntrospectionUtils.callMethod1(parent, this.methodName, child, this.paramType, this.digester.getClassLoader());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(parent)).append('.');
            code.append(this.methodName).append('(').append(this.digester.toVariableName(child)).append(");");
            code.append(System.lineSeparator());
        }
    }

    public String toString() {
        return "SetNextRule[methodName=" + this.methodName + ", paramType=" + this.paramType + ']';
    }
}
