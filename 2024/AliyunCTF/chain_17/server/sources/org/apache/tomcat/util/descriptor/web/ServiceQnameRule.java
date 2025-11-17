package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ServiceQnameRule.class */
final class ServiceQnameRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String text) throws Exception {
        String namespaceuri = null;
        String localpart = text;
        int colon = text.indexOf(58);
        if (colon >= 0) {
            String prefix = text.substring(0, colon);
            namespaceuri = this.digester.findNamespaceURI(prefix);
            localpart = text.substring(colon + 1);
        }
        ContextService contextService = (ContextService) this.digester.peek();
        contextService.setServiceqnameLocalpart(localpart);
        contextService.setServiceqnameNamespaceURI(namespaceuri);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(contextService)).append(".setServiceqnameLocalpart(\"");
            code.append(localpart).append("\");");
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(contextService)).append(".setServiceqnameNamespaceURI(\"");
            code.append(namespaceuri).append("\");");
            code.append(System.lineSeparator());
        }
    }
}
