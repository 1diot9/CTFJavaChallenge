package org.apache.catalina.realm;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: MemoryRuleSet.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/MemoryUserRule.class */
final class MemoryUserRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String username = attributes.getValue("username");
        if (username == null) {
            username = attributes.getValue("name");
        }
        String password = attributes.getValue("password");
        String roles = attributes.getValue("roles");
        MemoryRealm realm = (MemoryRealm) this.digester.peek(this.digester.getCount() - 1);
        realm.addUser(username, password, roles);
    }
}
