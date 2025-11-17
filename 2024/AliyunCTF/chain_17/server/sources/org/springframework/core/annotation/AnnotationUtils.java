package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationTypeMapping;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationUtils.class */
public abstract class AnnotationUtils {
    public static final String VALUE = "value";
    private static final AnnotationFilter JAVA_LANG_ANNOTATION_FILTER = AnnotationFilter.packages("java.lang.annotation");
    private static final Map<Class<? extends Annotation>, Map<String, DefaultValueHolder>> defaultValuesCache = new ConcurrentReferenceHashMap();

    public static boolean isCandidateClass(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes) {
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (isCandidateClass(clazz, annotationType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCandidateClass(Class<?> clazz, @Nullable Class<? extends Annotation> annotationType) {
        return annotationType != null && isCandidateClass(clazz, annotationType.getName());
    }

    public static boolean isCandidateClass(Class<?> clazz, String annotationName) {
        if (!annotationName.startsWith("java.") && AnnotationsScanner.hasPlainJavaAnnotationsOnly(clazz)) {
            return false;
        }
        return true;
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(Annotation annotation, Class<A> cls) {
        if (cls.isInstance(annotation)) {
            return (A) synthesizeAnnotation(annotation, cls);
        }
        if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotation)) {
            return null;
        }
        return MergedAnnotations.from(annotation, new Annotation[]{annotation}, RepeatableContainers.none()).get(cls).withNonMergedAttributes().synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> cls) {
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return (A) annotatedElement.getAnnotation(cls);
        }
        return MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none()).get(cls).withNonMergedAttributes().synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
    }

    private static <A extends Annotation> boolean isSingleLevelPresent(MergedAnnotation<A> mergedAnnotation) {
        int distance = mergedAnnotation.getDistance();
        return distance == 0 || distance == 1;
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> cls) {
        return (A) getAnnotation((AnnotatedElement) BridgeMethodResolver.findBridgedMethod(method), (Class) cls);
    }

