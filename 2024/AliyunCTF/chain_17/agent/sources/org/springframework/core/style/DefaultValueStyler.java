package org.springframework.core.style;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/style/DefaultValueStyler.class */
public class DefaultValueStyler implements ValueStyler {
    private static final String EMPTY = "[[empty]]";
    private static final String NULL = "[null]";
    private static final String COLLECTION = "collection";
    private static final String SET = "set";
    private static final String LIST = "list";
    private static final String MAP = "map";
    private static final String EMPTY_MAP = "map[[empty]]";
    private static final String ARRAY = "array";

    @Override // org.springframework.core.style.ValueStyler
    public String style(@Nullable Object value) {
        if (value == null) {
            return styleNull();
        }
        if (value instanceof String) {
            String str = (String) value;
            return styleString(str);
        }
        if (value instanceof Class) {
            Class<?> clazz = (Class) value;
            return styleClass(clazz);
        }
        if (value instanceof Method) {
            Method method = (Method) value;
            return styleMethod(method);
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            return styleMap(map);
        }
        if (value instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) value;
            return styleMapEntry(entry);
        }
        if (value instanceof Collection) {
            Collection<?> collection = (Collection) value;
            return styleCollection(collection);
        }
        if (value.getClass().isArray()) {
            return styleArray(ObjectUtils.toObjectArray(value));
        }
        return styleObject(value);
    }

    protected String styleNull() {
        return NULL;
    }

    protected String styleString(String str) {
        return "'" + str + "'";
    }

    protected String styleClass(Class<?> clazz) {
        return ClassUtils.getShortName(clazz);
    }

    protected String styleMethod(Method method) {
        return method.getName() + "@" + ClassUtils.getShortName(method.getDeclaringClass());
    }

    protected <K, V> String styleMap(Map<K, V> map) {
        if (map.isEmpty()) {
            return EMPTY_MAP;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            stringJoiner.add(styleMapEntry(it.next()));
        }
        return "map" + stringJoiner;
    }

    protected String styleMapEntry(Map.Entry<?, ?> entry) {
        return style(entry.getKey()) + " -> " + style(entry.getValue());
    }

    protected String styleCollection(Collection<?> collection) {
        String collectionType = getCollectionTypeString(collection);
        if (collection.isEmpty()) {
            return collectionType + "[[empty]]";
        }
        StringJoiner result = new StringJoiner(", ", "[", "]");
        for (Object element : collection) {
            result.add(style(element));
        }
        return collectionType + result;
    }

    protected String styleArray(Object[] array) {
        if (array.length == 0) {
            return "array<" + ClassUtils.getShortName(array.getClass().componentType()) + ">[[empty]]";
        }
        StringJoiner result = new StringJoiner(", ", "[", "]");
        for (Object element : array) {
            result.add(style(element));
        }
        return "array<" + ClassUtils.getShortName(array.getClass().componentType()) + ">" + result;
    }

    protected String styleObject(Object obj) {
        return String.valueOf(obj);
    }

    private static String getCollectionTypeString(Collection<?> collection) {
        if (collection instanceof List) {
            return "list";
        }
        if (collection instanceof Set) {
            return "set";
        }
        return COLLECTION;
    }
}
