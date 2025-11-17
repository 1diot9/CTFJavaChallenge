package org.h2.security.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/ConfigProperties.class */
public class ConfigProperties {
    private HashMap<String, String> properties;

    public ConfigProperties() {
        this.properties = new HashMap<>();
    }

    public ConfigProperties(PropertyConfig... propertyConfigArr) {
        this(propertyConfigArr == null ? null : Arrays.asList(propertyConfigArr));
    }

    public ConfigProperties(Collection<PropertyConfig> collection) {
        this.properties = new HashMap<>();
        if (collection != null) {
            for (PropertyConfig propertyConfig : collection) {
                if (this.properties.putIfAbsent(propertyConfig.getName(), propertyConfig.getValue()) != null) {
                    throw new AuthConfigException("duplicate property " + propertyConfig.getName());
                }
            }
        }
    }

    public String getStringValue(String str, String str2) {
        String str3 = this.properties.get(str);
        if (str3 == null) {
            return str2;
        }
        return str3;
    }

    public String getStringValue(String str) {
        String str2 = this.properties.get(str);
        if (str2 == null) {
            throw new AuthConfigException("missing config property " + str);
        }
        return str2;
    }

    public int getIntValue(String str, int i) {
        String str2 = this.properties.get(str);
        if (str2 == null) {
            return i;
        }
        return Integer.parseInt(str2);
    }

    public int getIntValue(String str) {
        String str2 = this.properties.get(str);
        if (str2 == null) {
            throw new AuthConfigException("missing config property " + str);
        }
        return Integer.parseInt(str2);
    }

    public boolean getBooleanValue(String str, boolean z) {
        String str2 = this.properties.get(str);
        if (str2 == null) {
            return z;
        }
        return Utils.parseBoolean(str2, z, true);
    }
}
