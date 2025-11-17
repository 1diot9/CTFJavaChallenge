package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ApplicationParameter.class */
public class ApplicationParameter implements Serializable {
    private static final long serialVersionUID = 1;
    private String description = null;
    private String name = null;
    private boolean override = true;
    private String value = null;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        StringBuilder sb = new StringBuilder("ApplicationParameter[");
        sb.append("name=");
        sb.append(this.name);
        if (this.description != null) {
            sb.append(", description=");
            sb.append(this.description);
        }
        sb.append(", value=");
        sb.append(this.value);
        sb.append(", override=");
        sb.append(this.override);
        sb.append(']');
        return sb.toString();
    }
}
