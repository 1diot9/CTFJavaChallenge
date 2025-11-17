package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/TypeMappedAnnotation.class */
final class TypeMappedAnnotation<A extends Annotation> extends AbstractMergedAnnotation<A> {
    private static final Map<Class<?>, Object> EMPTY_ARRAYS = Map.of(Boolean.TYPE, new boolean[0], Byte.TYPE, new byte[0], Character.TYPE, new char[0], Double.TYPE, new double[0], Float.TYPE, new float[0], Integer.TYPE, new int[0], Long.TYPE, new long[0], Short.TYPE, new short[0], String.class, new String[0]);
    private final AnnotationTypeMapping mapping;

    @Nullable
    private final ClassLoader classLoader;

    @Nullable
    private final Object source;

    @Nullable
    private final Object rootAttributes;
    private final ValueExtractor valueExtractor;
    private final int aggregateIndex;
    private final boolean useMergedValues;

    @Nullable
    private final Predicate<String> attributeFilter;
    private final int[] resolvedRootMirrors;
    private final int[] resolvedMirrors;

    private TypeMappedAnnotation(AnnotationTypeMapping mapping, @Nullable ClassLoader classLoader, @Nullable Object source, @Nullable Object rootAttributes, ValueExtractor valueExtractor, int aggregateIndex) {
        this(mapping, classLoader, source, rootAttributes, valueExtractor, aggregateIndex, null);
    }

    private TypeMappedAnnotation(AnnotationTypeMapping mapping, @Nullable ClassLoader classLoader, @Nullable Object source, @Nullable Object rootAttributes, ValueExtractor valueExtractor, int aggregateIndex, @Nullable int[] resolvedRootMirrors) {
        this.mapping = mapping;
        this.classLoader = classLoader;
        this.source = source;
        this.rootAttributes = rootAttributes;
        this.valueExtractor = valueExtractor;
        this.aggregateIndex = aggregateIndex;
        this.useMergedValues = true;
        this.attributeFilter = null;
        this.resolvedRootMirrors = resolvedRootMirrors != null ? resolvedRootMirrors : mapping.getRoot().getMirrorSets().resolve(source, rootAttributes, this.valueExtractor);
        this.resolvedMirrors = getDistance() == 0 ? this.resolvedRootMirrors : mapping.getMirrorSets().resolve(source, this, this::getValueForMirrorResolution);
    }

