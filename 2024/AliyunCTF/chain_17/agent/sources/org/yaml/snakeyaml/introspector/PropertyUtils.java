package org.yaml.snakeyaml.introspector;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.PlatformFeatureDetector;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/introspector/PropertyUtils.class */
public class PropertyUtils {
    private final Map<Class<?>, Map<String, Property>> propertiesCache;
    private final Map<Class<?>, Set<Property>> readableProperties;
    private BeanAccess beanAccess;
    private boolean allowReadOnlyProperties;
    private boolean skipMissingProperties;
    private final PlatformFeatureDetector platformFeatureDetector;
    private static final String TRANSIENT = "transient";

    public PropertyUtils() {
        this(new PlatformFeatureDetector());
    }

    PropertyUtils(PlatformFeatureDetector platformFeatureDetector) {
        this.propertiesCache = new HashMap();
        this.readableProperties = new HashMap();
        this.beanAccess = BeanAccess.DEFAULT;
        this.allowReadOnlyProperties = false;
        this.skipMissingProperties = false;
        this.platformFeatureDetector = platformFeatureDetector;
        if (platformFeatureDetector.isRunningOnAndroid()) {
            this.beanAccess = BeanAccess.FIELD;
        }
    }

    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (this.propertiesCache.containsKey(type)) {
            return this.propertiesCache.get(type);
        }
        Map<String, Property> properties = new LinkedHashMap<>();
        boolean inaccessableFieldsExist = false;
        if (bAccess == BeanAccess.FIELD) {
            Class<?> cls = type;
            while (true) {
                Class<?> c = cls;
                if (c == null) {
                    break;
                }
                for (Field field : c.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && !properties.containsKey(field.getName())) {
                        properties.put(field.getName(), new FieldProperty(field));
                    }
                }
                cls = c.getSuperclass();
            }
        } else {
            try {
                for (PropertyDescriptor property : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                    Method readMethod = property.getReadMethod();
                    if ((readMethod == null || !readMethod.getName().equals("getClass")) && !isTransient(property)) {
                        properties.put(property.getName(), new MethodProperty(property));
                    }
                }
                Class<?> cls2 = type;
                while (true) {
                    Class<?> c2 = cls2;
                    if (c2 == null) {
                        break;
                    }
                    for (Field field2 : c2.getDeclaredFields()) {
                        int modifiers2 = field2.getModifiers();
                        if (!Modifier.isStatic(modifiers2) && !Modifier.isTransient(modifiers2)) {
                            if (Modifier.isPublic(modifiers2)) {
                                properties.put(field2.getName(), new FieldProperty(field2));
                            } else {
                                inaccessableFieldsExist = true;
                            }
                        }
                    }
                    cls2 = c2.getSuperclass();
                }
            } catch (IntrospectionException e) {
                throw new YAMLException((Throwable) e);
            }
        }
        if (properties.isEmpty() && inaccessableFieldsExist) {
            throw new YAMLException("No JavaBean properties found in " + type.getName());
        }
        this.propertiesCache.put(type, properties);
        return properties;
    }

    private boolean isTransient(FeatureDescriptor fd) {
        return Boolean.TRUE.equals(fd.getValue(TRANSIENT));
    }

    public Set<Property> getProperties(Class<? extends Object> type) {
        return getProperties(type, this.beanAccess);
    }

    public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) {
        if (this.readableProperties.containsKey(type)) {
            return this.readableProperties.get(type);
        }
        Set<Property> properties = createPropertySet(type, bAccess);
        this.readableProperties.put(type, properties);
        return properties;
    }

    protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
        Set<Property> properties = new TreeSet<>();
        Collection<Property> props = getPropertiesMap(type, bAccess).values();
        for (Property property : props) {
            if (property.isReadable() && (this.allowReadOnlyProperties || property.isWritable())) {
                properties.add(property);
            }
        }
        return properties;
    }

    public Property getProperty(Class<? extends Object> type, String name) {
        return getProperty(type, name, this.beanAccess);
    }

    public Property getProperty(Class<? extends Object> type, String name, BeanAccess bAccess) {
        Map<String, Property> properties = getPropertiesMap(type, bAccess);
        Property property = properties.get(name);
        if (property == null && this.skipMissingProperties) {
            property = new MissingProperty(name);
        }
        if (property == null) {
            throw new YAMLException("Unable to find property '" + name + "' on class: " + type.getName());
        }
        return property;
    }

    public void setBeanAccess(BeanAccess beanAccess) {
        if (this.platformFeatureDetector.isRunningOnAndroid() && beanAccess != BeanAccess.FIELD) {
            throw new IllegalArgumentException("JVM is Android - only BeanAccess.FIELD is available");
        }
        if (this.beanAccess != beanAccess) {
            this.beanAccess = beanAccess;
            this.propertiesCache.clear();
            this.readableProperties.clear();
        }
    }

    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        if (this.allowReadOnlyProperties != allowReadOnlyProperties) {
            this.allowReadOnlyProperties = allowReadOnlyProperties;
            this.readableProperties.clear();
        }
    }

    public boolean isAllowReadOnlyProperties() {
        return this.allowReadOnlyProperties;
    }

    public void setSkipMissingProperties(boolean skipMissingProperties) {
        if (this.skipMissingProperties != skipMissingProperties) {
            this.skipMissingProperties = skipMissingProperties;
            this.readableProperties.clear();
        }
    }

    public boolean isSkipMissingProperties() {
        return this.skipMissingProperties;
    }
}