    @Nullable
    @Deprecated
    public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        try {
            return synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    @Deprecated
    public static Annotation[] getAnnotations(Method method) {
        try {
            return synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
        } catch (Throwable ex) {
            handleIntrospectionFailure(method, ex);
            return null;
        }
    }

    @Deprecated
    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return getRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    @Deprecated
    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        RepeatableContainers standardRepeatables;
        if (containerAnnotationType != null) {
            standardRepeatables = RepeatableContainers.of(annotationType, containerAnnotationType);
        } else {
            standardRepeatables = RepeatableContainers.standardRepeatables();
        }
        RepeatableContainers repeatableContainers = standardRepeatables;
        return (Set) MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.SUPERCLASS, repeatableContainers).stream(annotationType).filter(MergedAnnotationPredicates.firstRunOf((v0) -> {
            return v0.getAggregateIndex();
        })).map((v0) -> {
            return v0.withNonMergedAttributes();
        }).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    @Deprecated
    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    @Deprecated
    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        RepeatableContainers standardRepeatables;
        if (containerAnnotationType != null) {
            standardRepeatables = RepeatableContainers.of(annotationType, containerAnnotationType);
        } else {
            standardRepeatables = RepeatableContainers.standardRepeatables();
        }
        RepeatableContainers repeatableContainers = standardRepeatables;
        return (Set) MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.DIRECT, repeatableContainers).stream(annotationType).map((v0) -> {
            return v0.withNonMergedAttributes();
        }).collect(MergedAnnotationCollectors.toAnnotationSet());
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, @Nullable Class<A> cls) {
        if (cls == null) {
            return null;
        }
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return (A) annotatedElement.getDeclaredAnnotation(cls);
        }
        return MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none()).get(cls).withNonMergedAttributes().synthesize((v0) -> {
            return v0.isPresent();
        }).orElse(null);
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> cls) {
        if (cls == null) {
            return null;
        }
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(method)) {
            return (A) method.getDeclaredAnnotation(cls);
        }
        return MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none()).get(cls).withNonMergedAttributes().synthesize((v0) -> {
            return v0.isPresent();
        }).orElse(null);
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Class<?> cls, @Nullable Class<A> cls2) {
        if (cls2 == null) {
            return null;
        }
        if (AnnotationFilter.PLAIN.matches((Class<?>) cls2) || AnnotationsScanner.hasPlainJavaAnnotationsOnly(cls)) {
            A a = (A) cls.getDeclaredAnnotation(cls2);
            if (a != null) {
                return a;
            }
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass == null || superclass == Object.class) {
                return null;
            }
            return (A) findAnnotation((Class<?>) superclass, (Class) cls2);
        }
        return MergedAnnotations.from(cls, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none()).get(cls2).withNonMergedAttributes().synthesize((v0) -> {
            return v0.isPresent();
        }).orElse(null);
    }

    @Nullable
    @Deprecated
    public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return (Class) MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.SUPERCLASS).get(annotationType, (v0) -> {
            return v0.isDirectlyPresent();
        }).getSource();
    }

    @Nullable
    @Deprecated
    public static Class<?> findAnnotationDeclaringClassForTypes(List<Class<? extends Annotation>> annotationTypes, @Nullable Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return (Class) MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.SUPERCLASS).stream().filter(MergedAnnotationPredicates.typeIn(annotationTypes).and((v0) -> {
            return v0.isDirectlyPresent();
        })).map((v0) -> {
            return v0.getSource();
        }).findFirst().orElse(null);
    }

    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
        return MergedAnnotations.from(clazz).get(annotationType).isDirectlyPresent();
    }

    @Deprecated
    public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
        return ((MergedAnnotation) MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS).stream(annotationType).filter((v0) -> {
            return v0.isDirectlyPresent();
        }).findFirst().orElseGet(MergedAnnotation::missing)).getAggregateIndex() > 0;
    }

    @Deprecated
    public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType, @Nullable Class<? extends Annotation> metaAnnotationType) {
        if (metaAnnotationType == null) {
            return false;
        }
        if (AnnotationFilter.PLAIN.matches(metaAnnotationType) || AnnotationsScanner.hasPlainJavaAnnotationsOnly((Class<?>) annotationType)) {
            return annotationType.isAnnotationPresent(metaAnnotationType);
        }
        return MergedAnnotations.from(annotationType, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none()).isPresent(metaAnnotationType);
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable Annotation annotation) {
        return annotation != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotation);
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable String annotationType) {
        return annotationType != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotationType);
    }

    public static void validateAnnotation(Annotation annotation) {
        AttributeMethods.forAnnotationType(annotation.annotationType()).validate(annotation);
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        return getAnnotationAttributes((AnnotatedElement) null, annotation);
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation, boolean classValuesAsString) {
        return getAnnotationAttributes(annotation, classValuesAsString, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        return getAnnotationAttributes(null, annotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation) {
        return getAnnotationAttributes(annotatedElement, annotation, false, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, nestedAnnotationsAsMap);
        return (AnnotationAttributes) MergedAnnotation.from(annotatedElement, annotation).withNonMergedAttributes().asMap(mergedAnnotation -> {
            return new AnnotationAttributes((Class<? extends Annotation>) mergedAnnotation.getType(), true);
        }, adaptations);
    }

    public static void registerDefaultValues(AnnotationAttributes attributes) {
        Class<? extends Annotation> annotationType = attributes.annotationType();
        if (annotationType != null && Modifier.isPublic(annotationType.getModifiers()) && !AnnotationFilter.PLAIN.matches(annotationType)) {
            Map<String, DefaultValueHolder> defaultValues = getDefaultValues(annotationType);
            Objects.requireNonNull(attributes);
            defaultValues.forEach((v1, v2) -> {
                r1.putIfAbsent(v1, v2);
            });
        }
    }

    private static Map<String, DefaultValueHolder> getDefaultValues(Class<? extends Annotation> annotationType) {
        return defaultValuesCache.computeIfAbsent(annotationType, AnnotationUtils::computeDefaultValues);
    }

    private static Map<String, DefaultValueHolder> computeDefaultValues(Class<? extends Annotation> annotationType) {
        AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
        if (!methods.hasDefaultValueMethod()) {
            return Collections.emptyMap();
        }
        Map<String, DefaultValueHolder> result = CollectionUtils.newLinkedHashMap(methods.size());
        if (!methods.hasNestedAnnotation()) {
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                Object defaultValue = method.getDefaultValue();
                if (defaultValue != null) {
                    result.put(method.getName(), new DefaultValueHolder(defaultValue));
                }
            }
        } else {
            AnnotationAttributes attributes = (AnnotationAttributes) MergedAnnotation.of(annotationType).asMap(annotation -> {
                return new AnnotationAttributes((Class<? extends Annotation>) annotation.getType(), true);
            }, MergedAnnotation.Adapt.ANNOTATION_TO_MAP);
            for (Map.Entry<String, Object> element : attributes.entrySet()) {
                result.put(element.getKey(), new DefaultValueHolder(element.getValue()));
            }
        }
        return result;
    }

    public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, @Nullable AnnotationAttributes attributes, boolean classValuesAsString) {
        if (attributes == null) {
            return;
        }
        if (!attributes.validated) {
            Class<? extends Annotation> annotationType = attributes.annotationType();
            if (annotationType == null) {
                return;
            }
            AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(annotationType).get(0);
            for (int i = 0; i < mapping.getMirrorSets().size(); i++) {
                AnnotationTypeMapping.MirrorSets.MirrorSet mirrorSet = mapping.getMirrorSets().get(i);
                int resolved = mirrorSet.resolve(attributes.displayName, attributes, AnnotationUtils::getAttributeValueForMirrorResolution);
                if (resolved != -1) {
                    Method attribute = mapping.getAttributes().get(resolved);
                    Object value = attributes.get(attribute.getName());
                    for (int j = 0; j < mirrorSet.size(); j++) {
                        Method mirror = mirrorSet.get(j);
                        if (mirror != attribute) {
                            attributes.put(mirror.getName(), adaptValue(annotatedElement, value, classValuesAsString));
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
            String attributeName = attributeEntry.getKey();
            Object value2 = attributeEntry.getValue();
            if (value2 instanceof DefaultValueHolder) {
                DefaultValueHolder defaultValueHolder = (DefaultValueHolder) value2;
                attributes.put(attributeName, adaptValue(annotatedElement, defaultValueHolder.defaultValue, classValuesAsString));
            }
        }
    }

    private static Object getAttributeValueForMirrorResolution(Method attribute, Object attributes) {
        Object result = ((AnnotationAttributes) attributes).get(attribute.getName());
        if (!(result instanceof DefaultValueHolder)) {
            return result;
        }
        DefaultValueHolder defaultValueHolder = (DefaultValueHolder) result;
        return defaultValueHolder.defaultValue;
    }

    @Nullable
    private static Object adaptValue(@Nullable Object annotatedElement, @Nullable Object value, boolean classValuesAsString) {
        if (classValuesAsString) {
            if (value instanceof Class) {
                Class<?> clazz = (Class) value;
                return clazz.getName();
            }
            if (value instanceof Class[]) {
                Class<?>[] classes = (Class[]) value;
                String[] names = new String[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    names[i] = classes[i].getName();
                }
                return names;
            }
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            return MergedAnnotation.from(annotatedElement, annotation).synthesize();
        }
        if (value instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[]) value;
            Annotation[] synthesized = (Annotation[]) Array.newInstance(annotations.getClass().componentType(), annotations.length);
            for (int i2 = 0; i2 < annotations.length; i2++) {
                synthesized[i2] = MergedAnnotation.from(annotatedElement, annotations[i2]).synthesize();
            }
            return synthesized;
        }
        return value;
    }

    @Nullable
    public static Object getValue(Annotation annotation) {
        return getValue(annotation, "value");
    }

    @Nullable
    public static Object getValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
            return invokeAnnotationMethod(method, annotation);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Throwable ex) {
            handleValueRetrievalFailure(annotation, ex);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Object invokeAnnotationMethod(Method method, @Nullable Object annotation) {
        if (annotation == null) {
            return null;
        }
        if (Proxy.isProxyClass(annotation.getClass())) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(annotation);
                return handler.invoke(annotation, method, null);
            } catch (Throwable th) {
            }
        }
        return ReflectionUtils.invokeMethod(method, annotation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void rethrowAnnotationConfigurationException(Throwable ex) {
        if (ex instanceof AnnotationConfigurationException) {
            AnnotationConfigurationException exception = (AnnotationConfigurationException) ex;
            throw exception;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void handleIntrospectionFailure(AnnotatedElement element, Throwable ex) {
        rethrowAnnotationConfigurationException(ex);
        IntrospectionFailureLogger logger = IntrospectionFailureLogger.INFO;
        boolean meta = false;
        if (element instanceof Class) {
            Class<?> clazz = (Class) element;
            if (Annotation.class.isAssignableFrom(clazz)) {
                logger = IntrospectionFailureLogger.DEBUG;
                meta = true;
            }
        }
        if (logger.isEnabled()) {
            logger.log("Failed to " + (meta ? "meta-introspect annotation " : "introspect annotations on ") + element + ": " + ex);
        }
    }

    private static void handleValueRetrievalFailure(Annotation annotation, Throwable ex) {
        rethrowAnnotationConfigurationException(ex);
        IntrospectionFailureLogger logger = IntrospectionFailureLogger.INFO;
        if (logger.isEnabled()) {
            logger.log("Failed to retrieve value from " + annotation + ": " + ex);
        }
    }

    @Nullable
    public static Object getDefaultValue(Annotation annotation) {
        return getDefaultValue(annotation, "value");
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation != null) {
            return getDefaultValue(annotation.annotationType(), attributeName);
        }
        return null;
    }

    @Nullable
    public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
        return getDefaultValue(annotationType, "value");
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Class<? extends Annotation> annotationType, @Nullable String attributeName) {
        if (annotationType == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        return MergedAnnotation.of(annotationType).getDefaultValue(attributeName).orElse(null);
    }

    public static <A extends Annotation> A synthesizeAnnotation(A a, @Nullable AnnotatedElement annotatedElement) {
        if (isSynthesizedAnnotation(a) || AnnotationFilter.PLAIN.matches(a)) {
            return a;
        }
        return (A) MergedAnnotation.from(annotatedElement, a).synthesize();
    }

    public static <A extends Annotation> A synthesizeAnnotation(Class<A> cls) {
        return (A) synthesizeAnnotation(Collections.emptyMap(), cls, null);
    }

    public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> map, Class<A> cls, @Nullable AnnotatedElement annotatedElement) {
        try {
            return (A) MergedAnnotation.of(annotatedElement, cls, map).synthesize();
        } catch (IllegalStateException | NoSuchElementException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, AnnotatedElement annotatedElement) {
        if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotations;
        }
        Annotation[] synthesized = (Annotation[]) Array.newInstance(annotations.getClass().componentType(), annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
        }
        return synthesized;
    }

    public static boolean isSynthesizedAnnotation(@Nullable Annotation annotation) {
        if (annotation != null) {
            try {
                if (Proxy.isProxyClass(annotation.getClass())) {
                    if (Proxy.getInvocationHandler(annotation) instanceof SynthesizedMergedAnnotationInvocationHandler) {
                        return true;
                    }
                }
            } catch (SecurityException e) {
                return false;
            }
        }
        return false;
    }

    public static void clearCache() {
        AnnotationTypeMappings.clearCache();
        AnnotationsScanner.clearCache();
        AttributeMethods.cache.clear();
        RepeatableContainers.cache.clear();
        OrderUtils.orderCache.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationUtils$DefaultValueHolder.class */
    public static class DefaultValueHolder {
        final Object defaultValue;

        public DefaultValueHolder(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String toString() {
            return "*" + this.defaultValue;
        }
    }
}
