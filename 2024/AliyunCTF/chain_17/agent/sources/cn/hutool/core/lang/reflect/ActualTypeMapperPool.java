package cn.hutool.core.lang.reflect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/reflect/ActualTypeMapperPool.class */
public class ActualTypeMapperPool {
    private static final WeakConcurrentMap<Type, Map<Type, Type>> CACHE = new WeakConcurrentMap<>();

    public static Map<Type, Type> get(Type type) {
        return CACHE.computeIfAbsent((WeakConcurrentMap<Type, Map<Type, Type>>) type, (Function<? super WeakConcurrentMap<Type, Map<Type, Type>>, ? extends Map<Type, Type>>) key -> {
            return createTypeMap(type);
        });
    }

    public static Map<String, Type> getStrKeyMap(Type type) {
        return Convert.toMap(String.class, Type.class, get(type));
    }

    public static Type getActualType(Type type, TypeVariable<?> typeVariable) {
        Map<Type, Type> typeTypeMap = get(type);
        Type type2 = typeTypeMap.get(typeVariable);
        while (true) {
            Type result = type2;
            if (result instanceof TypeVariable) {
                type2 = typeTypeMap.get(result);
            } else {
                return result;
            }
        }
    }

    public static Type[] getActualTypes(Type type, Type... typeVariables) {
        Type[] result = new Type[typeVariables.length];
        for (int i = 0; i < typeVariables.length; i++) {
            result[i] = typeVariables[i] instanceof TypeVariable ? getActualType(type, (TypeVariable) typeVariables[i]) : typeVariables[i];
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<Type, Type> createTypeMap(Type type) {
        ParameterizedType parameterizedType;
        Map<Type, Type> typeMap = new HashMap<>();
        while (null != type && null != (parameterizedType = TypeUtil.toParameterizedType(type))) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            Class<?> rawType = (Class) parameterizedType.getRawType();
            Type[] typeParameters = rawType.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                Type value = typeArguments[i];
                if (false == (value instanceof TypeVariable)) {
                    typeMap.put(typeParameters[i], value);
                }
            }
            type = rawType;
        }
        return typeMap;
    }
}
