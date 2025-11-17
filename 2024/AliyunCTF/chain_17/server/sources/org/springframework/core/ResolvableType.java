package org.springframework.core;

import ch.qos.logback.core.CoreConstants;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;
import org.springframework.core.SerializableTypeWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType.class */
public class ResolvableType implements Serializable {
    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, (SerializableTypeWrapper.TypeProvider) null, (VariableResolver) null, (Integer) 0);
    private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache = new ConcurrentReferenceHashMap<>(256);
    private final Type type;

    @Nullable
    private final ResolvableType componentType;

    @Nullable
    private final SerializableTypeWrapper.TypeProvider typeProvider;

    @Nullable
    private final VariableResolver variableResolver;

    @Nullable
    private final Integer hash;

    @Nullable
    private Class<?> resolved;

    @Nullable
    private volatile ResolvableType superType;

    @Nullable
    private volatile ResolvableType[] interfaces;

    @Nullable
    private volatile ResolvableType[] generics;

    @Nullable
    private volatile Boolean unresolvableGenerics;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$VariableResolver.class */
    public interface VariableResolver extends Serializable {
        Object getSource();

        @Nullable
        ResolvableType resolveVariable(TypeVariable<?> variable);
    }

    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {
        this.type = type;
        this.componentType = null;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.hash = Integer.valueOf(calculateHashCode());
        this.resolved = null;
    }

    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver, @Nullable Integer hash) {
        this.type = type;
        this.componentType = null;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.hash = hash;
        this.resolved = resolveClass();
    }

    private ResolvableType(Type type, @Nullable ResolvableType componentType, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {
        this.type = type;
        this.componentType = componentType;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.hash = null;
        this.resolved = resolveClass();
    }

    private ResolvableType(@Nullable Class<?> clazz) {
        this.resolved = clazz != null ? clazz : Object.class;
        this.type = this.resolved;
        this.componentType = null;
        this.typeProvider = null;
        this.variableResolver = null;
        this.hash = null;
    }

    public Type getType() {
        return SerializableTypeWrapper.unwrap(this.type);
    }

    @Nullable
    public Class<?> getRawClass() {
        if (this.type == this.resolved) {
            return this.resolved;
        }
        Type rawType = this.type;
        if (rawType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) rawType;
            rawType = parameterizedType.getRawType();
        }
        if (!(rawType instanceof Class)) {
            return null;
        }
        Class<?> rawClass = (Class) rawType;
        return rawClass;
    }

    public Object getSource() {
        Object source = this.typeProvider != null ? this.typeProvider.getSource() : null;
        return source != null ? source : this.type;
    }

    public Class<?> toClass() {
        return resolve(Object.class);
    }

    public boolean isInstance(@Nullable Object obj) {
        return obj != null && isAssignableFrom(obj.getClass());
    }

    public boolean isAssignableFrom(Class<?> other) {
        Type type = this.type;
        if (!(type instanceof Class)) {
            return isAssignableFrom(forClass(other), false, null);
        }
        Class<?> clazz = (Class) type;
        return ClassUtils.isAssignable(clazz, other);
    }

    public boolean isAssignableFrom(ResolvableType other) {
        return isAssignableFrom(other, false, null);
    }

    private boolean isAssignableFrom(ResolvableType other, boolean strict, @Nullable Map<Type, Type> matchedBefore) {
        ResolvableType resolved;
        ResolvableType resolved2;
        Assert.notNull(other, "ResolvableType must not be null");
        if (this == NONE || other == NONE) {
            return false;
        }
        if (matchedBefore != null) {
            if (matchedBefore.get(this.type) == other.type) {
                return true;
            }
        } else {
            Type type = this.type;
            if (type instanceof Class) {
                Class<?> clazz = (Class) type;
                Type type2 = other.type;
                if (type2 instanceof Class) {
                    Class<?> otherClazz = (Class) type2;
                    return strict ? clazz.isAssignableFrom(otherClazz) : ClassUtils.isAssignable(clazz, otherClazz);
                }
            }
        }
        if (isArray()) {
            return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType(), true, matchedBefore);
        }
        WildcardBounds ourBounds = WildcardBounds.get(this);
        WildcardBounds typeBounds = WildcardBounds.get(other);
        if (typeBounds != null) {
            return ourBounds != null && ourBounds.isSameKind(typeBounds) && ourBounds.isAssignableFrom(typeBounds.getBounds());
        }
        if (ourBounds != null) {
            return ourBounds.isAssignableFrom(other);
        }
        boolean exactMatch = matchedBefore != null;
        boolean checkGenerics = true;
        Class<?> ourResolved = null;
        Type type3 = this.type;
        if (type3 instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) type3;
            if (this.variableResolver != null && (resolved2 = this.variableResolver.resolveVariable(variable)) != null) {
                ourResolved = resolved2.resolve();
            }
            if (ourResolved == null && other.variableResolver != null && (resolved = other.variableResolver.resolveVariable(variable)) != null) {
                ourResolved = resolved.resolve();
                checkGenerics = false;
            }
            if (ourResolved == null) {
                exactMatch = false;
            }
        }
        if (ourResolved == null) {
            ourResolved = toClass();
        }
        Class<?> otherResolved = other.toClass();
        if (exactMatch) {
            if (!ourResolved.equals(otherResolved)) {
                return false;
            }
        } else if (strict) {
            if (!ourResolved.isAssignableFrom(otherResolved)) {
                return false;
            }
        } else if (!ClassUtils.isAssignable(ourResolved, otherResolved)) {
            return false;
        }
        if (checkGenerics) {
            ResolvableType[] ourGenerics = getGenerics();
            ResolvableType[] typeGenerics = other.as(ourResolved).getGenerics();
            if (ourGenerics.length != typeGenerics.length) {
                return false;
            }
            if (ourGenerics.length > 0) {
                if (matchedBefore == null) {
                    matchedBefore = new IdentityHashMap(1);
                }
                matchedBefore.put(this.type, other.type);
                for (int i = 0; i < ourGenerics.length; i++) {
                    if (!ourGenerics[i].isAssignableFrom(typeGenerics[i], true, matchedBefore)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x001e, code lost:            if (r0.isArray() == false) goto L10;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isArray() {
        /*
            r3 = this;
            r0 = r3
            org.springframework.core.ResolvableType r1 = org.springframework.core.ResolvableType.NONE
            if (r0 != r1) goto L9
            r0 = 0
            return r0
        L9:
            r0 = r3
            java.lang.reflect.Type r0 = r0.type
            r5 = r0
            r0 = r5
            boolean r0 = r0 instanceof java.lang.Class
            if (r0 == 0) goto L21
            r0 = r5
            java.lang.Class r0 = (java.lang.Class) r0
            r4 = r0
            r0 = r4
            boolean r0 = r0.isArray()
            if (r0 != 0) goto L35
        L21:
            r0 = r3
            java.lang.reflect.Type r0 = r0.type
            boolean r0 = r0 instanceof java.lang.reflect.GenericArrayType
            if (r0 != 0) goto L35
            r0 = r3
            org.springframework.core.ResolvableType r0 = r0.resolveType()
            boolean r0 = r0.isArray()
            if (r0 == 0) goto L39
        L35:
            r0 = 1
            goto L3a
        L39:
            r0 = 0
        L3a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.ResolvableType.isArray():boolean");
    }

    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        Type type = this.type;
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            Class<?> componentType = clazz.componentType();
            return forType(componentType, this.variableResolver);
        }
        Type type2 = this.type;
        if (type2 instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type2;
            return forType(genericArrayType.getGenericComponentType(), this.variableResolver);
        }
        return resolveType().getComponentType();
    }

    public ResolvableType asCollection() {
        return as(Collection.class);
    }

    public ResolvableType asMap() {
        return as(Map.class);
    }

    public ResolvableType as(Class<?> type) {
        if (this == NONE) {
            return NONE;
        }
        Class<?> resolved = resolve();
        if (resolved == null || resolved == type) {
            return this;
        }
        for (ResolvableType interfaceType : getInterfaces()) {
            ResolvableType interfaceAsType = interfaceType.as(type);
            if (interfaceAsType != NONE) {
                return interfaceAsType;
            }
        }
        return getSuperType().as(type);
    }

    public ResolvableType getSuperType() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return NONE;
        }
        try {
            Type superclass = resolved.getGenericSuperclass();
            if (superclass == null) {
                return NONE;
            }
            ResolvableType superType = this.superType;
            if (superType == null) {
                superType = forType(superclass, this);
                this.superType = superType;
            }
            return superType;
        } catch (TypeNotPresentException e) {
            return NONE;
        }
    }

    public ResolvableType[] getInterfaces() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] interfaces = this.interfaces;
        if (interfaces == null) {
            Type[] genericIfcs = resolved.getGenericInterfaces();
            interfaces = new ResolvableType[genericIfcs.length];
            for (int i = 0; i < genericIfcs.length; i++) {
                interfaces[i] = forType(genericIfcs[i], this);
            }
            this.interfaces = interfaces;
        }
        return interfaces;
    }

    public boolean hasGenerics() {
        return getGenerics().length > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEntirelyUnresolvable() {
        if (this == NONE) {
            return false;
        }
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (!generic.isUnresolvableTypeVariable() && !generic.isWildcardWithoutBounds()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasUnresolvableGenerics() {
        if (this == NONE) {
            return false;
        }
        Boolean unresolvableGenerics = this.unresolvableGenerics;
        if (unresolvableGenerics == null) {
            unresolvableGenerics = Boolean.valueOf(determineUnresolvableGenerics());
            this.unresolvableGenerics = unresolvableGenerics;
        }
        return unresolvableGenerics.booleanValue();
    }

    private boolean determineUnresolvableGenerics() {
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (generic.isUnresolvableTypeVariable() || generic.isWildcardWithoutBounds()) {
                return true;
            }
        }
        Class<?> resolved = resolve();
        if (resolved != null) {
            try {
                for (Type genericInterface : resolved.getGenericInterfaces()) {
                    if (genericInterface instanceof Class) {
                        Class<?> clazz = (Class) genericInterface;
                        if (clazz.getTypeParameters().length > 0) {
                            return true;
                        }
                    }
                }
            } catch (TypeNotPresentException e) {
            }
            Class<?> superclass = resolved.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return getSuperType().hasUnresolvableGenerics();
            }
            return false;
        }
        return false;
    }

    private boolean isUnresolvableTypeVariable() {
        ResolvableType resolved;
        Type type = this.type;
        if (type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) type;
            if (this.variableResolver == null || (resolved = this.variableResolver.resolveVariable(variable)) == null || resolved.isUnresolvableTypeVariable()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isWildcardWithoutBounds() {
        Type type = this.type;
        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            if (wt.getLowerBounds().length == 0) {
                Type[] upperBounds = wt.getUpperBounds();
                if (upperBounds.length == 0) {
                    return true;
                }
                if (upperBounds.length == 1 && Object.class == upperBounds[0]) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public ResolvableType getNested(int nestingLevel) {
        return getNested(nestingLevel, null);
    }

    public ResolvableType getNested(int nestingLevel, @Nullable Map<Integer, Integer> typeIndexesPerLevel) {
        ResolvableType generic;
        ResolvableType result = this;
        for (int i = 2; i <= nestingLevel; i++) {
            if (result.isArray()) {
                generic = result.getComponentType();
            } else {
                while (result != NONE && !result.hasGenerics()) {
                    result = result.getSuperType();
                }
                Integer index = typeIndexesPerLevel != null ? typeIndexesPerLevel.get(Integer.valueOf(i)) : null;
                generic = result.getGeneric(Integer.valueOf(index == null ? result.getGenerics().length - 1 : index.intValue()).intValue());
            }
            result = generic;
        }
        return result;
    }

    public ResolvableType getGeneric(@Nullable int... indexes) {
        ResolvableType[] generics = getGenerics();
        if (indexes == null || indexes.length == 0) {
            return generics.length == 0 ? NONE : generics[0];
        }
        ResolvableType generic = this;
        for (int index : indexes) {
            ResolvableType[] generics2 = generic.getGenerics();
            if (index < 0 || index >= generics2.length) {
                return NONE;
            }
            generic = generics2[index];
        }
        return generic;
    }

    public ResolvableType[] getGenerics() {
        if (this == NONE) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] generics = this.generics;
        if (generics == null) {
            Type type = this.type;
            if (type instanceof Class) {
                Class<?> clazz = (Class) type;
                Type[] typeParams = clazz.getTypeParameters();
                generics = new ResolvableType[typeParams.length];
                for (int i = 0; i < generics.length; i++) {
                    generics[i] = forType(typeParams[i], this);
                }
            } else {
                Type type2 = this.type;
                if (type2 instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type2;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    generics = new ResolvableType[actualTypeArguments.length];
                    for (int i2 = 0; i2 < actualTypeArguments.length; i2++) {
                        generics[i2] = forType(actualTypeArguments[i2], this.variableResolver);
                    }
                } else {
                    generics = resolveType().getGenerics();
                }
            }
            this.generics = generics;
        }
        return generics;
    }

    public Class<?>[] resolveGenerics() {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve();
        }
        return resolvedGenerics;
    }

    public Class<?>[] resolveGenerics(Class<?> fallback) {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve(fallback);
        }
        return resolvedGenerics;
    }

    @Nullable
    public Class<?> resolveGeneric(int... indexes) {
        return getGeneric(indexes).resolve();
    }

    @Nullable
    public Class<?> resolve() {
        return this.resolved;
    }

    public Class<?> resolve(Class<?> fallback) {
        return this.resolved != null ? this.resolved : fallback;
    }

    @Nullable
    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        Type type = this.type;
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            return clazz;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            if (resolvedComponent != null) {
                return Array.newInstance(resolvedComponent, 0).getClass();
            }
            return null;
        }
        return resolveType().resolve();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolvableType resolveType() {
        ResolvableType resolved;
        Type type = this.type;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return forType(parameterizedType.getRawType(), this.variableResolver);
        }
        Type type2 = this.type;
        if (type2 instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type2;
            Type resolved2 = resolveBounds(wildcardType.getUpperBounds());
            if (resolved2 == null) {
                resolved2 = resolveBounds(wildcardType.getLowerBounds());
            }
            return forType(resolved2, this.variableResolver);
        }
        Type type3 = this.type;
        if (type3 instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) type3;
            if (this.variableResolver != null && (resolved = this.variableResolver.resolveVariable(variable)) != null) {
                return resolved;
            }
            return forType(resolveBounds(variable.getBounds()), this.variableResolver);
        }
        return NONE;
    }

    @Nullable
    private Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        return bounds[0];
    }

    @Nullable
    private ResolvableType resolveVariable(TypeVariable<?> variable) {
        ResolvableType resolved;
        if (this.type instanceof TypeVariable) {
            return resolveType().resolveVariable(variable);
        }
        Type type = this.type;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> resolved2 = resolve();
            if (resolved2 == null) {
                return null;
            }
            TypeVariable<?>[] variables = resolved2.getTypeParameters();
            for (int i = 0; i < variables.length; i++) {
                if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
                    Type actualType = parameterizedType.getActualTypeArguments()[i];
                    return forType(actualType, this.variableResolver);
                }
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                return forType(ownerType, this.variableResolver).resolveVariable(variable);
            }
        }
        if ((this.type instanceof WildcardType) && (resolved = resolveType().resolveVariable(variable)) != null) {
            return resolved;
        }
        if (this.variableResolver != null) {
            return this.variableResolver.resolveVariable(variable);
        }
        return null;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        ResolvableType otherType = (ResolvableType) other;
        if (!equalsType(otherType)) {
            return false;
        }
        if (this.typeProvider != otherType.typeProvider && (this.typeProvider == null || otherType.typeProvider == null || !ObjectUtils.nullSafeEquals(this.typeProvider.getType(), otherType.typeProvider.getType()))) {
            return false;
        }
        if (this.variableResolver != otherType.variableResolver) {
            if (this.variableResolver == null || otherType.variableResolver == null || !ObjectUtils.nullSafeEquals(this.variableResolver.getSource(), otherType.variableResolver.getSource())) {
                return false;
            }
            return true;
        }
        return true;
    }

    public boolean equalsType(ResolvableType otherType) {
        return ObjectUtils.nullSafeEquals(this.type, otherType.type) && ObjectUtils.nullSafeEquals(this.componentType, otherType.componentType);
    }

    public int hashCode() {
        return this.hash != null ? this.hash.intValue() : calculateHashCode();
    }

    private int calculateHashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.type);
        if (this.componentType != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.componentType);
        }
        if (this.typeProvider != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.typeProvider.getType());
        }
        if (this.variableResolver != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.variableResolver.getSource());
        }
        return hashCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public VariableResolver asVariableResolver() {
        if (this == NONE) {
            return null;
        }
        return new DefaultVariableResolver(this);
    }

    private Object readResolve() {
        return this.type == EmptyType.INSTANCE ? NONE : this;
    }

    public String toString() {
        if (isArray()) {
            return getComponentType() + "[]";
        }
        if (this.resolved == null) {
            return CoreConstants.NA;
        }
        Type type = this.type;
        if (type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) type;
            if (this.variableResolver == null || this.variableResolver.resolveVariable(variable) == null) {
                return CoreConstants.NA;
            }
        }
        if (hasGenerics()) {
            return this.resolved.getName() + "<" + StringUtils.arrayToDelimitedString(getGenerics(), ", ") + ">";
        }
        return this.resolved.getName();
    }

    public static ResolvableType forClass(@Nullable Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    public static ResolvableType forRawClass(@Nullable final Class<?> clazz) {
        return new ResolvableType(clazz) { // from class: org.springframework.core.ResolvableType.1
            @Override // org.springframework.core.ResolvableType
            public ResolvableType[] getGenerics() {
                return ResolvableType.EMPTY_TYPES_ARRAY;
            }

            @Override // org.springframework.core.ResolvableType
            public boolean isAssignableFrom(Class<?> other) {
                return clazz == null || ClassUtils.isAssignable(clazz, other);
            }

            @Override // org.springframework.core.ResolvableType
            public boolean isAssignableFrom(ResolvableType other) {
                Class<?> otherClass = other.resolve();
                return otherClass != null && (clazz == null || ClassUtils.isAssignable(clazz, otherClass));
            }
        };
    }

    public static ResolvableType forClass(Class<?> baseType, Class<?> implementationClass) {
        Assert.notNull(baseType, "Base type must not be null");
        ResolvableType asType = forType(implementationClass).as(baseType);
        return asType == NONE ? forType(baseType) : asType;
    }

    public static ResolvableType forClassWithGenerics(Class<?> clazz, Class<?>... generics) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(generics, "Generics array must not be null");
        ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvableGenerics[i] = forClass(generics[i]);
        }
        return forClassWithGenerics(clazz, resolvableGenerics);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.reflect.Type] */
    public static ResolvableType forClassWithGenerics(Class<?> cls, ResolvableType... resolvableTypeArr) {
        Assert.notNull(cls, "Class must not be null");
        Assert.notNull(resolvableTypeArr, "Generics array must not be null");
        TypeVariable<Class<?>>[] typeParameters = cls.getTypeParameters();
        Assert.isTrue(typeParameters.length == resolvableTypeArr.length, (Supplier<String>) () -> {
            return "Mismatched number of generics specified for " + cls.toGenericString();
        });
        Type[] typeArr = new Type[resolvableTypeArr.length];
        for (int i = 0; i < resolvableTypeArr.length; i++) {
            ResolvableType resolvableType = resolvableTypeArr[i];
            TypeVariable<Class<?>> type = resolvableType != null ? resolvableType.getType() : null;
            typeArr[i] = (type == null || (type instanceof TypeVariable)) ? typeParameters[i] : type;
        }
        return forType(new SyntheticParameterizedType(cls, typeArr), new TypeVariablesVariableResolver(typeParameters, resolvableTypeArr));
    }

    public static ResolvableType forInstance(@Nullable Object instance) {
        if (instance instanceof ResolvableTypeProvider) {
            ResolvableTypeProvider resolvableTypeProvider = (ResolvableTypeProvider) instance;
            ResolvableType type = resolvableTypeProvider.getResolvableType();
            if (type != null) {
                return type;
            }
        }
        return instance != null ? forClass(instance.getClass()) : NONE;
    }

    public static ResolvableType forField(Field field) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null);
    }

    public static ResolvableType forField(Field field, Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver());
    }

    public static ResolvableType forField(Field field, @Nullable ResolvableType implementationType) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = implementationType != null ? implementationType : NONE;
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.as(field.getDeclaringClass()).asVariableResolver());
    }

    public static ResolvableType forField(Field field, int nestingLevel) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null).getNested(nestingLevel);
    }

    public static ResolvableType forField(Field field, int nestingLevel, @Nullable Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver()).getNested(nestingLevel);
    }

    public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex) {
        Assert.notNull(constructor, "Constructor must not be null");
        return forMethodParameter(new MethodParameter(constructor, parameterIndex));
    }

    public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex, Class<?> implementationClass) {
        Assert.notNull(constructor, "Constructor must not be null");
        MethodParameter methodParameter = new MethodParameter(constructor, parameterIndex, implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodReturnType(Method method) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(new MethodParameter(method, -1));
    }

    public static ResolvableType forMethodReturnType(Method method, Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        MethodParameter methodParameter = new MethodParameter(method, -1, implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(Method method, int parameterIndex) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(new MethodParameter(method, parameterIndex));
    }

    public static ResolvableType forMethodParameter(Method method, int parameterIndex, Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        MethodParameter methodParameter = new MethodParameter(method, parameterIndex, implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter) {
        return forMethodParameter(methodParameter, (Type) null);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable ResolvableType implementationType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        ResolvableType owner = (implementationType != null ? implementationType : forType(methodParameter.getContainingClass())).as(methodParameter.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable Type targetType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        return forMethodParameter(methodParameter, targetType, methodParameter.getNestingLevel());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable Type targetType, int nestingLevel) {
        ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
        return forType(targetType, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(nestingLevel, methodParameter.typeIndexesPerLevel);
    }

    public static ResolvableType forArrayComponent(ResolvableType componentType) {
        Assert.notNull(componentType, "Component type must not be null");
        Class<?> arrayType = componentType.resolve().arrayType();
        return new ResolvableType(arrayType, componentType, (SerializableTypeWrapper.TypeProvider) null, (VariableResolver) null);
    }

    public static ResolvableType forType(@Nullable Type type) {
        return forType(type, null, null);
    }

    public static ResolvableType forType(@Nullable Type type, @Nullable ResolvableType owner) {
        VariableResolver variableResolver = null;
        if (owner != null) {
            variableResolver = owner.asVariableResolver();
        }
        return forType(type, variableResolver);
    }

    public static ResolvableType forType(ParameterizedTypeReference<?> typeReference) {
        return forType(typeReference.getType(), null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ResolvableType forType(@Nullable Type type, @Nullable VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }

    static ResolvableType forType(@Nullable Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {
        if (type == null && typeProvider != null) {
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return NONE;
        }
        if (type instanceof Class) {
            return new ResolvableType(type, (ResolvableType) null, typeProvider, variableResolver);
        }
        cache.purgeUnreferencedEntries();
        ResolvableType resultType = new ResolvableType(type, typeProvider, variableResolver);
        ResolvableType cachedType = cache.get(resultType);
        if (cachedType == null) {
            cachedType = new ResolvableType(type, typeProvider, variableResolver, resultType.hash);
            cache.put(cachedType, cachedType);
        }
        resultType.resolved = cachedType.resolved;
        return resultType;
    }

    public static void clearCache() {
        cache.clear();
        SerializableTypeWrapper.cache.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$DefaultVariableResolver.class */
    public static class DefaultVariableResolver implements VariableResolver {
        private final ResolvableType source;

        DefaultVariableResolver(ResolvableType resolvableType) {
            this.source = resolvableType;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        @Nullable
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            return this.source.resolveVariable(variable);
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        public Object getSource() {
            return this.source;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$TypeVariablesVariableResolver.class */
    public static class TypeVariablesVariableResolver implements VariableResolver {
        private final TypeVariable<?>[] variables;
        private final ResolvableType[] generics;

        public TypeVariablesVariableResolver(TypeVariable<?>[] variables, ResolvableType[] generics) {
            this.variables = variables;
            this.generics = generics;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        @Nullable
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            TypeVariable<?> variableToCompare = (TypeVariable) SerializableTypeWrapper.unwrap(variable);
            for (int i = 0; i < this.variables.length; i++) {
                TypeVariable<?> resolvedVariable = (TypeVariable) SerializableTypeWrapper.unwrap(this.variables[i]);
                if (ObjectUtils.nullSafeEquals(resolvedVariable, variableToCompare)) {
                    return this.generics[i];
                }
            }
            return null;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        public Object getSource() {
            return this.generics;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$SyntheticParameterizedType.class */
    public static final class SyntheticParameterizedType implements ParameterizedType, Serializable {
        private final Type rawType;
        private final Type[] typeArguments;

        public SyntheticParameterizedType(Type rawType, Type[] typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override // java.lang.reflect.Type
        public String getTypeName() {
            String typeName = this.rawType.getTypeName();
            if (this.typeArguments.length > 0) {
                StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
                for (Type argument : this.typeArguments) {
                    stringJoiner.add(argument.getTypeName());
                }
                return typeName + stringJoiner;
            }
            return typeName;
        }

        @Override // java.lang.reflect.ParameterizedType
        @Nullable
        public Type getOwnerType() {
            return null;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getRawType() {
            return this.rawType;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type[] getActualTypeArguments() {
            return this.typeArguments;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ParameterizedType) {
                    ParameterizedType that = (ParameterizedType) other;
                    if (that.getOwnerType() != null || !this.rawType.equals(that.getRawType()) || !Arrays.equals(this.typeArguments, that.getActualTypeArguments())) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.rawType.hashCode() * 31) + Arrays.hashCode(this.typeArguments);
        }

        public String toString() {
            return getTypeName();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$WildcardBounds.class */
    public static class WildcardBounds {
        private final Kind kind;
        private final ResolvableType[] bounds;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$WildcardBounds$Kind.class */
        public enum Kind {
            UPPER,
            LOWER
        }

        public WildcardBounds(Kind kind, ResolvableType[] bounds) {
            this.kind = kind;
            this.bounds = bounds;
        }

        public boolean isSameKind(WildcardBounds bounds) {
            return this.kind == bounds.kind;
        }

        public boolean isAssignableFrom(ResolvableType... types) {
            for (ResolvableType bound : this.bounds) {
                for (ResolvableType type : types) {
                    if (!isAssignable(bound, type)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isAssignable(ResolvableType source, ResolvableType from) {
            return this.kind == Kind.UPPER ? source.isAssignableFrom(from) : from.isAssignableFrom(source);
        }

        public ResolvableType[] getBounds() {
            return this.bounds;
        }

        @Nullable
        public static WildcardBounds get(ResolvableType type) {
            ResolvableType resolvableType = type;
            while (true) {
                ResolvableType resolveToWildcard = resolvableType;
                Type type2 = resolveToWildcard.getType();
                if (!(type2 instanceof WildcardType)) {
                    if (resolveToWildcard == ResolvableType.NONE) {
                        return null;
                    }
                    resolvableType = resolveToWildcard.resolveType();
                } else {
                    WildcardType wildcardType = (WildcardType) type2;
                    Kind boundsType = wildcardType.getLowerBounds().length > 0 ? Kind.LOWER : Kind.UPPER;
                    Type[] bounds = boundsType == Kind.UPPER ? wildcardType.getUpperBounds() : wildcardType.getLowerBounds();
                    ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
                    for (int i = 0; i < bounds.length; i++) {
                        resolvableBounds[i] = ResolvableType.forType(bounds[i], type.variableResolver);
                    }
                    return new WildcardBounds(boundsType, resolvableBounds);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ResolvableType$EmptyType.class */
    public static class EmptyType implements Type, Serializable {
        static final Type INSTANCE = new EmptyType();

        EmptyType() {
        }

        Object readResolve() {
            return INSTANCE;
        }
    }
}
