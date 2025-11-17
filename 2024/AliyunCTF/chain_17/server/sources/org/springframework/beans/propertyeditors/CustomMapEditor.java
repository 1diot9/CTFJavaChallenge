package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CustomMapEditor.class */
public class CustomMapEditor extends PropertyEditorSupport {
    private final Class<? extends Map> mapType;
    private final boolean nullAsEmptyMap;

    public CustomMapEditor(Class<? extends Map> mapType) {
        this(mapType, false);
    }

    public CustomMapEditor(Class<? extends Map> mapType, boolean nullAsEmptyMap) {
        Assert.notNull(mapType, "Map type is required");
        if (!Map.class.isAssignableFrom(mapType)) {
            throw new IllegalArgumentException("Map type [" + mapType.getName() + "] does not implement [java.util.Map]");
        }
        this.mapType = mapType;
        this.nullAsEmptyMap = nullAsEmptyMap;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text);
    }

    public void setValue(@Nullable Object value) {
        if (value == null && this.nullAsEmptyMap) {
            super.setValue(createMap(this.mapType, 0));
            return;
        }
        if (value == null || (this.mapType.isInstance(value) && !alwaysCreateNewMap())) {
            super.setValue(value);
        } else {
            if (value instanceof Map) {
                Map<?, ?> source = (Map) value;
                Map<Object, Object> target = createMap(this.mapType, source.size());
                source.forEach((key, val) -> {
                    target.put(convertKey(key), convertValue(val));
                });
                super.setValue(target);
                return;
            }
            throw new IllegalArgumentException("Value cannot be converted to Map: " + value);
        }
    }

    protected Map<Object, Object> createMap(Class<? extends Map> mapType, int initialCapacity) {
        if (!mapType.isInterface()) {
            try {
                return (Map) ReflectionUtils.accessibleConstructor(mapType, new Class[0]).newInstance(new Object[0]);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate map class: " + mapType.getName(), ex);
            }
        }
        if (SortedMap.class == mapType) {
            return new TreeMap();
        }
        return new LinkedHashMap(initialCapacity);
    }

    protected boolean alwaysCreateNewMap() {
        return false;
    }

    protected Object convertKey(Object key) {
        return key;
    }

    protected Object convertValue(Object value) {
        return value;
    }

    @Nullable
    public String getAsText() {
        return null;
    }
}
