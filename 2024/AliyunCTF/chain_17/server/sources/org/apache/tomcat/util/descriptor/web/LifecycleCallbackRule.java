package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.CallMethodRule;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/LifecycleCallbackRule.class */
final class LifecycleCallbackRule extends CallMethodRule {
    private final boolean postConstruct;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LifecycleCallbackRule(String methodName, int paramCount, boolean postConstruct) {
        super(methodName, paramCount);
        this.postConstruct = postConstruct;
    }

    @Override // org.apache.tomcat.util.digester.CallMethodRule, org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object[] params = (Object[]) this.digester.peekParams();
        if (params != null && params.length == 2) {
            WebXml webXml = (WebXml) this.digester.peek();
            if (this.postConstruct) {
                if (webXml.getPostConstructMethods().containsKey(params[0])) {
                    throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.postconstruct.duplicate", params[0]));
                }
            } else if (webXml.getPreDestroyMethods().containsKey(params[0])) {
                throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.predestroy.duplicate", params[0]));
            }
        }
        super.end(namespace, name);
    }
}
