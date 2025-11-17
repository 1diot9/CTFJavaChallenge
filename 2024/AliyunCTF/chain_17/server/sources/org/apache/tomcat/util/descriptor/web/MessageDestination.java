package org.apache.tomcat.util.descriptor.web;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/MessageDestination.class */
public class MessageDestination extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String displayName = null;
    private String largeIcon = null;
    private String smallIcon = null;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MessageDestination[");
        sb.append("name=");
        sb.append(getName());
        if (this.displayName != null) {
            sb.append(", displayName=");
            sb.append(this.displayName);
        }
        if (this.largeIcon != null) {
            sb.append(", largeIcon=");
            sb.append(this.largeIcon);
        }
        if (this.smallIcon != null) {
            sb.append(", smallIcon=");
            sb.append(this.smallIcon);
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
        return (31 * ((31 * ((31 * result) + (this.displayName == null ? 0 : this.displayName.hashCode()))) + (this.largeIcon == null ? 0 : this.largeIcon.hashCode()))) + (this.smallIcon == null ? 0 : this.smallIcon.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        MessageDestination other = (MessageDestination) obj;
        if (this.displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!this.displayName.equals(other.displayName)) {
            return false;
        }
        if (this.largeIcon == null) {
            if (other.largeIcon != null) {
                return false;
            }
        } else if (!this.largeIcon.equals(other.largeIcon)) {
            return false;
        }
        if (this.smallIcon == null) {
            if (other.smallIcon != null) {
                return false;
            }
            return true;
        }
        if (!this.smallIcon.equals(other.smallIcon)) {
            return false;
        }
        return true;
    }
}
