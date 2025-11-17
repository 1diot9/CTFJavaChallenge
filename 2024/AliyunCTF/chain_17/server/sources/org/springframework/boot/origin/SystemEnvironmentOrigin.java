package org.springframework.boot.origin;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/SystemEnvironmentOrigin.class */
public class SystemEnvironmentOrigin implements Origin {
    private final String property;

    public SystemEnvironmentOrigin(String property) {
        Assert.notNull(property, "Property name must not be null");
        Assert.hasText(property, "Property name must not be empty");
        this.property = property;
    }

    public String getProperty() {
        return this.property;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SystemEnvironmentOrigin other = (SystemEnvironmentOrigin) obj;
        return ObjectUtils.nullSafeEquals(this.property, other.property);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.property);
    }

    public String toString() {
        return "System Environment Property \"" + this.property + "\"";
    }
}
