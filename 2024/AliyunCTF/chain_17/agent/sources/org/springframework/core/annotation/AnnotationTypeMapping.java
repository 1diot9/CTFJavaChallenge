package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationTypeMapping.class */
public final class AnnotationTypeMapping {
    private static final Log logger = LogFactory.getLog((Class<?>) AnnotationTypeMapping.class);
    private static final Predicate<? super Annotation> isBeanValidationConstraint = annotation -> {
        return annotation.annotationType().getName().equals("jakarta.validation.Constraint");
    };
    private static final Set<String> conventionBasedOverrideCheckCache = ConcurrentHashMap.newKeySet();
    private static final MirrorSets.MirrorSet[] EMPTY_MIRROR_SETS = new MirrorSets.MirrorSet[0];

    @Nullable
    private final AnnotationTypeMapping source;
    private final AnnotationTypeMapping root;
    private final int distance;
    private final Class<? extends Annotation> annotationType;
    private final List<Class<? extends Annotation>> metaTypes;

    @Nullable
    private final Annotation annotation;
    private final AttributeMethods attributes;
    private final MirrorSets mirrorSets;
    private final int[] aliasMappings;
    private final int[] conventionMappings;
    private final int[] annotationValueMappings;
    private final AnnotationTypeMapping[] annotationValueSource;
    private final Map<Method, List<Method>> aliasedBy;
    private final boolean synthesizable;
    private final Set<Method> claimedAliases = new HashSet();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationTypeMapping(@Nullable AnnotationTypeMapping source, Class<? extends Annotation> annotationType, @Nullable Annotation annotation, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        this.source = source;
        this.root = source != null ? source.getRoot() : this;
        this.distance = source == null ? 0 : source.getDistance() + 1;
        this.annotationType = annotationType;
        this.metaTypes = merge(source != null ? source.getMetaTypes() : null, annotationType);
        this.annotation = annotation;
        this.attributes = AttributeMethods.forAnnotationType(annotationType);
        this.mirrorSets = new MirrorSets();
        this.aliasMappings = filledIntArray(this.attributes.size());
        this.conventionMappings = filledIntArray(this.attributes.size());
        this.annotationValueMappings = filledIntArray(this.attributes.size());
        this.annotationValueSource = new AnnotationTypeMapping[this.attributes.size()];
        this.aliasedBy = resolveAliasedForTargets();
        processAliases();
        addConventionMappings();
        addConventionAnnotationValues();
        this.synthesizable = computeSynthesizableFlag(visitedAnnotationTypes);
    }

    private static <T> List<T> merge(@Nullable List<T> existing, T element) {
        if (existing == null) {
            return Collections.singletonList(element);
        }
        List<T> merged = new ArrayList<>(existing.size() + 1);
        merged.addAll(existing);
        merged.add(element);
        return Collections.unmodifiableList(merged);
    }

    private Map<Method, List<Method>> resolveAliasedForTargets() {
        Map<Method, List<Method>> aliasedBy = new HashMap<>();
        for (int i = 0; i < this.attributes.size(); i++) {
            Method attribute = this.attributes.get(i);
            AliasFor aliasFor = (AliasFor) AnnotationsScanner.getDeclaredAnnotation(attribute, AliasFor.class);
            if (aliasFor != null) {
                Method target = resolveAliasTarget(attribute, aliasFor);
                aliasedBy.computeIfAbsent(target, key -> {
                    return new ArrayList();
                }).add(attribute);
            }
        }
        return Collections.unmodifiableMap(aliasedBy);
    }

    private Method resolveAliasTarget(Method attribute, AliasFor aliasFor) {
        return resolveAliasTarget(attribute, aliasFor, true);
    }

