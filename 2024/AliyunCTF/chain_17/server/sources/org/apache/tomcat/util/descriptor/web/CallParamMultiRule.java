package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import org.apache.tomcat.util.digester.CallParamRule;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/CallParamMultiRule.class */
final class CallParamMultiRule extends CallParamRule {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CallParamMultiRule(int paramIndex) {
        super(paramIndex);
    }

    @Override // org.apache.tomcat.util.digester.CallParamRule, org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[]) this.digester.peekParams();
            ArrayList<String> params = (ArrayList) parameters[this.paramIndex];
            if (params == null) {
                params = new ArrayList<>();
                parameters[this.paramIndex] = params;
            }
            params.add(this.bodyTextStack.pop());
        }
    }
}
