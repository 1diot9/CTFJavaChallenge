package org.h2.security.auth;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/PropertyConfig.class */
public class PropertyConfig {
    private String name;
    private String value;

    public PropertyConfig() {
    }

    public PropertyConfig(String str, String str2) {
        this.name = str;
        this.value = str2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String str) {
        this.value = str;
    }
}
