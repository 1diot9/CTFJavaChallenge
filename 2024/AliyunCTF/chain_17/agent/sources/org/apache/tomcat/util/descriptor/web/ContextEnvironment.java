package org.apache.tomcat.util.descriptor.web;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextEnvironment.class */
public class ContextEnvironment extends ResourceBase {
    private static final long serialVersionUID = 1;
    private boolean override = true;
    private String value = null;

    public boolean getOverride() {
        return this.override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextEnvironment[");
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
        if (this.value != null) {
            sb.append(", value=");
            sb.append(this.value);
        }
        sb.append(", override=");
        sb.append(this.override);
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + (this.override ? 1231 : 1237))) + (this.value == null ? 0 : this.value.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextEnvironment other = (ContextEnvironment) obj;
        if (this.override != other.override) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
            return true;
        }
        if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
