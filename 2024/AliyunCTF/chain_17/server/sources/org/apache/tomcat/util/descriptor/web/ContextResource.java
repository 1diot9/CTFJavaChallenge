package org.apache.tomcat.util.descriptor.web;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextResource.class */
public class ContextResource extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String auth = null;
    private String scope = "Shareable";
    private boolean singleton = true;
    private String closeMethod = null;
    private boolean closeMethodConfigured = false;

    public String getAuth() {
        return this.auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean getSingleton() {
        return this.singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getCloseMethod() {
        return this.closeMethod;
    }

    public void setCloseMethod(String closeMethod) {
        this.closeMethodConfigured = true;
        this.closeMethod = closeMethod;
    }

    public boolean getCloseMethodConfigured() {
        return this.closeMethodConfigured;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextResource[");
        sb.append("name=");
        sb.append(getName());
        if (getDescription() != null) {
            sb.append(", description=");
            sb.append(getDescription());
        }
        if (getType() != null) {
            sb.append(", type=");
            sb.append(getType());
        }
        if (this.auth != null) {
            sb.append(", auth=");
            sb.append(this.auth);
        }
        if (this.scope != null) {
            sb.append(", scope=");
            sb.append(this.scope);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * ((31 * result) + (this.auth == null ? 0 : this.auth.hashCode()))) + (this.closeMethod == null ? 0 : this.closeMethod.hashCode()))) + (this.scope == null ? 0 : this.scope.hashCode()))) + (this.singleton ? 1231 : 1237);
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextResource other = (ContextResource) obj;
        if (this.auth == null) {
            if (other.auth != null) {
                return false;
            }
        } else if (!this.auth.equals(other.auth)) {
            return false;
        }
        if (this.closeMethod == null) {
            if (other.closeMethod != null) {
                return false;
            }
        } else if (!this.closeMethod.equals(other.closeMethod)) {
            return false;
        }
        if (this.scope == null) {
            if (other.scope != null) {
                return false;
            }
        } else if (!this.scope.equals(other.scope)) {
            return false;
        }
        if (this.singleton != other.singleton) {
            return false;
        }
        return true;
    }
}
