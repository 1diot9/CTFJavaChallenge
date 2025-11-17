package org.apache.catalina.realm;

import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/MemoryRuleSet.class */
public class MemoryRuleSet implements RuleSet {
    protected final String prefix;

    public MemoryRuleSet() {
        this("tomcat-users/");
    }

    public MemoryRuleSet(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addRule(this.prefix + "user", new MemoryUserRule());
    }
}