    private Method resolveAliasTarget(Method attribute, AliasFor aliasFor, boolean checkAliasPair) {
        AliasFor targetAliasFor;
        if (StringUtils.hasText(aliasFor.value()) && StringUtils.hasText(aliasFor.attribute())) {
            throw new AnnotationConfigurationException(String.format("In @AliasFor declared on %s, attribute 'attribute' and its alias 'value' are present with values of '%s' and '%s', but only one is permitted.", AttributeMethods.describe(attribute), aliasFor.attribute(), aliasFor.value()));
        }
        Class<? extends Annotation> targetAnnotation = aliasFor.annotation();
        if (targetAnnotation == Annotation.class) {
            targetAnnotation = this.annotationType;
        }
        String targetAttributeName = aliasFor.attribute();
        if (!StringUtils.hasLength(targetAttributeName)) {
            targetAttributeName = aliasFor.value();
        }
        if (!StringUtils.hasLength(targetAttributeName)) {
            targetAttributeName = attribute.getName();
        }
        Method target = AttributeMethods.forAnnotationType(targetAnnotation).get(targetAttributeName);
        if (target == null) {
            if (targetAnnotation == this.annotationType) {
                throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s declares an alias for '%s' which is not present.", AttributeMethods.describe(attribute), targetAttributeName));
            }
            throw new AnnotationConfigurationException(String.format("%s is declared as an @AliasFor nonexistent %s.", StringUtils.capitalize(AttributeMethods.describe(attribute)), AttributeMethods.describe(targetAnnotation, targetAttributeName)));
        }
        if (target.equals(attribute)) {
            throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s points to itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.", AttributeMethods.describe(attribute)));
        }
        if (!isCompatibleReturnType(attribute.getReturnType(), target.getReturnType())) {
            throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare the same return type.", AttributeMethods.describe(attribute), AttributeMethods.describe(target)));
        }
        if (isAliasPair(target) && checkAliasPair && (targetAliasFor = (AliasFor) target.getAnnotation(AliasFor.class)) != null) {
            Method mirror = resolveAliasTarget(target, targetAliasFor, false);
            if (!mirror.equals(attribute)) {
                throw new AnnotationConfigurationException(String.format("%s must be declared as an @AliasFor %s, not %s.", StringUtils.capitalize(AttributeMethods.describe(target)), AttributeMethods.describe(attribute), AttributeMethods.describe(mirror)));
            }
        }
        return target;
    }

    private boolean isAliasPair(Method target) {
        return this.annotationType == target.getDeclaringClass();
    }

    private boolean isCompatibleReturnType(Class<?> attributeType, Class<?> targetType) {
        return attributeType == targetType || attributeType == targetType.componentType();
    }

    private void processAliases() {
        List<Method> aliases = new ArrayList<>();
        for (int i = 0; i < this.attributes.size(); i++) {
            aliases.clear();
            aliases.add(this.attributes.get(i));
            collectAliases(aliases);
            if (aliases.size() > 1) {
                processAliases(i, aliases);
            }
        }
    }

    private void collectAliases(List<Method> aliases) {
        AnnotationTypeMapping annotationTypeMapping = this;
        while (true) {
            AnnotationTypeMapping mapping = annotationTypeMapping;
            if (mapping != null) {
                int size = aliases.size();
                for (int j = 0; j < size; j++) {
                    List<Method> additional = mapping.aliasedBy.get(aliases.get(j));
                    if (additional != null) {
                        aliases.addAll(additional);
                    }
                }
                annotationTypeMapping = mapping.source;
            } else {
                return;
            }
        }
    }

    private void processAliases(int attributeIndex, List<Method> aliases) {
        int rootAttributeIndex = getFirstRootAttributeIndex(aliases);
        AnnotationTypeMapping annotationTypeMapping = this;
        while (true) {
            AnnotationTypeMapping mapping = annotationTypeMapping;
            if (mapping != null) {
                if (rootAttributeIndex != -1 && mapping != this.root) {
                    for (int i = 0; i < mapping.attributes.size(); i++) {
                        if (aliases.contains(mapping.attributes.get(i))) {
                            mapping.aliasMappings[i] = rootAttributeIndex;
                        }
                    }
                }
                mapping.mirrorSets.updateFrom(aliases);
                mapping.claimedAliases.addAll(aliases);
                if (mapping.annotation != null) {
                    int[] resolvedMirrors = mapping.mirrorSets.resolve(null, mapping.annotation, AnnotationUtils::invokeAnnotationMethod);
                    for (int i2 = 0; i2 < mapping.attributes.size(); i2++) {
                        if (aliases.contains(mapping.attributes.get(i2))) {
                            this.annotationValueMappings[attributeIndex] = resolvedMirrors[i2];
                            this.annotationValueSource[attributeIndex] = mapping;
                        }
                    }
                }
                annotationTypeMapping = mapping.source;
            } else {
                return;
            }
        }
    }

