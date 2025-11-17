package org.apache.tomcat.util.descriptor.web;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextLocalEjb.class */
public class ContextLocalEjb extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String home = null;
    private String link = null;
    private String local = null;

    public String getHome() {
        return this.home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocal() {
        return this.local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextLocalEjb[");
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
        if (this.home != null) {
            sb.append(", home=");
            sb.append(this.home);
        }
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        if (this.local != null) {
            sb.append(", local=");
            sb.append(this.local);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * result) + (this.home == null ? 0 : this.home.hashCode()))) + (this.link == null ? 0 : this.link.hashCode()))) + (this.local == null ? 0 : this.local.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextLocalEjb other = (ContextLocalEjb) obj;
        if (this.home == null) {
            if (other.home != null) {
                return false;
            }
        } else if (!this.home.equals(other.home)) {
            return false;
        }
        if (this.link == null) {
            if (other.link != null) {
                return false;
            }
        } else if (!this.link.equals(other.link)) {
            return false;
        }
        if (this.local == null) {
            if (other.local != null) {
                return false;
            }
            return true;
        }
        if (!this.local.equals(other.local)) {
            return false;
        }
        return true;
    }
}
