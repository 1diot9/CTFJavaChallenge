package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/SetOverrideRule.class */
final class SetOverrideRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        ContextEnvironment envEntry = (ContextEnvironment) this.digester.peek();
        envEntry.setOverride(false);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug(envEntry.getClass().getName() + ".setOverride(false)");
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(envEntry)).append(".setOverride(false);");
            code.append(System.lineSeparator());
        }
    }
}
