package org.apache.tomcat.util.descriptor.web;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/MessageDestinationRef.class */
public class MessageDestinationRef extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String link = null;
    private String usage = null;

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUsage() {
        return this.usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MessageDestination[");
        sb.append("name=");
        sb.append(getName());
        if (this.link != null) {
            sb.append(", link=");
            sb.append(this.link);
        }
        if (getType() != null) {
            sb.append(", type=");
            sb.append(getType());
        }
        if (this.usage != null) {
            sb.append(", usage=");
            sb.append(this.usage);
        }
        if (getDescription() != null) {
            sb.append(", description=");
            sb.append(getDescription());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + (this.link == null ? 0 : this.link.hashCode()))) + (this.usage == null ? 0 : this.usage.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        MessageDestinationRef other = (MessageDestinationRef) obj;
        if (this.link == null) {
            if (other.link != null) {
                return false;
            }
        } else if (!this.link.equals(other.link)) {
            return false;
        }
        if (this.usage == null) {
            if (other.usage != null) {
                return false;
            }
            return true;
        }
        if (!this.usage.equals(other.usage)) {
            return false;
        }
        return true;
    }
}
