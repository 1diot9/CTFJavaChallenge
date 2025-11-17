package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/VersionRule.class */
final class VersionRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        WebXml webXml = (WebXml) this.digester.peek(this.digester.getCount() - 1);
        webXml.setVersion(attributes.getValue("version"));
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug(webXml.getClass().getName() + ".setVersion( " + webXml.getVersion() + ")");
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".setVersion(\"");
            code.append(attributes.getValue("version")).append("\");");
            code.append(System.lineSeparator());
        }
    }
}
