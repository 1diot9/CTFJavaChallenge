package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/SetLoginConfig.class */
final class SetLoginConfig extends Rule {
    boolean isLoginConfigSet = false;

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isLoginConfigSet) {
            throw new IllegalArgumentException("<login-config> element is limited to 1 occurrence");
        }
        this.isLoginConfigSet = true;
    }
}
