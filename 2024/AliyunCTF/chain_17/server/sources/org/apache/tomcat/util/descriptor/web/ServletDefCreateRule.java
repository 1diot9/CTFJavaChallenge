package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ServletDefCreateRule.class */
final class ServletDefCreateRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        ServletDef servletDef = new ServletDef();
        this.digester.push(servletDef);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("new " + servletDef.getClass().getName());
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(ServletDef.class.getName()).append(' ').append(this.digester.toVariableName(servletDef)).append(" = new ");
            code.append(ServletDef.class.getName()).append("();").append(System.lineSeparator());
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        ServletDef servletDef = (ServletDef) this.digester.pop();
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("pop " + servletDef.getClass().getName());
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
        }
    }
}
