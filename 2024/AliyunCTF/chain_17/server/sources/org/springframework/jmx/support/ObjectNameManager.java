package org.springframework.jmx.support;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/ObjectNameManager.class */
public final class ObjectNameManager {
    private ObjectNameManager() {
    }

    public static ObjectName getInstance(Object name) throws MalformedObjectNameException {
        if (name instanceof ObjectName) {
            ObjectName objectName = (ObjectName) name;
            return objectName;
        }
        if (!(name instanceof String)) {
            throw new MalformedObjectNameException("Invalid ObjectName value type [" + name.getClass().getName() + "]: only ObjectName and String supported.");
        }
        String text = (String) name;
        return getInstance(text);
    }

    public static ObjectName getInstance(String objectName) throws MalformedObjectNameException {
        return ObjectName.getInstance(objectName);
    }

    public static ObjectName getInstance(String domainName, String key, String value) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, key, value);
    }

    public static ObjectName getInstance(String domainName, Hashtable<String, String> properties) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, properties);
    }
}
