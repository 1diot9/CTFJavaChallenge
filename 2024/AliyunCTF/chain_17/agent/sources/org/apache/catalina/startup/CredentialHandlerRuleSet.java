package org.apache.catalina.startup;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.apache.catalina.Globals;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/CredentialHandlerRuleSet.class */
public class CredentialHandlerRuleSet implements RuleSet {
    private static final int MAX_NESTED_LEVELS = Integer.getInteger("org.apache.catalina.startup.CredentialHandlerRuleSet.MAX_NESTED_LEVELS", 3).intValue();
    protected final String prefix;

    public CredentialHandlerRuleSet() {
        this("");
    }

    public CredentialHandlerRuleSet(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        StringBuilder pattern = new StringBuilder(this.prefix);
        int i = 0;
        while (i < MAX_NESTED_LEVELS) {
            if (i > 0) {
                pattern.append('/');
            }
            pattern.append("CredentialHandler");
            addRuleInstances(digester, pattern.toString(), i == 0 ? "setCredentialHandler" : "addCredentialHandler");
            i++;
        }
    }

    private void addRuleInstances(Digester digester, String pattern, String methodName) {
        digester.addObjectCreate(pattern, null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(pattern);
        digester.addSetNext(pattern, methodName, Globals.CREDENTIAL_HANDLER);
    }
}
