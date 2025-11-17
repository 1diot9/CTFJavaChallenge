package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/CallParamRule.class */
public class CallParamRule extends Rule {
    protected final String attributeName;
    protected final int paramIndex;
    protected final boolean fromStack;
    protected final int stackIndex;
    protected ArrayStack<String> bodyTextStack;

    public CallParamRule(int paramIndex) {
        this(paramIndex, null);
    }

    public CallParamRule(int paramIndex, String attributeName) {
        this(attributeName, paramIndex, 0, false);
    }

    private CallParamRule(String attributeName, int paramIndex, int stackIndex, boolean fromStack) {
        this.attributeName = attributeName;
        this.paramIndex = paramIndex;
        this.stackIndex = stackIndex;
        this.fromStack = fromStack;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Object param = null;
        if (this.attributeName != null) {
            param = attributes.getValue(this.attributeName);
        } else if (this.fromStack) {
            param = this.digester.peek(this.stackIndex);
            if (this.digester.log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("[CallParamRule]{");
                sb.append(this.digester.match);
                sb.append("} Save from stack; from stack?").append(this.fromStack);
                sb.append("; object=").append(param);
                this.digester.log.debug(sb.toString());
            }
        }
        if (param != null) {
            Object[] parameters = (Object[]) this.digester.peekParams();
            parameters[this.paramIndex] = param;
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String bodyText) throws Exception {
        if (this.attributeName == null && !this.fromStack) {
            if (this.bodyTextStack == null) {
                this.bodyTextStack = new ArrayStack<>();
            }
            this.bodyTextStack.push(bodyText.trim());
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[]) this.digester.peekParams();
            parameters[this.paramIndex] = this.bodyTextStack.pop();
        }
    }

    public String toString() {
        return "CallParamRule[paramIndex=" + this.paramIndex + ", attributeName=" + this.attributeName + ", from stack=" + this.fromStack + ']';
    }
}
