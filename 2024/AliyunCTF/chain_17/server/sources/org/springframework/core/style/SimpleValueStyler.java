package org.springframework.core.style;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/style/SimpleValueStyler.class */
public class SimpleValueStyler extends DefaultValueStyler {
    public static final Function<Class<?>, String> DEFAULT_CLASS_STYLER = (v0) -> {
        return v0.getCanonicalName();
    };
    public static final Function<Method, String> DEFAULT_METHOD_STYLER = SimpleValueStyler::toSimpleMethodSignature;
    private final Function<Class<?>, String> classStyler;
    private final Function<Method, String> methodStyler;

    public SimpleValueStyler() {
        this(DEFAULT_CLASS_STYLER, DEFAULT_METHOD_STYLER);
    }

    public SimpleValueStyler(Function<Class<?>, String> classStyler, Function<Method, String> methodStyler) {
        this.classStyler = classStyler;
        this.methodStyler = methodStyler;
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleNull() {
        return "null";
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleString(String str) {
        return "\"" + str + "\"";
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleClass(Class<?> clazz) {
        return this.classStyler.apply(clazz);
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleMethod(Method method) {
        return this.methodStyler.apply(method);
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected <K, V> String styleMap(Map<K, V> map) {
        StringJoiner result = new StringJoiner(", ", "{", "}");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.add(style(entry));
        }
        return result.toString();
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleCollection(Collection<?> collection) {
        StringJoiner result = new StringJoiner(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]");
        for (Object element : collection) {
            result.add(style(element));
        }
        return result.toString();
    }

    @Override // org.springframework.core.style.DefaultValueStyler
    protected String styleArray(Object[] array) {
        StringJoiner result = new StringJoiner(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]");
        for (Object element : array) {
            result.add(style(element));
        }
        return result.toString();
    }

    private static String toSimpleMethodSignature(Method method) {
        String parameterList = (String) Arrays.stream(method.getParameterTypes()).map((v0) -> {
            return v0.getSimpleName();
        }).collect(Collectors.joining(", "));
        return String.format("%s(%s)", method.getName(), parameterList);
    }
}
