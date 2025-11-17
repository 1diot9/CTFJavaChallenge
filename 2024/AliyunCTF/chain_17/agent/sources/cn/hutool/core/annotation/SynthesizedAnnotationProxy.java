package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationProxy.class */
public class SynthesizedAnnotationProxy implements InvocationHandler {
    private final AnnotationAttributeValueProvider annotationAttributeValueProvider;
    private final SynthesizedAnnotation annotation;
    private final Map<String, BiFunction<Method, Object[], Object>> methods;

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationProxy$SyntheticProxyAnnotation.class */
    interface SyntheticProxyAnnotation extends SynthesizedAnnotation {
        SynthesizedAnnotation getSynthesizedAnnotation();
    }

    public static <T extends Annotation> T create(Class<T> annotationType, AnnotationAttributeValueProvider annotationAttributeValueProvider, SynthesizedAnnotation annotation) {
        if (ObjectUtil.isNull(annotation)) {
            return null;
        }
        SynthesizedAnnotationProxy proxyHandler = new SynthesizedAnnotationProxy(annotationAttributeValueProvider, annotation);
        if (ObjectUtil.isNull(annotation)) {
            return null;
        }
        return (T) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType, SyntheticProxyAnnotation.class}, proxyHandler);
    }

    public static <T extends Annotation> T create(Class<T> cls, SynthesizedAnnotation synthesizedAnnotation) {
        return (T) create(cls, synthesizedAnnotation, synthesizedAnnotation);
    }

    public static boolean isProxyAnnotation(Class<?> annotationType) {
        return ClassUtil.isAssignable(SyntheticProxyAnnotation.class, annotationType);
    }

    SynthesizedAnnotationProxy(AnnotationAttributeValueProvider annotationAttributeValueProvider, SynthesizedAnnotation annotation) {
        Assert.notNull(annotationAttributeValueProvider, "annotationAttributeValueProvider must not null", new Object[0]);
        Assert.notNull(annotation, "annotation must not null", new Object[0]);
        this.annotationAttributeValueProvider = annotationAttributeValueProvider;
        this.annotation = annotation;
        this.methods = new HashMap(9);
        loadMethods();
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return Opt.ofNullable(this.methods.get(method.getName())).map(m -> {
            return m.apply(method, args);
        }).orElseGet(() -> {
            return ReflectUtil.invoke(this, method, args);
        });
    }

    void loadMethods() {
        this.methods.put("toString", (method, args) -> {
            return proxyToString();
        });
        this.methods.put(IdentityNamingStrategy.HASH_CODE_KEY, (method2, args2) -> {
            return Integer.valueOf(proxyHashCode());
        });
        this.methods.put("getSynthesizedAnnotation", (method3, args3) -> {
            return proxyGetSynthesizedAnnotation();
        });
        this.methods.put("getRoot", (method4, args4) -> {
            return this.annotation.getRoot();
        });
        this.methods.put("getVerticalDistance", (method5, args5) -> {
            return Integer.valueOf(this.annotation.getVerticalDistance());
        });
        this.methods.put("getHorizontalDistance", (method6, args6) -> {
            return Integer.valueOf(this.annotation.getHorizontalDistance());
        });
        this.methods.put("hasAttribute", (method7, args7) -> {
            return Boolean.valueOf(this.annotation.hasAttribute((String) args7[0], (Class) args7[1]));
        });
        this.methods.put("getAttributes", (method8, args8) -> {
            return this.annotation.getAttributes();
        });
        this.methods.put("setAttribute", (method9, args9) -> {
            throw new UnsupportedOperationException("proxied annotation can not reset attributes");
        });
        this.methods.put("getAttributeValue", (method10, args10) -> {
            return this.annotation.getAttributeValue((String) args10[0]);
        });
        this.methods.put("annotationType", (method11, args11) -> {
            return this.annotation.annotationType();
        });
        for (Method declaredMethod : ClassUtil.getDeclaredMethods(this.annotation.getAnnotation().annotationType())) {
            this.methods.put(declaredMethod.getName(), (method12, args12) -> {
                return proxyAttributeValue(method12);
            });
        }
    }

    private String proxyToString() {
        String attributes = (String) Stream.of((Object[]) ClassUtil.getDeclaredMethods(this.annotation.getAnnotation().annotationType())).filter(AnnotationUtil::isAttributeMethod).map(method -> {
            return CharSequenceUtil.format("{}={}", method.getName(), proxyAttributeValue(method));
        }).collect(Collectors.joining(", "));
        return CharSequenceUtil.format("@{}({})", this.annotation.annotationType().getName(), attributes);
    }

    private int proxyHashCode() {
        return Objects.hash(this.annotationAttributeValueProvider, this.annotation);
    }

    private Object proxyGetSynthesizedAnnotation() {
        return this.annotation;
    }

    private Object proxyAttributeValue(Method attributeMethod) {
        return this.annotationAttributeValueProvider.getAttributeValue(attributeMethod.getName(), attributeMethod.getReturnType());
    }
}
