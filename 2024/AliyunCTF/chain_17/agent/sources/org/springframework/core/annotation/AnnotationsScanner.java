package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationsScanner.class */
public abstract class AnnotationsScanner {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private static final Method[] NO_METHODS = new Method[0];
    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Method[]> baseTypeMethodsCache = new ConcurrentReferenceHashMap(256);

    private AnnotationsScanner() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public static <C, R> R scan(C c, AnnotatedElement annotatedElement, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> predicate, AnnotationsProcessor<C, R> annotationsProcessor) {
        return (R) annotationsProcessor.finish(process(c, annotatedElement, searchStrategy, predicate, annotationsProcessor));
    }

    @Nullable
    private static <C, R> R process(C c, AnnotatedElement annotatedElement, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> predicate, AnnotationsProcessor<C, R> annotationsProcessor) {
        if (annotatedElement instanceof Class) {
            return (R) processClass(c, (Class) annotatedElement, searchStrategy, predicate, annotationsProcessor);
        }
        if (annotatedElement instanceof Method) {
            return (R) processMethod(c, (Method) annotatedElement, searchStrategy, annotationsProcessor);
        }
        return (R) processElement(c, annotatedElement, annotationsProcessor);
    }

    @Nullable
    private static <C, R> R processClass(C c, Class<?> cls, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> predicate, AnnotationsProcessor<C, R> annotationsProcessor) {
        switch (searchStrategy) {
            case DIRECT:
                return (R) processElement(c, cls, annotationsProcessor);
            case INHERITED_ANNOTATIONS:
                return (R) processClassInheritedAnnotations(c, cls, searchStrategy, annotationsProcessor);
            case SUPERCLASS:
                return (R) processClassHierarchy(c, cls, annotationsProcessor, false, MergedAnnotations.Search.never);
            case TYPE_HIERARCHY:
                return (R) processClassHierarchy(c, cls, annotationsProcessor, true, predicate);
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    @Nullable
    private static <C, R> R processClassInheritedAnnotations(C c, Class<?> cls, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        try {
            if (isWithoutHierarchy(cls, searchStrategy, MergedAnnotations.Search.never)) {
                return (R) processElement(c, cls, annotationsProcessor);
            }
            Annotation[] annotationArr = null;
            int i = Integer.MAX_VALUE;
            int i2 = 0;
            while (cls != null && cls != Object.class && i > 0 && !hasPlainJavaAnnotationsOnly(cls)) {
                R doWithAggregate = annotationsProcessor.doWithAggregate(c, i2);
                if (doWithAggregate != null) {
                    return doWithAggregate;
                }
                Annotation[] declaredAnnotations = getDeclaredAnnotations(cls, true);
                if (annotationArr == null && declaredAnnotations.length > 0) {
                    annotationArr = cls.getAnnotations();
                    i = annotationArr.length;
                }
                for (int i3 = 0; i3 < declaredAnnotations.length; i3++) {
                    if (declaredAnnotations[i3] != null) {
                        boolean z = false;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= annotationArr.length) {
                                break;
                            }
                            if (annotationArr[i4] == null || declaredAnnotations[i3].annotationType() != annotationArr[i4].annotationType()) {
                                i4++;
                            } else {
                                z = true;
                                annotationArr[i4] = null;
                                i--;
                                break;
                            }
                        }
                        if (!z) {
                            declaredAnnotations[i3] = null;
                        }
                    }
                }
                R doWithAnnotations = annotationsProcessor.doWithAnnotations(c, i2, cls, declaredAnnotations);
                if (doWithAnnotations != null) {
                    return doWithAnnotations;
                }
                cls = cls.getSuperclass();
                i2++;
            }
            return null;
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(cls, th);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processClassHierarchy(C c, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, boolean z, Predicate<Class<?>> predicate) {
        return (R) processClassHierarchy(c, new int[]{0}, cls, annotationsProcessor, z, predicate);
    }

    @Nullable
    private static <C, R> R processClassHierarchy(C c, int[] iArr, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, boolean z, Predicate<Class<?>> predicate) {
        R r;
        try {
            R doWithAggregate = annotationsProcessor.doWithAggregate(c, iArr[0]);
            if (doWithAggregate != null) {
                return doWithAggregate;
            }
            if (hasPlainJavaAnnotationsOnly(cls)) {
                return null;
            }
            R doWithAnnotations = annotationsProcessor.doWithAnnotations(c, iArr[0], cls, getDeclaredAnnotations(cls, false));
            if (doWithAnnotations != null) {
                return doWithAnnotations;
            }
            iArr[0] = iArr[0] + 1;
            if (z) {
                for (Class<?> cls2 : cls.getInterfaces()) {
                    R r2 = (R) processClassHierarchy(c, iArr, cls2, annotationsProcessor, true, predicate);
                    if (r2 != null) {
                        return r2;
                    }
                }
            }
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass != Object.class && superclass != null && (r = (R) processClassHierarchy(c, iArr, superclass, annotationsProcessor, z, predicate)) != null) {
                return r;
            }
            if (predicate.test(cls)) {
                try {
                    Class<?> enclosingClass = cls.getEnclosingClass();
                    if (enclosingClass != null) {
                        R r3 = (R) processClassHierarchy(c, iArr, enclosingClass, annotationsProcessor, z, predicate);
                        if (r3 != null) {
                            return r3;
                        }
                    }
                } catch (Throwable th) {
                    AnnotationUtils.handleIntrospectionFailure(cls, th);
                }
            }
            return null;
        } catch (Throwable th2) {
            AnnotationUtils.handleIntrospectionFailure(cls, th2);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processMethod(C c, Method method, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> annotationsProcessor) {
        switch (searchStrategy) {
            case DIRECT:
            case INHERITED_ANNOTATIONS:
                return (R) processMethodInheritedAnnotations(c, method, annotationsProcessor);
            case SUPERCLASS:
                return (R) processMethodHierarchy(c, new int[]{0}, method.getDeclaringClass(), annotationsProcessor, method, false);
            case TYPE_HIERARCHY:
                return (R) processMethodHierarchy(c, new int[]{0}, method.getDeclaringClass(), annotationsProcessor, method, true);
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    @Nullable
    private static <C, R> R processMethodInheritedAnnotations(C c, Method method, AnnotationsProcessor<C, R> annotationsProcessor) {
        try {
            R doWithAggregate = annotationsProcessor.doWithAggregate(c, 0);
            return doWithAggregate != null ? doWithAggregate : (R) processMethodAnnotations(c, 0, method, annotationsProcessor);
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(method, th);
            return null;
        }
    }

    @Nullable
    private static <C, R> R processMethodHierarchy(C c, int[] iArr, Class<?> cls, AnnotationsProcessor<C, R> annotationsProcessor, Method method, boolean z) {
        try {
            R doWithAggregate = annotationsProcessor.doWithAggregate(c, iArr[0]);
            if (doWithAggregate != null) {
                return doWithAggregate;
            }
            if (hasPlainJavaAnnotationsOnly(cls)) {
                return null;
            }
            boolean z2 = false;
            if (cls == method.getDeclaringClass()) {
                R r = (R) processMethodAnnotations(c, iArr[0], method, annotationsProcessor);
                z2 = true;
                if (r != null) {
                    return r;
                }
            } else {
                for (Method method2 : getBaseTypeMethods(c, cls)) {
                    if (method2 != null && isOverride(method, method2)) {
                        R r2 = (R) processMethodAnnotations(c, iArr[0], method2, annotationsProcessor);
                        z2 = true;
                        if (r2 != null) {
                            return r2;
                        }
                    }
                }
            }
            if (Modifier.isPrivate(method.getModifiers())) {
                return null;
            }
            if (z2) {
                iArr[0] = iArr[0] + 1;
            }
            if (z) {
                for (Class<?> cls2 : cls.getInterfaces()) {
                    R r3 = (R) processMethodHierarchy(c, iArr, cls2, annotationsProcessor, method, true);
                    if (r3 != null) {
                        return r3;
                    }
                }
            }
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass != Object.class && superclass != null) {
                R r4 = (R) processMethodHierarchy(c, iArr, superclass, annotationsProcessor, method, z);
                if (r4 != null) {
                    return r4;
                }
                return null;
            }
            return null;
        } catch (Throwable th) {
            AnnotationUtils.handleIntrospectionFailure(method, th);
            return null;
        }
    }

    private static <C> Method[] getBaseTypeMethods(C context, Class<?> baseType) {
        if (baseType == Object.class || hasPlainJavaAnnotationsOnly(baseType)) {
            return NO_METHODS;
        }
        Method[] methods = baseTypeMethodsCache.get(baseType);
        if (methods == null) {
            methods = ReflectionUtils.getDeclaredMethods(baseType);
            int cleared = 0;
            for (int i = 0; i < methods.length; i++) {
                if (Modifier.isPrivate(methods[i].getModifiers()) || hasPlainJavaAnnotationsOnly(methods[i]) || getDeclaredAnnotations(methods[i], false).length == 0) {
                    methods[i] = null;
                    cleared++;
                }
            }
            if (cleared == methods.length) {
                methods = NO_METHODS;
            }
            baseTypeMethodsCache.put(baseType, methods);
        }
        return methods;
    }

    private static boolean isOverride(Method rootMethod, Method candidateMethod) {
        return !Modifier.isPrivate(candidateMethod.getModifiers()) && candidateMethod.getName().equals(rootMethod.getName()) && hasSameParameterTypes(rootMethod, candidateMethod);
    }

    private static boolean hasSameParameterTypes(Method rootMethod, Method candidateMethod) {
        if (candidateMethod.getParameterCount() != rootMethod.getParameterCount()) {
            return false;
        }
        Class<?>[] rootParameterTypes = rootMethod.getParameterTypes();
        Class<?>[] candidateParameterTypes = candidateMethod.getParameterTypes();
        if (Arrays.equals(candidateParameterTypes, rootParameterTypes)) {
            return true;
        }
        return hasSameGenericTypeParameters(rootMethod, candidateMethod, rootParameterTypes);
    }

    private static boolean hasSameGenericTypeParameters(Method rootMethod, Method candidateMethod, Class<?>[] rootParameterTypes) {
        Class<?> sourceDeclaringClass = rootMethod.getDeclaringClass();
        Class<?> candidateDeclaringClass = candidateMethod.getDeclaringClass();
        if (!candidateDeclaringClass.isAssignableFrom(sourceDeclaringClass)) {
            return false;
        }
        for (int i = 0; i < rootParameterTypes.length; i++) {
            Class<?> resolvedParameterType = ResolvableType.forMethodParameter(candidateMethod, i, sourceDeclaringClass).resolve();
            if (rootParameterTypes[i] != resolvedParameterType) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static <C, R> R processMethodAnnotations(C context, int aggregateIndex, Method source, AnnotationsProcessor<C, R> processor) {
        Annotation[] annotations = getDeclaredAnnotations(source, false);
        R result = processor.doWithAnnotations(context, aggregateIndex, source, annotations);
        if (result != null) {
            return result;
        }
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(source);
        if (bridgedMethod != source) {
            Annotation[] bridgedAnnotations = getDeclaredAnnotations(bridgedMethod, true);
            for (int i = 0; i < bridgedAnnotations.length; i++) {
                if (ObjectUtils.containsElement(annotations, bridgedAnnotations[i])) {
                    bridgedAnnotations[i] = null;
                }
            }
            return processor.doWithAnnotations(context, aggregateIndex, source, bridgedAnnotations);
        }
        return null;
    }

    @Nullable
    private static <C, R> R processElement(C context, AnnotatedElement source, AnnotationsProcessor<C, R> processor) {
        try {
            R result = processor.doWithAggregate(context, 0);
            return result != null ? result : processor.doWithAnnotations(context, 0, source, getDeclaredAnnotations(source, false));
        } catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(source, ex);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        for (Annotation annotation : getDeclaredAnnotations(annotatedElement, false)) {
            A a = (A) annotation;
            if (a != null && cls == a.annotationType()) {
                return a;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
        boolean cached = false;
        Annotation[] annotations = declaredAnnotationCache.get(source);
        if (annotations != null) {
            cached = true;
        } else {
            annotations = source.getDeclaredAnnotations();
            if (annotations.length != 0) {
                boolean allIgnored = true;
                for (int i = 0; i < annotations.length; i++) {
                    Annotation annotation = annotations[i];
                    if (isIgnorable(annotation.annotationType()) || !AttributeMethods.forAnnotationType(annotation.annotationType()).canLoad(annotation)) {
                        annotations[i] = null;
                    } else {
                        allIgnored = false;
                    }
                }
                annotations = allIgnored ? NO_ANNOTATIONS : annotations;
                if ((source instanceof Class) || (source instanceof Member)) {
                    declaredAnnotationCache.put(source, annotations);
                    cached = true;
                }
            }
        }
        if (!defensive || annotations.length == 0 || !cached) {
            return annotations;
        }
        return (Annotation[]) annotations.clone();
    }

    private static boolean isIgnorable(Class<?> annotationType) {
        return AnnotationFilter.PLAIN.matches(annotationType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isKnownEmpty(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> searchEnclosingClass) {
        if (hasPlainJavaAnnotationsOnly(source)) {
            return true;
        }
        if (searchStrategy == MergedAnnotations.SearchStrategy.DIRECT || isWithoutHierarchy(source, searchStrategy, searchEnclosingClass)) {
            if (source instanceof Method) {
                Method method = (Method) source;
                if (method.isBridge()) {
                    return false;
                }
            }
            return getDeclaredAnnotations(source, false).length == 0;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
        if (annotatedElement instanceof Class) {
            Class<?> clazz = (Class) annotatedElement;
            return hasPlainJavaAnnotationsOnly(clazz);
        }
        if (annotatedElement instanceof Member) {
            Member member = (Member) annotatedElement;
            return hasPlainJavaAnnotationsOnly(member.getDeclaringClass());
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
        return type.getName().startsWith("java.") || type == Ordered.class;
    }

    private static boolean isWithoutHierarchy(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy, Predicate<Class<?>> searchEnclosingClass) {
        if (source == Object.class) {
            return true;
        }
        if (source instanceof Class) {
            Class<?> sourceClass = (Class) source;
            boolean noSuperTypes = sourceClass.getSuperclass() == Object.class && sourceClass.getInterfaces().length == 0;
            return searchEnclosingClass.test(sourceClass) ? noSuperTypes && sourceClass.getEnclosingClass() == null : noSuperTypes;
        }
        if (!(source instanceof Method)) {
            return true;
        }
        Method sourceMethod = (Method) source;
        return Modifier.isPrivate(sourceMethod.getModifiers()) || isWithoutHierarchy(sourceMethod.getDeclaringClass(), searchStrategy, searchEnclosingClass);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCache() {
        declaredAnnotationCache.clear();
        baseTypeMethodsCache.clear();
    }
}
