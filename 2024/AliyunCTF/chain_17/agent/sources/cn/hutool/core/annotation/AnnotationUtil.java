package cn.hutool.core.annotation;

import cn.hutool.core.annotation.scanner.AnnotationScanner;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AnnotationUtil.class */
public class AnnotationUtil {
    static final Set<Class<? extends Annotation>> META_ANNOTATIONS = CollUtil.newHashSet(Target.class, Retention.class, Inherited.class, Documented.class, SuppressWarnings.class, Override.class, Deprecated.class);

    public static boolean isJdkMetaAnnotation(Class<? extends Annotation> annotationType) {
        return META_ANNOTATIONS.contains(annotationType);
    }

    public static boolean isNotJdkMateAnnotation(Class<? extends Annotation> annotationType) {
        return false == isJdkMetaAnnotation(annotationType);
    }

    public static CombinationAnnotationElement toCombination(AnnotatedElement annotationEle) {
        if (annotationEle instanceof CombinationAnnotationElement) {
            return (CombinationAnnotationElement) annotationEle;
        }
        return new CombinationAnnotationElement(annotationEle);
    }

    public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination) {
        return getAnnotations(annotationEle, isToCombination, (Predicate<Annotation>) null);
    }

    public static <T> T[] getCombinationAnnotations(AnnotatedElement annotatedElement, Class<T> cls) {
        return (T[]) getAnnotations(annotatedElement, true, (Class) cls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T[] getAnnotations(AnnotatedElement annotatedElement, boolean z, Class<T> cls) {
        Annotation[] annotations = getAnnotations(annotatedElement, z, (Predicate<Annotation>) annotation -> {
            return null == cls || cls.isAssignableFrom(annotation.getClass());
        });
        T[] tArr = (T[]) ArrayUtil.newArray(cls, annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            tArr[i] = annotations[i];
        }
        return tArr;
    }

    public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination, Predicate<Annotation> predicate) {
        if (null == annotationEle) {
            return null;
        }
        if (isToCombination) {
            if (null == predicate) {
                return toCombination(annotationEle).getAnnotations();
            }
            return CombinationAnnotationElement.of(annotationEle, predicate).getAnnotations();
        }
        Annotation[] result = annotationEle.getAnnotations();
        if (null == predicate) {
            return result;
        }
        predicate.getClass();
        return (Annotation[]) ArrayUtil.filter(result, (v1) -> {
            return r1.test(v1);
        });
    }

    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        if (null == annotatedElement) {
            return null;
        }
        return (A) toCombination(annotatedElement).getAnnotation(cls);
    }

    public static boolean hasAnnotation(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) {
        return null != getAnnotation(annotationEle, annotationType);
    }

    public static <T> T getAnnotationValue(AnnotatedElement annotatedElement, Class<? extends Annotation> cls) throws UtilException {
        return (T) getAnnotationValue(annotatedElement, cls, "value");
    }

    public static <T> T getAnnotationValue(AnnotatedElement annotatedElement, Class<? extends Annotation> cls, String str) throws UtilException {
        Method methodOfObj;
        Annotation annotation = getAnnotation(annotatedElement, cls);
        if (null == annotation || null == (methodOfObj = ReflectUtil.getMethodOfObj(annotation, str, new Object[0]))) {
            return null;
        }
        return (T) ReflectUtil.invoke(annotation, methodOfObj, new Object[0]);
    }

    public static <A extends Annotation, R> R getAnnotationValue(AnnotatedElement annotatedElement, Func1<A, R> func1) {
        if (func1 == null) {
            return null;
        }
        SerializedLambda resolve = LambdaUtil.resolve(func1);
        String instantiatedMethodType = resolve.getInstantiatedMethodType();
        return (R) getAnnotationValue(annotatedElement, ClassUtil.loadClass(StrUtil.sub(instantiatedMethodType, 2, StrUtil.indexOf(instantiatedMethodType, ';'))), resolve.getImplMethodName());
    }

    public static Map<String, Object> getAnnotationValueMap(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) throws UtilException {
        Annotation annotation = getAnnotation(annotationEle, annotationType);
        if (null == annotation) {
            return null;
        }
        Method[] methods = ReflectUtil.getMethods(annotationType, t -> {
            if (ArrayUtil.isEmpty((Object[]) t.getParameterTypes())) {
                String name = t.getName();
                return false == IdentityNamingStrategy.HASH_CODE_KEY.equals(name) && false == "toString".equals(name) && false == "annotationType".equals(name);
            }
            return false;
        });
        HashMap<String, Object> result = new HashMap<>(methods.length, 1.0f);
        for (Method method : methods) {
            result.put(method.getName(), ReflectUtil.invoke(annotation, method, new Object[0]));
        }
        return result;
    }

    public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
        Retention retention = (Retention) annotationType.getAnnotation(Retention.class);
        if (null == retention) {
            return RetentionPolicy.CLASS;
        }
        return retention.value();
    }

    public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
        Target target = (Target) annotationType.getAnnotation(Target.class);
        if (null == target) {
            return new ElementType[]{ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE};
        }
        return target.value();
    }

    public static boolean isDocumented(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Documented.class);
    }

    public static boolean isInherited(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Inherited.class);
    }

    public static List<Annotation> scanMetaAnnotation(Class<? extends Annotation> annotationType) {
        return AnnotationScanner.DIRECTLY_AND_META_ANNOTATION.getAnnotationsIfSupport(annotationType);
    }

    public static List<Annotation> scanClass(Class<?> targetClass) {
        return AnnotationScanner.TYPE_HIERARCHY.getAnnotationsIfSupport(targetClass);
    }

    public static List<Annotation> scanMethod(Method method) {
        return AnnotationScanner.TYPE_HIERARCHY.getAnnotationsIfSupport(method);
    }

    public static void setValue(Annotation annotation, String annotationField, Object value) {
        Map memberValues = (Map) ReflectUtil.getFieldValue(Proxy.getInvocationHandler(annotation), "memberValues");
        memberValues.put(annotationField, value);
    }

    public static boolean isSynthesizedAnnotation(Annotation annotation) {
        return SynthesizedAnnotationProxy.isProxyAnnotation(annotation.getClass());
    }

    public static <T extends Annotation> T getAnnotationAlias(AnnotatedElement annotatedElement, Class<T> cls) {
        return (T) aggregatingFromAnnotation(getAnnotation(annotatedElement, cls)).synthesize(cls);
    }

    public static <T extends Annotation> T getSynthesizedAnnotation(Class<T> annotationType, Annotation... annotations) {
        return (T) Opt.ofNullable(annotations).filter((v0) -> {
            return ArrayUtil.isNotEmpty(v0);
        }).map(AnnotationUtil::aggregatingFromAnnotationWithMeta).map(a -> {
            return a.synthesize(annotationType);
        }).get();
    }

    public static <T extends Annotation> T getSynthesizedAnnotation(AnnotatedElement annotatedElement, Class<T> cls) {
        T t = (T) annotatedElement.getAnnotation(cls);
        if (ObjectUtil.isNotNull(t)) {
            return t;
        }
        return (T) AnnotationScanner.DIRECTLY.getAnnotationsIfSupport(annotatedElement).stream().map(annotation -> {
            return getSynthesizedAnnotation(cls, annotation);
        }).filter((v0) -> {
            return Objects.nonNull(v0);
        }).findFirst().orElse(null);
    }

    public static <T extends Annotation> List<T> getAllSynthesizedAnnotations(AnnotatedElement annotatedEle, Class<T> annotationType) {
        return (List) AnnotationScanner.DIRECTLY.getAnnotationsIfSupport(annotatedEle).stream().map(annotation -> {
            return getSynthesizedAnnotation(annotationType, annotation);
        }).filter((v0) -> {
            return Objects.nonNull(v0);
        }).collect(Collectors.toList());
    }

    public static SynthesizedAggregateAnnotation aggregatingFromAnnotation(Annotation... annotations) {
        return new GenericSynthesizedAggregateAnnotation(Arrays.asList(annotations), AnnotationScanner.NOTHING);
    }

    public static SynthesizedAggregateAnnotation aggregatingFromAnnotationWithMeta(Annotation... annotations) {
        return new GenericSynthesizedAggregateAnnotation(Arrays.asList(annotations), AnnotationScanner.DIRECTLY_AND_META_ANNOTATION);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE;
    }
}