    private TypeMappedAnnotation(AnnotationTypeMapping mapping, @Nullable ClassLoader classLoader, @Nullable Object source, @Nullable Object rootAnnotation, ValueExtractor valueExtractor, int aggregateIndex, boolean useMergedValues, @Nullable Predicate<String> attributeFilter, int[] resolvedRootMirrors, int[] resolvedMirrors) {
        this.classLoader = classLoader;
        this.source = source;
        this.rootAttributes = rootAnnotation;
        this.valueExtractor = valueExtractor;
        this.mapping = mapping;
        this.aggregateIndex = aggregateIndex;
        this.useMergedValues = useMergedValues;
        this.attributeFilter = attributeFilter;
        this.resolvedRootMirrors = resolvedRootMirrors;
        this.resolvedMirrors = resolvedMirrors;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Class<A> getType() {
        return (Class<A>) this.mapping.getAnnotationType();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public List<Class<? extends Annotation>> getMetaTypes() {
        return this.mapping.getMetaTypes();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean isPresent() {
        return true;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int getDistance() {
        return this.mapping.getDistance();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int getAggregateIndex() {
        return this.aggregateIndex;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    @Nullable
    public Object getSource() {
        return this.source;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    @Nullable
    public MergedAnnotation<?> getMetaSource() {
        AnnotationTypeMapping metaSourceMapping = this.mapping.getSource();
        if (metaSourceMapping == null) {
            return null;
        }
        return new TypeMappedAnnotation(metaSourceMapping, this.classLoader, this.source, this.rootAttributes, this.valueExtractor, this.aggregateIndex, this.resolvedRootMirrors);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<?> getRoot() {
        if (getDistance() == 0) {
            return this;
        }
        AnnotationTypeMapping rootMapping = this.mapping.getRoot();
        return new TypeMappedAnnotation(rootMapping, this.classLoader, this.source, this.rootAttributes, this.valueExtractor, this.aggregateIndex, this.resolvedRootMirrors);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean hasDefaultValue(String attributeName) {
        int attributeIndex = getAttributeIndex(attributeName, true);
        Object value = getValue(attributeIndex, true, false);
        return value == null || this.mapping.isEquivalentToDefaultValue(attributeIndex, value, this.valueExtractor);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException {
        int attributeIndex = getAttributeIndex(attributeName, true);
        Method attribute = this.mapping.getAttributes().get(attributeIndex);
        Assert.notNull(type, "Type must not be null");
        Assert.isAssignable((Class<?>) type, attribute.getReturnType(), (Supplier<String>) () -> {
            return "Attribute " + attributeName + " type mismatch:";
        });
        return (MergedAnnotation) getRequiredValue(attributeIndex, attributeName);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException {
        int attributeIndex = getAttributeIndex(attributeName, true);
        Method attribute = this.mapping.getAttributes().get(attributeIndex);
        Class<?> componentType = attribute.getReturnType().componentType();
        Assert.notNull(type, "Type must not be null");
        Assert.notNull(componentType, (Supplier<String>) () -> {
            return "Attribute " + attributeName + " is not an array";
        });
        Assert.isAssignable((Class<?>) type, componentType, (Supplier<String>) () -> {
            return "Attribute " + attributeName + " component type mismatch:";
        });
        return (MergedAnnotation[]) getRequiredValue(attributeIndex, attributeName);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T> Optional<T> getDefaultValue(String attributeName, Class<T> type) {
        int attributeIndex = getAttributeIndex(attributeName, false);
        if (attributeIndex == -1) {
            return Optional.empty();
        }
        Method attribute = this.mapping.getAttributes().get(attributeIndex);
        return Optional.ofNullable(adapt(attribute, attribute.getDefaultValue(), type));
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<A> filterAttributes(Predicate<String> predicate) {
        if (this.attributeFilter != null) {
            predicate = this.attributeFilter.and(predicate);
        }
        return new TypeMappedAnnotation(this.mapping, this.classLoader, this.source, this.rootAttributes, this.valueExtractor, this.aggregateIndex, this.useMergedValues, predicate, this.resolvedRootMirrors, this.resolvedMirrors);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<A> withNonMergedAttributes() {
        return new TypeMappedAnnotation(this.mapping, this.classLoader, this.source, this.rootAttributes, this.valueExtractor, this.aggregateIndex, false, this.attributeFilter, this.resolvedRootMirrors, this.resolvedMirrors);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Map<String, Object> asMap(MergedAnnotation.Adapt... adaptations) {
        return Collections.unmodifiableMap(asMap(mergedAnnotation -> {
            return new LinkedHashMap();
        }, adaptations));
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, MergedAnnotation.Adapt... adaptations) {
        T map = factory.apply(this);
        Assert.state(map != null, "Factory used to create MergedAnnotation Map must not return null");
        AttributeMethods attributes = this.mapping.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Method attribute = attributes.get(i);
            Object value = isFiltered(attribute.getName()) ? null : getValue(i, getTypeForMapOptions(attribute, adaptations));
            if (value != null) {
                map.put(attribute.getName(), adaptValueForMapOptions(attribute, value, map.getClass(), factory, adaptations));
            }
        }
        return map;
    }

    private Class<?> getTypeForMapOptions(Method attribute, MergedAnnotation.Adapt[] adaptations) {
        Class<?> attributeType = attribute.getReturnType();
        Class<?> componentType = attributeType.isArray() ? attributeType.componentType() : attributeType;
        if (MergedAnnotation.Adapt.CLASS_TO_STRING.isIn(adaptations) && componentType == Class.class) {
            return attributeType.isArray() ? String[].class : String.class;
        }
        return Object.class;
    }

    private <T extends Map<String, Object>> Object adaptValueForMapOptions(Method attribute, Object value, Class<?> mapType, Function<MergedAnnotation<?>, T> factory, MergedAnnotation.Adapt[] adaptations) {
        if (value instanceof MergedAnnotation) {
            MergedAnnotation<?> annotation = (MergedAnnotation) value;
            return MergedAnnotation.Adapt.ANNOTATION_TO_MAP.isIn(adaptations) ? annotation.asMap(factory, adaptations) : annotation.synthesize();
        }
        if (value instanceof MergedAnnotation[]) {
            MergedAnnotation<?>[] annotations = (MergedAnnotation[]) value;
            if (MergedAnnotation.Adapt.ANNOTATION_TO_MAP.isIn(adaptations)) {
                Object result = Array.newInstance(mapType, annotations.length);
                for (int i = 0; i < annotations.length; i++) {
                    Array.set(result, i, annotations[i].asMap(factory, adaptations));
                }
                return result;
            }
            Object result2 = Array.newInstance(attribute.getReturnType().componentType(), annotations.length);
            for (int i2 = 0; i2 < annotations.length; i2++) {
                Array.set(result2, i2, annotations[i2].synthesize());
            }
            return result2;
        }
        return value;
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation
    protected A createSynthesizedAnnotation() {
        if (isTargetAnnotation(this.rootAttributes) && !isSynthesizable((Annotation) this.rootAttributes)) {
            return (A) this.rootAttributes;
        }
        if (isTargetAnnotation(this.mapping.getAnnotation()) && !isSynthesizable(this.mapping.getAnnotation())) {
            return (A) this.mapping.getAnnotation();
        }
        return (A) SynthesizedMergedAnnotationInvocationHandler.createProxy(this, getType());
    }

    private boolean isTargetAnnotation(@Nullable Object obj) {
        return getType().isInstance(obj);
    }

    private boolean isSynthesizable(Annotation annotation) {
        if (AnnotationUtils.isSynthesizedAnnotation(annotation)) {
            return false;
        }
        if (getDistance() > 0 && this.resolvedMirrors.length > 0) {
            return true;
        }
        return this.mapping.isSynthesizable();
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation
    @Nullable
    protected <T> T getAttributeValue(String str, Class<T> cls) {
        int attributeIndex = getAttributeIndex(str, false);
        if (attributeIndex != -1) {
            return (T) getValue(attributeIndex, cls);
        }
        return null;
    }

    private Object getRequiredValue(int attributeIndex, String attributeName) {
        Object value = getValue(attributeIndex, (Class<Object>) Object.class);
        if (value == null) {
            throw new NoSuchElementException("No element at attribute index " + attributeIndex + " for name " + attributeName);
        }
        return value;
    }

    @Nullable
    private <T> T getValue(int i, Class<T> cls) {
        Method method = this.mapping.getAttributes().get(i);
        Object value = getValue(i, true, false);
        if (value == null) {
            value = method.getDefaultValue();
        }
        return (T) adapt(method, value, cls);
    }

    @Nullable
    private Object getValue(int attributeIndex, boolean useConventionMapping, boolean forMirrorResolution) {
        AnnotationTypeMapping mapping = this.mapping;
        if (this.useMergedValues) {
            int mappedIndex = this.mapping.getAliasMapping(attributeIndex);
            if (mappedIndex == -1 && useConventionMapping) {
                mappedIndex = this.mapping.getConventionMapping(attributeIndex);
            }
            if (mappedIndex != -1) {
                mapping = mapping.getRoot();
                attributeIndex = mappedIndex;
            }
        }
        if (!forMirrorResolution) {
            attributeIndex = (mapping.getDistance() != 0 ? this.resolvedMirrors : this.resolvedRootMirrors)[attributeIndex];
        }
        if (attributeIndex == -1) {
            return null;
        }
        if (mapping.getDistance() == 0) {
            Method attribute = mapping.getAttributes().get(attributeIndex);
            Object result = this.valueExtractor.extract(attribute, this.rootAttributes);
            return result != null ? result : attribute.getDefaultValue();
        }
        return getValueFromMetaAnnotation(attributeIndex, forMirrorResolution);
    }

    @Nullable
    private Object getValueFromMetaAnnotation(int attributeIndex, boolean forMirrorResolution) {
        Object value = null;
        if (this.useMergedValues || forMirrorResolution) {
            value = this.mapping.getMappedAnnotationValue(attributeIndex, forMirrorResolution);
        }
        if (value == null) {
            Method attribute = this.mapping.getAttributes().get(attributeIndex);
            value = AnnotationUtils.invokeAnnotationMethod(attribute, this.mapping.getAnnotation());
        }
        return value;
    }

    @Nullable
    private Object getValueForMirrorResolution(Method attribute, Object annotation) {
        int attributeIndex = this.mapping.getAttributes().indexOf(attribute);
        boolean valueAttribute = "value".equals(attribute.getName());
        return getValue(attributeIndex, !valueAttribute, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0161  */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private <T> T adapt(java.lang.reflect.Method r6, @org.springframework.lang.Nullable java.lang.Object r7, java.lang.Class<T> r8) {
        /*
            Method dump skipped, instructions count: 355
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.annotation.TypeMappedAnnotation.adapt(java.lang.reflect.Method, java.lang.Object, java.lang.Class):java.lang.Object");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object adaptForAttribute(Method attribute, Object value) {
        Class<?> attributeType = ClassUtils.resolvePrimitiveIfNecessary(attribute.getReturnType());
        if (attributeType.isArray() && !value.getClass().isArray()) {
            Object array = Array.newInstance(value.getClass(), 1);
            Array.set(array, 0, value);
            return adaptForAttribute(attribute, array);
        }
        if (attributeType.isAnnotation()) {
            return adaptToMergedAnnotation(value, attributeType);
        }
        if (attributeType.isArray() && attributeType.componentType().isAnnotation()) {
            MergedAnnotation<?>[] result = new MergedAnnotation[Array.getLength(value)];
            for (int i = 0; i < result.length; i++) {
                result[i] = adaptToMergedAnnotation(Array.get(value, i), attributeType.componentType());
            }
            return result;
        }
        if ((attributeType == Class.class && (value instanceof String)) || ((attributeType == Class[].class && (value instanceof String[])) || ((attributeType == String.class && (value instanceof Class)) || (attributeType == String[].class && (value instanceof Class[]))))) {
            return value;
        }
        if (attributeType.isArray() && isEmptyObjectArray(value)) {
            return emptyArray(attributeType.componentType());
        }
        if (!attributeType.isInstance(value)) {
            throw new IllegalStateException("Attribute '" + attribute.getName() + "' in annotation " + getType().getName() + " should be compatible with " + attributeType.getName() + " but a " + value.getClass().getName() + " value was returned");
        }
        return value;
    }

    private boolean isEmptyObjectArray(Object value) {
        if (value instanceof Object[]) {
            Object[] objects = (Object[]) value;
            if (objects.length == 0) {
                return true;
            }
        }
        return false;
    }

    private Object emptyArray(Class<?> componentType) {
        Object result = EMPTY_ARRAYS.get(componentType);
        if (result == null) {
            result = Array.newInstance(componentType, 0);
        }
        return result;
    }

    private MergedAnnotation<?> adaptToMergedAnnotation(Object value, Class<? extends Annotation> annotationType) {
        if (value instanceof MergedAnnotation) {
            MergedAnnotation<?> mergedAnnotation = (MergedAnnotation) value;
            return mergedAnnotation;
        }
        AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(annotationType).get(0);
        return new TypeMappedAnnotation(mapping, null, this.source, value, getValueExtractor(value), this.aggregateIndex);
    }

    private ValueExtractor getValueExtractor(Object value) {
        if (value instanceof Annotation) {
            return AnnotationUtils::invokeAnnotationMethod;
        }
        if (value instanceof Map) {
            return TypeMappedAnnotation::extractFromMap;
        }
        return this.valueExtractor;
    }

    private <T> Class<T> getAdaptType(Method method, Class<T> cls) {
        if (cls != Object.class) {
            return cls;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.isAnnotation()) {
            return MergedAnnotation.class;
        }
        if (returnType.isArray() && returnType.componentType().isAnnotation()) {
            return MergedAnnotation[].class;
        }
        return (Class<T>) ClassUtils.resolvePrimitiveIfNecessary(returnType);
    }

    private int getAttributeIndex(String attributeName, boolean required) {
        Assert.hasText(attributeName, "Attribute name must not be null");
        int attributeIndex = isFiltered(attributeName) ? -1 : this.mapping.getAttributes().indexOf(attributeName);
        if (attributeIndex == -1 && required) {
            throw new NoSuchElementException("No attribute named '" + attributeName + "' present in merged annotation " + getType().getName());
        }
        return attributeIndex;
    }

    private boolean isFiltered(String attributeName) {
        return (this.attributeFilter == null || this.attributeFilter.test(attributeName)) ? false : true;
    }

    @Nullable
    private ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.source != null) {
            Object obj = this.source;
            if (obj instanceof Class) {
                Class<?> clazz = (Class) obj;
                return clazz.getClassLoader();
            }
            Object obj2 = this.source;
            if (obj2 instanceof Member) {
                Member member = (Member) obj2;
                member.getDeclaringClass().getClassLoader();
                return null;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> MergedAnnotation<A> from(@Nullable Object source, A annotation) {
        Assert.notNull(annotation, "Annotation must not be null");
        AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(annotation.annotationType());
        return new TypeMappedAnnotation(mappings.get(0), null, source, annotation, AnnotationUtils::invokeAnnotationMethod, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> MergedAnnotation<A> of(@Nullable ClassLoader classLoader, @Nullable Object source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(annotationType);
        return new TypeMappedAnnotation(mappings.get(0), classLoader, source, attributes, TypeMappedAnnotation::extractFromMap, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.lang.annotation.Annotation] */
    @Nullable
    public static <A extends Annotation> TypeMappedAnnotation<A> createIfPossible(AnnotationTypeMapping mapping, MergedAnnotation<?> annotation, IntrospectionFailureLogger logger) {
        if (annotation instanceof TypeMappedAnnotation) {
            TypeMappedAnnotation<?> typeMappedAnnotation = (TypeMappedAnnotation) annotation;
            return createIfPossible(mapping, ((TypeMappedAnnotation) typeMappedAnnotation).source, ((TypeMappedAnnotation) typeMappedAnnotation).rootAttributes, ((TypeMappedAnnotation) typeMappedAnnotation).valueExtractor, ((TypeMappedAnnotation) typeMappedAnnotation).aggregateIndex, logger);
        }
        return createIfPossible(mapping, annotation.getSource(), annotation.synthesize(), annotation.getAggregateIndex(), logger);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static <A extends Annotation> TypeMappedAnnotation<A> createIfPossible(AnnotationTypeMapping mapping, @Nullable Object source, Annotation annotation, int aggregateIndex, IntrospectionFailureLogger logger) {
        return createIfPossible(mapping, source, annotation, AnnotationUtils::invokeAnnotationMethod, aggregateIndex, logger);
    }

    @Nullable
    private static <A extends Annotation> TypeMappedAnnotation<A> createIfPossible(AnnotationTypeMapping mapping, @Nullable Object source, @Nullable Object rootAttribute, ValueExtractor valueExtractor, int aggregateIndex, IntrospectionFailureLogger logger) {
        try {
            return new TypeMappedAnnotation<>(mapping, null, source, rootAttribute, valueExtractor, aggregateIndex);
        } catch (Exception ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            if (logger.isEnabled()) {
                String type = mapping.getAnnotationType().getName();
                String item = mapping.getDistance() == 0 ? "annotation " + type : "meta-annotation " + type + " from " + mapping.getRoot().getAnnotationType().getName();
                logger.log("Failed to introspect " + item, source, ex);
                return null;
            }
            return null;
        }
    }

    @Nullable
    static Object extractFromMap(Method attribute, @Nullable Object map) {
        if (map != null) {
            return ((Map) map).get(attribute.getName());
        }
        return null;
    }
}
