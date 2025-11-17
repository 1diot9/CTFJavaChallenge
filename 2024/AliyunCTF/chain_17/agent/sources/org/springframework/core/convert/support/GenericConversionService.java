package org.springframework.core.convert.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService.class */
public class GenericConversionService implements ConfigurableConversionService {
    private static final GenericConverter NO_OP_CONVERTER = new NoOpConverter("NO_OP");
    private static final GenericConverter NO_MATCH = new NoOpConverter("NO_MATCH");
    private final Converters converters = new Converters();
    private final Map<ConverterCacheKey, GenericConverter> converterCache = new ConcurrentReferenceHashMap(64);

    public void addConverter(Converter<?, ?> converter) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(converter.getClass(), Converter.class);
        if (typeInfo == null && (converter instanceof DecoratingProxy)) {
            DecoratingProxy decoratingProxy = (DecoratingProxy) converter;
            typeInfo = getRequiredTypeInfo(decoratingProxy.getDecoratedClass(), Converter.class);
        }
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type <S> and target type <T> for your Converter [" + converter.getClass().getName() + "]; does the class parameterize those types?");
        }
        addConverter(new ConverterAdapter(converter, typeInfo[0], typeInfo[1]));
    }

    public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter) {
        addConverter(new ConverterAdapter(converter, ResolvableType.forClass(sourceType), ResolvableType.forClass(targetType)));
    }

    public void addConverter(GenericConverter converter) {
        this.converters.add(converter);
        invalidateCache();
    }

    public void addConverterFactory(ConverterFactory<?, ?> factory) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(factory.getClass(), ConverterFactory.class);
        if (typeInfo == null && (factory instanceof DecoratingProxy)) {
            DecoratingProxy decoratingProxy = (DecoratingProxy) factory;
            typeInfo = getRequiredTypeInfo(decoratingProxy.getDecoratedClass(), ConverterFactory.class);
        }
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type <S> and target type <T> for your ConverterFactory [" + factory.getClass().getName() + "]; does the class parameterize those types?");
        }
        addConverter(new ConverterFactoryAdapter(factory, new GenericConverter.ConvertiblePair(typeInfo[0].toClass(), typeInfo[1].toClass())));
    }

    public void removeConvertible(Class<?> sourceType, Class<?> targetType) {
        this.converters.remove(sourceType, targetType);
        invalidateCache();
    }

    @Override // org.springframework.core.convert.ConversionService
    public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return canConvert(sourceType != null ? TypeDescriptor.valueOf(sourceType) : null, TypeDescriptor.valueOf(targetType));
    }

    @Override // org.springframework.core.convert.ConversionService
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return sourceType == null || getConverter(sourceType, targetType) != null;
    }

    public boolean canBypassConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return sourceType == null || getConverter(sourceType, targetType) == NO_OP_CONVERTER;
    }

    @Override // org.springframework.core.convert.ConversionService
    @Nullable
    public <T> T convert(@Nullable Object obj, Class<T> cls) {
        Assert.notNull(cls, "Target type to convert to cannot be null");
        return (T) convert(obj, TypeDescriptor.forObject(obj), TypeDescriptor.valueOf(cls));
    }

    @Override // org.springframework.core.convert.ConversionService
    @Nullable
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        if (sourceType == null) {
            Assert.isTrue(source == null, "Source must be [null] if source type == [null]");
            return handleResult(null, targetType, convertNullSource(null, targetType));
        }
        if (source != null && !sourceType.getObjectType().isInstance(source)) {
            throw new IllegalArgumentException("Source to convert from must be an instance of [" + sourceType + "]; instead it was a [" + source.getClass().getName() + "]");
        }
        GenericConverter converter = getConverter(sourceType, targetType);
        if (converter != null) {
            Object result = ConversionUtils.invokeConverter(converter, source, sourceType, targetType);
            return handleResult(sourceType, targetType, result);
        }
        return handleConverterNotFound(source, sourceType, targetType);
    }

    public String toString() {
        return this.converters.toString();
    }

    @Nullable
    protected Object convertNullSource(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getObjectType() == Optional.class) {
            return Optional.empty();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConverterCacheKey key = new ConverterCacheKey(sourceType, targetType);
        GenericConverter converter = this.converterCache.get(key);
        if (converter != null) {
            if (converter != NO_MATCH) {
                return converter;
            }
            return null;
        }
        GenericConverter converter2 = this.converters.find(sourceType, targetType);
        if (converter2 == null) {
            converter2 = getDefaultConverter(sourceType, targetType);
        }
        if (converter2 != null) {
            this.converterCache.put(key, converter2);
            return converter2;
        }
        this.converterCache.put(key, NO_MATCH);
        return null;
    }

    @Nullable
    protected GenericConverter getDefaultConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(targetType)) {
            return NO_OP_CONVERTER;
        }
        return null;
    }

    @Nullable
    private ResolvableType[] getRequiredTypeInfo(Class<?> converterClass, Class<?> genericIfc) {
        ResolvableType resolvableType = ResolvableType.forClass(converterClass).as(genericIfc);
        ResolvableType[] generics = resolvableType.getGenerics();
        if (generics.length < 2) {
            return null;
        }
        Class<?> sourceType = generics[0].resolve();
        Class<?> targetType = generics[1].resolve();
        if (sourceType == null || targetType == null) {
            return null;
        }
        return generics;
    }

    private void invalidateCache() {
        this.converterCache.clear();
    }

    @Nullable
    private Object handleConverterNotFound(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            assertNotPrimitiveTargetType(sourceType, targetType);
            return null;
        }
        if ((sourceType == null || sourceType.isAssignableTo(targetType)) && targetType.getObjectType().isInstance(source)) {
            return source;
        }
        throw new ConverterNotFoundException(sourceType, targetType);
    }

    @Nullable
    private Object handleResult(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType, @Nullable Object result) {
        if (result == null) {
            assertNotPrimitiveTargetType(sourceType, targetType);
        }
        return result;
    }

    private void assertNotPrimitiveTargetType(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.isPrimitive()) {
            throw new ConversionFailedException(sourceType, targetType, null, new IllegalArgumentException("A null value cannot be assigned to a primitive type"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$ConverterAdapter.class */
    public final class ConverterAdapter implements ConditionalGenericConverter {
        private final Converter<Object, Object> converter;
        private final GenericConverter.ConvertiblePair typeInfo;
        private final ResolvableType targetType;

        public ConverterAdapter(Converter<?, ?> converter, ResolvableType sourceType, ResolvableType targetType) {
            this.converter = converter;
            this.typeInfo = new GenericConverter.ConvertiblePair(sourceType.toClass(), targetType.toClass());
            this.targetType = targetType;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (this.typeInfo.getTargetType() != targetType.getObjectType()) {
                return false;
            }
            ResolvableType rt = targetType.getResolvableType();
            if (!(rt.getType() instanceof Class) && !rt.isAssignableFrom(this.targetType) && !this.targetType.hasUnresolvableGenerics()) {
                return false;
            }
            Converter<Object, Object> converter = this.converter;
            if (converter instanceof ConditionalConverter) {
                ConditionalConverter conditionalConverter = (ConditionalConverter) converter;
                if (!conditionalConverter.matches(sourceType, targetType)) {
                    return false;
                }
            }
            return true;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converter.convert(source);
        }

        public String toString() {
            return this.typeInfo + " : " + this.converter;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$ConverterFactoryAdapter.class */
    public final class ConverterFactoryAdapter implements ConditionalGenericConverter {
        private final ConverterFactory<Object, Object> converterFactory;
        private final GenericConverter.ConvertiblePair typeInfo;

        public ConverterFactoryAdapter(ConverterFactory<?, ?> converterFactory, GenericConverter.ConvertiblePair typeInfo) {
            this.converterFactory = converterFactory;
            this.typeInfo = typeInfo;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            boolean matches = true;
            ConverterFactory<Object, Object> converterFactory = this.converterFactory;
            if (converterFactory instanceof ConditionalConverter) {
                ConditionalConverter conditionalConverter = (ConditionalConverter) converterFactory;
                matches = conditionalConverter.matches(sourceType, targetType);
            }
            if (matches) {
                Converter<?, ?> converter = this.converterFactory.getConverter(targetType.getType());
                if (converter instanceof ConditionalConverter) {
                    ConditionalConverter conditionalConverter2 = (ConditionalConverter) converter;
                    matches = conditionalConverter2.matches(sourceType, targetType);
                }
            }
            return matches;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converterFactory.getConverter(targetType.getObjectType()).convert(source);
        }

        public String toString() {
            return this.typeInfo + " : " + this.converterFactory;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$ConverterCacheKey.class */
    public static final class ConverterCacheKey implements Comparable<ConverterCacheKey> {
        private final TypeDescriptor sourceType;
        private final TypeDescriptor targetType;

        public ConverterCacheKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ConverterCacheKey) {
                    ConverterCacheKey that = (ConverterCacheKey) other;
                    if (!this.sourceType.equals(that.sourceType) || !this.targetType.equals(that.targetType)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.sourceType.hashCode() * 29) + this.targetType.hashCode();
        }

        public String toString() {
            return "ConverterCacheKey [sourceType = " + this.sourceType + ", targetType = " + this.targetType + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(ConverterCacheKey other) {
            int result = this.sourceType.getResolvableType().toString().compareTo(other.sourceType.getResolvableType().toString());
            if (result == 0) {
                result = this.targetType.getResolvableType().toString().compareTo(other.targetType.getResolvableType().toString());
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$Converters.class */
    public static class Converters {
        private final Set<GenericConverter> globalConverters = new CopyOnWriteArraySet();
        private final Map<GenericConverter.ConvertiblePair, ConvertersForPair> converters = new ConcurrentHashMap(256);

        private Converters() {
        }

        public void add(GenericConverter converter) {
            Set<GenericConverter.ConvertiblePair> convertibleTypes = converter.getConvertibleTypes();
            if (convertibleTypes == null) {
                Assert.state(converter instanceof ConditionalConverter, "Only conditional converters may return null convertible types");
                this.globalConverters.add(converter);
            } else {
                for (GenericConverter.ConvertiblePair convertiblePair : convertibleTypes) {
                    getMatchableConverters(convertiblePair).add(converter);
                }
            }
        }

        private ConvertersForPair getMatchableConverters(GenericConverter.ConvertiblePair convertiblePair) {
            return this.converters.computeIfAbsent(convertiblePair, k -> {
                return new ConvertersForPair();
            });
        }

        public void remove(Class<?> sourceType, Class<?> targetType) {
            this.converters.remove(new GenericConverter.ConvertiblePair(sourceType, targetType));
        }

        @Nullable
        public GenericConverter find(TypeDescriptor sourceType, TypeDescriptor targetType) {
            List<Class<?>> sourceCandidates = getClassHierarchy(sourceType.getType());
            List<Class<?>> targetCandidates = getClassHierarchy(targetType.getType());
            for (Class<?> sourceCandidate : sourceCandidates) {
                for (Class<?> targetCandidate : targetCandidates) {
                    GenericConverter.ConvertiblePair convertiblePair = new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                    GenericConverter converter = getRegisteredConverter(sourceType, targetType, convertiblePair);
                    if (converter != null) {
                        return converter;
                    }
                }
            }
            return null;
        }

        @Nullable
        private GenericConverter getRegisteredConverter(TypeDescriptor sourceType, TypeDescriptor targetType, GenericConverter.ConvertiblePair convertiblePair) {
            GenericConverter converter;
            ConvertersForPair convertersForPair = this.converters.get(convertiblePair);
            if (convertersForPair != null && (converter = convertersForPair.getConverter(sourceType, targetType)) != null) {
                return converter;
            }
            for (GenericConverter globalConverter : this.globalConverters) {
                if (((ConditionalConverter) globalConverter).matches(sourceType, targetType)) {
                    return globalConverter;
                }
            }
            return null;
        }

        private List<Class<?>> getClassHierarchy(Class<?> type) {
            List<Class<?>> hierarchy = new ArrayList<>(20);
            Set<Class<?>> visited = new HashSet<>(20);
            addToClassHierarchy(0, ClassUtils.resolvePrimitiveIfNecessary(type), false, hierarchy, visited);
            boolean array = type.isArray();
            for (int i = 0; i < hierarchy.size(); i++) {
                Class<?> candidate = hierarchy.get(i);
                Class<?> candidate2 = array ? candidate.componentType() : ClassUtils.resolvePrimitiveIfNecessary(candidate);
                Class<?> superclass = candidate2.getSuperclass();
                if (superclass != null && superclass != Object.class && superclass != Enum.class) {
                    addToClassHierarchy(i + 1, candidate2.getSuperclass(), array, hierarchy, visited);
                }
                addInterfacesToClassHierarchy(candidate2, array, hierarchy, visited);
            }
            if (Enum.class.isAssignableFrom(type)) {
                addToClassHierarchy(hierarchy.size(), Enum.class, array, hierarchy, visited);
                addToClassHierarchy(hierarchy.size(), Enum.class, false, hierarchy, visited);
                addInterfacesToClassHierarchy(Enum.class, array, hierarchy, visited);
            }
            addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
            addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
            return hierarchy;
        }

        private void addInterfacesToClassHierarchy(Class<?> type, boolean asArray, List<Class<?>> hierarchy, Set<Class<?>> visited) {
            for (Class<?> implementedInterface : type.getInterfaces()) {
                addToClassHierarchy(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
            }
        }

        private void addToClassHierarchy(int index, Class<?> type, boolean asArray, List<Class<?>> hierarchy, Set<Class<?>> visited) {
            if (asArray) {
                type = type.arrayType();
            }
            if (visited.add(type)) {
                hierarchy.add(index, type);
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ConversionService converters =\n");
            for (String converterString : getConverterStrings()) {
                builder.append('\t').append(converterString).append('\n');
            }
            return builder.toString();
        }

        private List<String> getConverterStrings() {
            List<String> converterStrings = new ArrayList<>();
            for (ConvertersForPair convertersForPair : this.converters.values()) {
                converterStrings.add(convertersForPair.toString());
            }
            Collections.sort(converterStrings);
            return converterStrings;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$ConvertersForPair.class */
    public static class ConvertersForPair {
        private final Deque<GenericConverter> converters = new ConcurrentLinkedDeque();

        private ConvertersForPair() {
        }

        public void add(GenericConverter converter) {
            this.converters.addFirst(converter);
        }

        @Nullable
        public GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
            for (GenericConverter converter : this.converters) {
                if (converter instanceof ConditionalGenericConverter) {
                    ConditionalGenericConverter genericConverter = (ConditionalGenericConverter) converter;
                    if (genericConverter.matches(sourceType, targetType)) {
                    }
                }
                return converter;
            }
            return null;
        }

        public String toString() {
            return StringUtils.collectionToCommaDelimitedString(this.converters);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/GenericConversionService$NoOpConverter.class */
    private static class NoOpConverter implements GenericConverter {
        private final String name;

        public NoOpConverter(String name) {
            this.name = name;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return null;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return source;
        }

        public String toString() {
            return this.name;
        }
    }
}
