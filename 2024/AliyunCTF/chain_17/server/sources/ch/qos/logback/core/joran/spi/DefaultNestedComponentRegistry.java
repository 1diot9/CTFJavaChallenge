package ch.qos.logback.core.joran.spi;

import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/DefaultNestedComponentRegistry.class */
public class DefaultNestedComponentRegistry {
    Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap();
    Map<String, Class<?>> tagToClassMap = new HashMap();

    public void duplicate(DefaultNestedComponentRegistry other) {
        this.defaultComponentMap.putAll(other.defaultComponentMap);
        this.tagToClassMap.putAll(other.tagToClassMap);
    }

    public void add(Class<?> hostClass, String propertyName, Class<?> componentClass) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
        this.defaultComponentMap.put(hpDouble, componentClass);
        this.tagToClassMap.put(propertyName, componentClass);
    }

    public String findDefaultComponentTypeByTag(String tagName) {
        Class<?> defaultClass = this.tagToClassMap.get(tagName);
        if (defaultClass == null) {
            return null;
        }
        return defaultClass.getCanonicalName();
    }

    public Class<?> findDefaultComponentType(Class<?> hostClass, String propertyName) {
        String propertyName2 = propertyName.toLowerCase();
        while (hostClass != null) {
            Class<?> componentClass = oneShotFind(hostClass, propertyName2);
            if (componentClass != null) {
                return componentClass;
            }
            hostClass = hostClass.getSuperclass();
        }
        return null;
    }

    private Class<?> oneShotFind(Class<?> hostClass, String propertyName) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
        return this.defaultComponentMap.get(hpDouble);
    }
}
