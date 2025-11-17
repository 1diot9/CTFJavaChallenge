package org.springframework.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/ModelMap.class */
public class ModelMap extends LinkedHashMap<String, Object> {
    public ModelMap() {
    }

    public ModelMap(String attributeName, @Nullable Object attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    public ModelMap(Object attributeValue) {
        addAttribute(attributeValue);
    }

    public ModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        Assert.notNull(attributeName, "Model attribute name must not be null");
        put(attributeName, attributeValue);
        return this;
    }

    public ModelMap addAttribute(Object attributeValue) {
        Assert.notNull(attributeValue, "Model object must not be null");
        if (attributeValue instanceof Collection) {
            Collection<?> collection = (Collection) attributeValue;
            if (collection.isEmpty()) {
                return this;
            }
        }
        return addAttribute(Conventions.getVariableName(attributeValue), attributeValue);
    }

    public ModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        if (attributeValues != null) {
            for (Object attributeValue : attributeValues) {
                addAttribute(attributeValue);
            }
        }
        return this;
    }

    public ModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            putAll(attributes);
        }
        return this;
    }

    public ModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (!containsKey(key)) {
                    put(key, value);
                }
            });
        }
        return this;
    }

    public boolean containsAttribute(String attributeName) {
        return containsKey(attributeName);
    }

    @Nullable
    public Object getAttribute(String attributeName) {
        return get(attributeName);
    }
}
