package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;

/* compiled from: WebRuleSet.java */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/MappedNameRule.class */
final class MappedNameRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String text) throws Exception {
        ResourceBase resourceBase = (ResourceBase) this.digester.peek();
        resourceBase.setProperty("mappedName", text.trim());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(resourceBase));
            code.append(".setProperty(\"mappedName\", \"").append(text.trim()).append("\");");
            code.append(System.lineSeparator());
        }
    }
}
