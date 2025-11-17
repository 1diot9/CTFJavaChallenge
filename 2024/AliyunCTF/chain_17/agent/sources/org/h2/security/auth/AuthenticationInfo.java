package org.h2.security.auth;

import cn.hutool.core.text.StrPool;
import org.h2.engine.ConnectionInfo;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/AuthenticationInfo.class */
public class AuthenticationInfo {
    private ConnectionInfo connectionInfo;
    private String password;
    private String realm;
    Object nestedIdentity;

    public AuthenticationInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        this.realm = connectionInfo.getProperty("AUTHREALM", (String) null);
        if (this.realm != null) {
            this.realm = StringUtils.toUpperEnglish(this.realm);
        }
        this.password = connectionInfo.getProperty("AUTHZPWD", (String) null);
    }

    public String getUserName() {
        return this.connectionInfo.getUserName();
    }

    public String getRealm() {
        return this.realm;
    }

    public String getPassword() {
        return this.password;
    }

    public ConnectionInfo getConnectionInfo() {
        return this.connectionInfo;
    }

    public String getFullyQualifiedName() {
        if (this.realm == null) {
            return this.connectionInfo.getUserName();
        }
        return this.connectionInfo.getUserName() + StrPool.AT + this.realm;
    }

    public Object getNestedIdentity() {
        return this.nestedIdentity;
    }

    public void setNestedIdentity(Object obj) {
        this.nestedIdentity = obj;
    }

    public void clean() {
        this.password = null;
        this.nestedIdentity = null;
        this.connectionInfo.cleanAuthenticationInfo();
    }
}
