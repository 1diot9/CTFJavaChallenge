package org.h2.security.auth.impl;

import ch.qos.logback.core.testUtil.CoreTestConstants;
import java.util.Hashtable;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.h2.api.CredentialsValidator;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/LdapCredentialsValidator.class */
public class LdapCredentialsValidator implements CredentialsValidator {
    private String bindDnPattern;
    private String host;
    private int port;
    private boolean secure;
    private String url;

    @Override // org.h2.security.auth.Configurable
    public void configure(ConfigProperties configProperties) {
        this.bindDnPattern = configProperties.getStringValue("bindDnPattern");
        this.host = configProperties.getStringValue("host");
        this.secure = configProperties.getBooleanValue("secure", true);
        this.port = configProperties.getIntValue("port", this.secure ? 636 : 389);
        this.url = "ldap" + (this.secure ? "s" : "") + "://" + this.host + ":" + this.port;
    }

    @Override // org.h2.api.CredentialsValidator
    public boolean validateCredentials(AuthenticationInfo authenticationInfo) throws Exception {
        DirContext dirContext = null;
        try {
            String replace = this.bindDnPattern.replace("%u", authenticationInfo.getUserName());
            Hashtable hashtable = new Hashtable();
            hashtable.put(CoreTestConstants.JAVA_NAMING_FACTORY_INITIAL, "com.sun.jndi.ldap.LdapCtxFactory");
            hashtable.put("java.naming.provider.url", this.url);
            hashtable.put("java.naming.security.authentication", "simple");
            hashtable.put("java.naming.security.principal", replace);
            hashtable.put("java.naming.security.credentials", authenticationInfo.getPassword());
            if (this.secure) {
                hashtable.put("java.naming.security.protocol", "ssl");
            }
            dirContext = new InitialDirContext(hashtable);
            authenticationInfo.setNestedIdentity(replace);
            if (dirContext != null) {
                dirContext.close();
            }
            return true;
        } catch (Throwable th) {
            if (dirContext != null) {
                dirContext.close();
            }
            throw th;
        }
    }
}
