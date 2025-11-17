package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import org.apache.tomcat.util.buf.UDecoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/LoginConfig.class */
public class LoginConfig extends XmlEncodingBase implements Serializable {
    private static final long serialVersionUID = 2;
    private String authMethod = null;
    private String errorPage = null;
    private String loginPage = null;
    private String realmName = null;

    public LoginConfig() {
    }

    public LoginConfig(String authMethod, String realmName, String loginPage, String errorPage) {
        setAuthMethod(authMethod);
        setRealmName(realmName);
        setLoginPage(loginPage);
        setErrorPage(errorPage);
    }

    public String getAuthMethod() {
        return this.authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getErrorPage() {
        return this.errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = UDecoder.URLDecode(errorPage, getCharset());
    }

    public String getLoginPage() {
        return this.loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = UDecoder.URLDecode(loginPage, getCharset());
    }

    public String getRealmName() {
        return this.realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("LoginConfig[");
        sb.append("authMethod=");
        sb.append(this.authMethod);
        if (this.realmName != null) {
            sb.append(", realmName=");
            sb.append(this.realmName);
        }
        if (this.loginPage != null) {
            sb.append(", loginPage=");
            sb.append(this.loginPage);
        }
        if (this.errorPage != null) {
            sb.append(", errorPage=");
            sb.append(this.errorPage);
        }
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        int result = (31 * 1) + (this.authMethod == null ? 0 : this.authMethod.hashCode());
        return (31 * ((31 * ((31 * result) + (this.errorPage == null ? 0 : this.errorPage.hashCode()))) + (this.loginPage == null ? 0 : this.loginPage.hashCode()))) + (this.realmName == null ? 0 : this.realmName.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LoginConfig)) {
            return false;
        }
        LoginConfig other = (LoginConfig) obj;
        if (this.authMethod == null) {
            if (other.authMethod != null) {
                return false;
            }
        } else if (!this.authMethod.equals(other.authMethod)) {
            return false;
        }
        if (this.errorPage == null) {
            if (other.errorPage != null) {
                return false;
            }
        } else if (!this.errorPage.equals(other.errorPage)) {
            return false;
        }
        if (this.loginPage == null) {
            if (other.loginPage != null) {
                return false;
            }
        } else if (!this.loginPage.equals(other.loginPage)) {
            return false;
        }
        if (this.realmName == null) {
            if (other.realmName != null) {
                return false;
            }
            return true;
        }
        if (!this.realmName.equals(other.realmName)) {
            return false;
        }
        return true;
    }
}
