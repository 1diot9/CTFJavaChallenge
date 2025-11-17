package org.apache.tomcat.util.descriptor.web;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextEjb.class */
public class ContextEjb extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String home = null;
    private String link = null;
    private String remote = null;

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

    public String getRemote() {
        return this.remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextEjb[");
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
        if (this.remote != null) {
            sb.append(", remote=");
            sb.append(this.remote);
        }
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * result) + (this.home == null ? 0 : this.home.hashCode()))) + (this.link == null ? 0 : this.link.hashCode()))) + (this.remote == null ? 0 : this.remote.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextEjb other = (ContextEjb) obj;
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
        if (this.remote == null) {
            if (other.remote != null) {
                return false;
            }
            return true;
        }
        if (!this.remote.equals(other.remote)) {
            return false;
        }
        return true;
    }
}
