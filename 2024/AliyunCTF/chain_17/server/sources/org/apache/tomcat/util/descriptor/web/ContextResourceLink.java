package org.apache.tomcat.util.descriptor.web;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextResourceLink.class */
public class ContextResourceLink extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String global = null;
    private String factory = null;

    public String getGlobal() {
        return this.global;
    }

    public void setGlobal(String global) {
        this.global = global;
    }

    public String getFactory() {
        return this.factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextResourceLink[");
        sb.append("name=");
        sb.append(getName());
        if (getType() != null) {
            sb.append(", type=");
            sb.append(getType());
        }
        if (getGlobal() != null) {
            sb.append(", global=");
            sb.append(getGlobal());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + (this.factory == null ? 0 : this.factory.hashCode()))) + (this.global == null ? 0 : this.global.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextResourceLink other = (ContextResourceLink) obj;
        if (this.factory == null) {
            if (other.factory != null) {
                return false;
            }
        } else if (!this.factory.equals(other.factory)) {
            return false;
        }
        if (this.global == null) {
            if (other.global != null) {
                return false;
            }
            return true;
        }
        if (!this.global.equals(other.global)) {
            return false;
        }
        return true;
    }
}
