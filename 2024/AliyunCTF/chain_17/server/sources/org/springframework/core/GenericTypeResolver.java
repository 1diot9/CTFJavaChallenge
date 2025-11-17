package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/GenericTypeResolver.class */
public final class GenericTypeResolver {
    private static final Map<Class<?>, Map<TypeVariable, Type>> typeVariableCache = new ConcurrentReferenceHashMap();

    private GenericTypeResolver() {
    }

    @Deprecated
    public static Class<?> resolveParameterType(MethodParameter methodParameter, Class<?> implementationClass) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        Assert.notNull(implementationClass, "Class must not be null");
        methodParameter.setContainingClass(implementationClass);
        return methodParameter.getParameterType();
    }

    public static Class<?> resolveReturnType(Method method, Class<?> clazz) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(clazz, "Class must not be null");
        return ResolvableType.forMethodReturnType(method, clazz).resolve(method.getReturnType());
    }

    @Nullable
    public static Class<?> resolveReturnTypeArgument(Method method, Class<?> genericType) {
        Assert.notNull(method, "Method must not be null");
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method).as(genericType);
        if (!resolvableType.hasGenerics() || (resolvableType.getType() instanceof WildcardType)) {
            return null;
        }
        return getSingleGeneric(resolvableType);
    }

    @Nullable
    public static Class<?> resolveTypeArgument(Class<?> clazz, Class<?> genericType) {
        ResolvableType resolvableType = ResolvableType.forClass(clazz).as(genericType);
        if (!resolvableType.hasGenerics()) {
            return null;
        }
        return getSingleGeneric(resolvableType);
    }

    @Nullable
    private static Class<?> getSingleGeneric(ResolvableType resolvableType) {
        Assert.isTrue(resolvableType.getGenerics().length == 1, (Supplier<String>) () -> {
            return "Expected 1 type argument on generic interface [" + resolvableType + "] but found " + resolvableType.getGenerics().length;
        });
        return resolvableType.getGeneric(new int[0]).resolve();
    }

    @Nullable
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericType) {
        ResolvableType type = ResolvableType.forClass(clazz).as(genericType);
        if (!type.hasGenerics() || type.isEntirelyUnresolvable()) {
            return null;
        }
        return type.resolveGenerics(Object.class);
    }

    public static Type resolveType(Type genericType, @Nullable Class<?> contextClass) {
        Class<?> resolved;
        if (contextClass != null) {
            if (genericType instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable) genericType;
                ResolvableType resolvedTypeVariable = resolveVariable(typeVariable, ResolvableType.forClass(contextClass));
                if (resolvedTypeVariable != ResolvableType.NONE && (resolved = resolvedTypeVariable.resolve()) != null) {
                    return resolved;
                }
            } else if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                ResolvableType resolvedType = ResolvableType.forType(genericType);
                if (resolvedType.hasUnresolvableGenerics()) {
                    Class<?>[] generics = new Class[parameterizedType.getActualTypeArguments().length];
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    ResolvableType contextType = ResolvableType.forClass(contextClass);
                    for (int i = 0; i < typeArguments.length; i++) {
                        Type typeArgument = typeArguments[i];
                        if (typeArgument instanceof TypeVariable) {
                            TypeVariable<?> typeVariable2 = (TypeVariable) typeArgument;
                            ResolvableType resolvedTypeArgument = resolveVariable(typeVariable2, contextType);
                            if (resolvedTypeArgument != ResolvableType.NONE) {
                                generics[i] = resolvedTypeArgument.resolve();
                            } else {
                                generics[i] = ResolvableType.forType(typeArgument).resolve();
                            }
                        } else {
                            generics[i] = ResolvableType.forType(typeArgument).resolve();
                        }
                    }
                    Class<?> rawClass = resolvedType.getRawClass();
                    if (rawClass != null) {
                        return ResolvableType.forClassWithGenerics(rawClass, generics).getType();
                    }
                }
            }
        }
        return genericType;
    }

    private static ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
        ResolvableType resolvedType;
        if (contextType.hasGenerics()) {
            ResolvableType.VariableResolver variableResolver = contextType.asVariableResolver();
            if (variableResolver == null) {
                return ResolvableType.NONE;
            }
            ResolvableType resolvedType2 = variableResolver.resolveVariable(typeVariable);
            if (resolvedType2 != null) {
                return resolvedType2;
            }
        }
        ResolvableType superType = contextType.getSuperType();
        if (superType != ResolvableType.NONE && (resolvedType = resolveVariable(typeVariable, superType)) != ResolvableType.NONE) {
            return resolvedType;
        }
        for (ResolvableType ifc : contextType.getInterfaces()) {
            ResolvableType resolvedType3 = resolveVariable(typeVariable, ifc);
            if (resolvedType3 != ResolvableType.NONE) {
                return resolvedType3;
            }
        }
        return ResolvableType.NONE;
    }

    public static Class<?> resolveType(Type genericType, Map<TypeVariable, Type> map) {
        return ResolvableType.forType(genericType, new TypeVariableMapVariableResolver(map)).toClass();
    }

    public static Map<TypeVariable, Type> getTypeVariableMap(Class<?> clazz) {
        Map<TypeVariable, Type> typeVariableMap = typeVariableCache.get(clazz);
        if (typeVariableMap == null) {
            typeVariableMap = new HashMap();
            buildTypeVariableMap(ResolvableType.forClass(clazz), typeVariableMap);
            typeVariableCache.put(clazz, Collections.unmodifiableMap(typeVariableMap));
        }
        return typeVariableMap;
    }

    private static void buildTypeVariableMap(ResolvableType type, Map<TypeVariable, Type> typeVariableMap) {
        ResolvableType generic;
        if (type != ResolvableType.NONE) {
            Class<?> resolved = type.resolve();
            if (resolved != null && (type.getType() instanceof ParameterizedType)) {
                TypeVariable<?>[] variables = resolved.getTypeParameters();
                for (int i = 0; i < variables.length; i++) {
                    ResolvableType generic2 = type.getGeneric(i);
                    while (true) {
                        generic = generic2;
                        if (!(generic.getType() instanceof TypeVariable)) {
                            break;
                        } else {
                            generic2 = generic.resolveType();
                        }
                    }
                    if (generic != ResolvableType.NONE) {
                        typeVariableMap.put(variables[i], generic.getType());
                    }
                }
            }
            buildTypeVariableMap(type.getSuperType(), typeVariableMap);
            for (ResolvableType interfaceType : type.getInterfaces()) {
                buildTypeVariableMap(interfaceType, typeVariableMap);
            }
            if (resolved != null && resolved.isMemberClass()) {
                buildTypeVariableMap(ResolvableType.forClass(resolved.getEnclosingClass()), typeVariableMap);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/GenericTypeResolver$TypeVariableMapVariableResolver.class */
    private static class TypeVariableMapVariableResolver implements ResolvableType.VariableResolver {
        private final Map<TypeVariable, Type> typeVariableMap;

        public TypeVariableMapVariableResolver(Map<TypeVariable, Type> typeVariableMap) {
            this.typeVariableMap = typeVariableMap;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        @Nullable
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            Type type = this.typeVariableMap.get(variable);
            if (type != null) {
                return ResolvableType.forType(type);
            }
            return null;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        public Object getSource() {
            return this.typeVariableMap;
        }
    }
}