    private int getFirstRootAttributeIndex(Collection<Method> aliases) {
        AttributeMethods rootAttributes = this.root.getAttributes();
        for (int i = 0; i < rootAttributes.size(); i++) {
            if (aliases.contains(rootAttributes.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private void addConventionMappings() {
        if (this.distance == 0) {
            return;
        }
        AttributeMethods rootAttributes = this.root.getAttributes();
        int[] mappings = this.conventionMappings;
        Set<String> conventionMappedAttributes = new HashSet<>();
        for (int i = 0; i < mappings.length; i++) {
            String name = this.attributes.get(i).getName();
            int mapped = rootAttributes.indexOf(name);
            if (!"value".equals(name) && mapped != -1 && !isExplicitAttributeOverride(name)) {
                conventionMappedAttributes.add(name);
                mappings[i] = mapped;
                MirrorSets.MirrorSet mirrors = getMirrorSets().getAssigned(i);
                if (mirrors != null) {
                    for (int j = 0; j < mirrors.size(); j++) {
                        mappings[mirrors.getAttributeIndex(j)] = mapped;
                    }
                }
            }
        }
        String rootAnnotationTypeName = this.root.annotationType.getName();
        String cacheKey = rootAnnotationTypeName + "-" + this.annotationType.getName();
        if (conventionBasedOverrideCheckCache.add(cacheKey) && !conventionMappedAttributes.isEmpty() && Arrays.stream(this.annotationType.getAnnotations()).noneMatch(isBeanValidationConstraint) && logger.isWarnEnabled()) {
            logger.warn("Support for convention-based annotation attribute overrides is deprecated and will be removed in Spring Framework 6.2. Please annotate the following attributes in @%s with appropriate @AliasFor declarations: %s".formatted(rootAnnotationTypeName, conventionMappedAttributes));
        }
    }

    private boolean isExplicitAttributeOverride(String name) {
        AliasFor aliasFor;
        Method attribute = this.root.getAttributes().get(name);
        return (attribute == null || (aliasFor = (AliasFor) AnnotationsScanner.getDeclaredAnnotation(attribute, AliasFor.class)) == null || aliasFor.annotation() == Annotation.class || aliasFor.annotation() == this.root.annotationType) ? false : true;
    }

    private void addConventionAnnotationValues() {
        for (int i = 0; i < this.attributes.size(); i++) {
            Method attribute = this.attributes.get(i);
            boolean isValueAttribute = "value".equals(attribute.getName());
            AnnotationTypeMapping annotationTypeMapping = this;
            while (true) {
                AnnotationTypeMapping mapping = annotationTypeMapping;
                if (mapping != null && mapping.distance > 0) {
                    int mapped = mapping.getAttributes().indexOf(attribute.getName());
                    if (mapped != -1 && isBetterConventionAnnotationValue(i, isValueAttribute, mapping)) {
                        this.annotationValueMappings[i] = mapped;
                        this.annotationValueSource[i] = mapping;
                    }
                    annotationTypeMapping = mapping.source;
                }
            }
        }
    }

    private boolean isBetterConventionAnnotationValue(int index, boolean isValueAttribute, AnnotationTypeMapping mapping) {
        if (this.annotationValueMappings[index] == -1) {
            return true;
        }
        int existingDistance = this.annotationValueSource[index].distance;
        return !isValueAttribute && existingDistance > mapping.distance;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean computeSynthesizableFlag(Set<Class<? extends Annotation>> visitedAnnotationTypes) {
        visitedAnnotationTypes.add(this.annotationType);
        for (int index : this.aliasMappings) {
            if (index != -1) {
                return true;
            }
        }
        if (!this.aliasedBy.isEmpty()) {
            return true;
        }
        for (int index2 : this.conventionMappings) {
            if (index2 != -1) {
                return true;
            }
        }
        if (getAttributes().hasNestedAnnotation()) {
            AttributeMethods attributeMethods = getAttributes();
            for (int i = 0; i < attributeMethods.size(); i++) {
                Method method = attributeMethods.get(i);
                Class<?> type = method.getReturnType();
                if (type.isAnnotation() || (type.isArray() && type.componentType().isAnnotation())) {
                    Class<?> componentType = type.isAnnotation() ? type : type.componentType();
                    if (visitedAnnotationTypes.add(componentType)) {
                        AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(componentType, visitedAnnotationTypes).get(0);
                        if (mapping.isSynthesizable()) {
                            return true;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void afterAllMappingsSet() {
        validateAllAliasesClaimed();
        for (int i = 0; i < this.mirrorSets.size(); i++) {
            validateMirrorSet(this.mirrorSets.get(i));
        }
        this.claimedAliases.clear();
    }

    private void validateAllAliasesClaimed() {
        for (int i = 0; i < this.attributes.size(); i++) {
            Method attribute = this.attributes.get(i);
            AliasFor aliasFor = (AliasFor) AnnotationsScanner.getDeclaredAnnotation(attribute, AliasFor.class);
            if (aliasFor != null && !this.claimedAliases.contains(attribute)) {
                Method target = resolveAliasTarget(attribute, aliasFor);
                throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s declares an alias for %s which is not meta-present.", AttributeMethods.describe(attribute), AttributeMethods.describe(target)));
            }
        }
    }

    private void validateMirrorSet(MirrorSets.MirrorSet mirrorSet) {
        Method firstAttribute = mirrorSet.get(0);
        Object firstDefaultValue = firstAttribute.getDefaultValue();
        for (int i = 1; i <= mirrorSet.size() - 1; i++) {
            Method mirrorAttribute = mirrorSet.get(i);
            Object mirrorDefaultValue = mirrorAttribute.getDefaultValue();
            if (firstDefaultValue == null || mirrorDefaultValue == null) {
                throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare default values.", AttributeMethods.describe(firstAttribute), AttributeMethods.describe(mirrorAttribute)));
            }
            if (!ObjectUtils.nullSafeEquals(firstDefaultValue, mirrorDefaultValue)) {
                throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare the same default value.", AttributeMethods.describe(firstAttribute), AttributeMethods.describe(mirrorAttribute)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationTypeMapping getRoot() {
        return this.root;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public AnnotationTypeMapping getSource() {
        return this.source;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDistance() {
        return this.distance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Class<? extends Annotation>> getMetaTypes() {
        return this.metaTypes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Annotation getAnnotation() {
        return this.annotation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AttributeMethods getAttributes() {
        return this.attributes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAliasMapping(int attributeIndex) {
        return this.aliasMappings[attributeIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getConventionMapping(int attributeIndex) {
        return this.conventionMappings[attributeIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Object getMappedAnnotationValue(int attributeIndex, boolean metaAnnotationsOnly) {
        int mappedIndex = this.annotationValueMappings[attributeIndex];
        if (mappedIndex == -1) {
            return null;
        }
        AnnotationTypeMapping source = this.annotationValueSource[attributeIndex];
        if (source == this && metaAnnotationsOnly) {
            return null;
        }
        return AnnotationUtils.invokeAnnotationMethod(source.attributes.get(mappedIndex), source.annotation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEquivalentToDefaultValue(int attributeIndex, Object value, ValueExtractor valueExtractor) {
        Method attribute = this.attributes.get(attributeIndex);
        return isEquivalentToDefaultValue(attribute, value, valueExtractor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MirrorSets getMirrorSets() {
        return this.mirrorSets;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSynthesizable() {
        return this.synthesizable;
    }

    private static int[] filledIntArray(int size) {
        int[] array = new int[size];
        Arrays.fill(array, -1);
        return array;
    }

    private static boolean isEquivalentToDefaultValue(Method attribute, Object value, ValueExtractor valueExtractor) {
        return areEquivalent(attribute.getDefaultValue(), value, valueExtractor);
    }

    private static boolean areEquivalent(@Nullable Object value, @Nullable Object extractedValue, ValueExtractor valueExtractor) {
        if (ObjectUtils.nullSafeEquals(value, extractedValue)) {
            return true;
        }
        if (value instanceof Class) {
            Class<?> clazz = (Class) value;
            if (extractedValue instanceof String) {
                String string = (String) extractedValue;
                return areEquivalent(clazz, string);
            }
        }
        if (value instanceof Class[]) {
            Class<?>[] classes = (Class[]) value;
            if (extractedValue instanceof String[]) {
                String[] strings = (String[]) extractedValue;
                return areEquivalent(classes, strings);
            }
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            return areEquivalent(annotation, extractedValue, valueExtractor);
        }
        return false;
    }

    private static boolean areEquivalent(Class<?>[] value, String[] extractedValue) {
        if (value.length != extractedValue.length) {
            return false;
        }
        for (int i = 0; i < value.length; i++) {
            if (!areEquivalent(value[i], extractedValue[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean areEquivalent(Class<?> value, String extractedValue) {
        return value.getName().equals(extractedValue);
    }

    private static boolean areEquivalent(Annotation annotation, @Nullable Object extractedValue, ValueExtractor valueExtractor) {
        Object value2;
        AttributeMethods attributes = AttributeMethods.forAnnotationType(annotation.annotationType());
        for (int i = 0; i < attributes.size(); i++) {
            Method attribute = attributes.get(i);
            Object value1 = AnnotationUtils.invokeAnnotationMethod(attribute, annotation);
            if (extractedValue instanceof TypeMappedAnnotation) {
                TypeMappedAnnotation<?> typeMappedAnnotation = (TypeMappedAnnotation) extractedValue;
                value2 = typeMappedAnnotation.getValue(attribute.getName()).orElse(null);
            } else {
                value2 = valueExtractor.extract(attribute, extractedValue);
            }
            if (!areEquivalent(value1, value2, valueExtractor)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationTypeMapping$MirrorSets.class */
    public class MirrorSets {
        private MirrorSet[] mirrorSets = AnnotationTypeMapping.EMPTY_MIRROR_SETS;
        private final MirrorSet[] assigned;

        MirrorSets() {
            this.assigned = new MirrorSet[AnnotationTypeMapping.this.attributes.size()];
        }

        void updateFrom(Collection<Method> aliases) {
            MirrorSet mirrorSet = null;
            int size = 0;
            int last = -1;
            for (int i = 0; i < AnnotationTypeMapping.this.attributes.size(); i++) {
                Method attribute = AnnotationTypeMapping.this.attributes.get(i);
                if (aliases.contains(attribute)) {
                    size++;
                    if (size > 1) {
                        if (mirrorSet == null) {
                            mirrorSet = new MirrorSet();
                            this.assigned[last] = mirrorSet;
                        }
                        this.assigned[i] = mirrorSet;
                    }
                    last = i;
                }
            }
            if (mirrorSet != null) {
                mirrorSet.update();
                Set<MirrorSet> unique = new LinkedHashSet<>(Arrays.asList(this.assigned));
                unique.remove(null);
                this.mirrorSets = (MirrorSet[]) unique.toArray(AnnotationTypeMapping.EMPTY_MIRROR_SETS);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int size() {
            return this.mirrorSets.length;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public MirrorSet get(int index) {
            return this.mirrorSets[index];
        }

        @Nullable
        MirrorSet getAssigned(int attributeIndex) {
            return this.assigned[attributeIndex];
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int[] resolve(@Nullable Object source, @Nullable Object annotation, ValueExtractor valueExtractor) {
            int[] result = new int[AnnotationTypeMapping.this.attributes.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = i;
            }
            for (int i2 = 0; i2 < size(); i2++) {
                MirrorSet mirrorSet = get(i2);
                int resolved = mirrorSet.resolve(source, annotation, valueExtractor);
                for (int j = 0; j < mirrorSet.size; j++) {
                    result[mirrorSet.indexes[j]] = resolved;
                }
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationTypeMapping$MirrorSets$MirrorSet.class */
        public class MirrorSet {
            private int size;
            private final int[] indexes;

            MirrorSet() {
                this.indexes = new int[AnnotationTypeMapping.this.attributes.size()];
            }

            void update() {
                this.size = 0;
                Arrays.fill(this.indexes, -1);
                for (int i = 0; i < MirrorSets.this.assigned.length; i++) {
                    if (MirrorSets.this.assigned[i] == this) {
                        this.indexes[this.size] = i;
                        this.size++;
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public <A> int resolve(@Nullable Object source, @Nullable A annotation, ValueExtractor valueExtractor) {
                int result = -1;
                Object lastValue = null;
                for (int i = 0; i < this.size; i++) {
                    Method attribute = AnnotationTypeMapping.this.attributes.get(this.indexes[i]);
                    Object value = valueExtractor.extract(attribute, annotation);
                    boolean isDefaultValue = value == null || AnnotationTypeMapping.isEquivalentToDefaultValue(attribute, value, valueExtractor);
                    if (isDefaultValue || ObjectUtils.nullSafeEquals(lastValue, value)) {
                        if (result == -1) {
                            result = this.indexes[i];
                        }
                    } else {
                        if (lastValue != null && !ObjectUtils.nullSafeEquals(lastValue, value)) {
                            String on = source != null ? " declared on " + source : "";
                            throw new AnnotationConfigurationException(String.format("Different @AliasFor mirror values for annotation [%s]%s; attribute '%s' and its alias '%s' are declared with values of [%s] and [%s].", AnnotationTypeMapping.this.getAnnotationType().getName(), on, AnnotationTypeMapping.this.attributes.get(result).getName(), attribute.getName(), ObjectUtils.nullSafeToString(lastValue), ObjectUtils.nullSafeToString(value)));
                        }
                        result = this.indexes[i];
                        lastValue = value;
                    }
                }
                return result;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public int size() {
                return this.size;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public Method get(int index) {
                int attributeIndex = this.indexes[index];
                return AnnotationTypeMapping.this.attributes.get(attributeIndex);
            }

            int getAttributeIndex(int index) {
                return this.indexes[index];
            }
        }
    }
}
