package cn.hutool.core.annotation;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AnnotationProxy.class */
public class AnnotationProxy<T extends Annotation> implements Annotation, InvocationHandler, Serializable {
    private static final long serialVersionUID = 1;
    private final T annotation;
    private final Class<T> type;
    private final Map<String, Object> attributes = initAttributes();

    public AnnotationProxy(T t) {
        this.annotation = t;
        this.type = (Class<T>) t.annotationType();
    }

    @Override // java.lang.annotation.Annotation
    public Class<? extends Annotation> annotationType() {
        return this.type;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Alias alias = (Alias) method.getAnnotation(Alias.class);
        if (null != alias) {
            String name = alias.value();
            if (StrUtil.isNotBlank(name)) {
                if (false == this.attributes.containsKey(name)) {
                    throw new IllegalArgumentException(StrUtil.format("No method for alias: [{}]", name));
                }
                return this.attributes.get(name);
            }
        }
        Object value = this.attributes.get(method.getName());
        if (value != null) {
            return value;
        }
        return method.invoke(this, args);
    }

    private Map<String, Object> initAttributes() {
        Method[] methods = ReflectUtil.getMethods(this.type);
        Map<String, Object> attributes = new HashMap<>(methods.length, 1.0f);
        for (Method method : methods) {
            if (!method.isSynthetic()) {
                attributes.put(method.getName(), ReflectUtil.invoke(this.annotation, method, new Object[0]));
            }
        }
        return attributes;
    }
}
