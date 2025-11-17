package cn.hutool.core.util;

import cn.hutool.core.lang.ParameterizedTypeImpl;
import cn.hutool.core.lang.reflect.ActualTypeMapperPool;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/TypeUtil.class */
public class TypeUtil {
    public static Class<?> getClass(Type type) {
        if (null != type) {
            if (type instanceof Class) {
                return (Class) type;
            }
            if (type instanceof ParameterizedType) {
                return (Class) ((ParameterizedType) type).getRawType();
            }
            if (type instanceof TypeVariable) {
                return (Class) ((TypeVariable) type).getBounds()[0];
            }
            if (type instanceof WildcardType) {
                Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getClass(upperBounds[0]);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public static Type getType(Field field) {
        if (null == field) {
            return null;
        }
        return field.getGenericType();
    }

    public static Type getFieldType(Class<?> clazz, String fieldName) {
        return getType(ReflectUtil.getField(clazz, fieldName));
    }

    public static Class<?> getClass(Field field) {
        if (null == field) {
            return null;
        }
        return field.getType();
    }

    public static Type getFirstParamType(Method method) {
        return getParamType(method, 0);
    }

    public static Class<?> getFirstParamClass(Method method) {
        return getParamClass(method, 0);
    }

    public static Type getParamType(Method method, int index) {
        Type[] types = getParamTypes(method);
        if (null != types && types.length > index) {
            return types[index];
        }
        return null;
    }

    public static Class<?> getParamClass(Method method, int index) {
        Class<?>[] classes = getParamClasses(method);
        if (null != classes && classes.length > index) {
            return classes[index];
        }
        return null;
    }

    public static Type[] getParamTypes(Method method) {
        if (null == method) {
            return null;
        }
        return method.getGenericParameterTypes();
    }

    public static Class<?>[] getParamClasses(Method method) {
        if (null == method) {
            return null;
        }
        return method.getParameterTypes();
    }

    public static Type getReturnType(Method method) {
        if (null == method) {
            return null;
        }
        return method.getGenericReturnType();
    }

    public static Class<?> getReturnClass(Method method) {
        if (null == method) {
            return null;
        }
        return method.getReturnType();
    }

    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    public static Type getTypeArgument(Type type, int index) {
        Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    public static Type[] getTypeArguments(Type type) {
        ParameterizedType parameterizedType;
        if (null == type || null == (parameterizedType = toParameterizedType(type))) {
            return null;
        }
        return parameterizedType.getActualTypeArguments();
    }

    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (ArrayUtil.isNotEmpty((Object[]) genericInterfaces)) {
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }

    public static boolean isUnknown(Type type) {
        return null == type || (type instanceof TypeVariable);
    }

    public static boolean hasTypeVariable(Type... types) {
        for (Type type : types) {
            if (type instanceof TypeVariable) {
                return true;
            }
        }
        return false;
    }

    public static Map<Type, Type> getTypeMap(Class<?> clazz) {
        return ActualTypeMapperPool.get(clazz);
    }

    public static Type getActualType(Type type, Field field) {
        if (null == field) {
            return null;
        }
        return getActualType((Type) ObjectUtil.defaultIfNull((Class<?>) type, field.getDeclaringClass()), field.getGenericType());
    }

    public static Type getActualType(Type type, Type typeVariable) {
        if (typeVariable instanceof ParameterizedType) {
            return getActualType(type, (ParameterizedType) typeVariable);
        }
        if (typeVariable instanceof TypeVariable) {
            return ActualTypeMapperPool.getActualType(type, (TypeVariable) typeVariable);
        }
        return typeVariable;
    }

    public static Type getActualType(Type type, ParameterizedType parameterizedType) {
        if (hasTypeVariable(parameterizedType.getActualTypeArguments())) {
            Type[] actualTypeArguments = getActualTypes(type, parameterizedType.getActualTypeArguments());
            if (ArrayUtil.isNotEmpty((Object[]) actualTypeArguments)) {
                parameterizedType = new ParameterizedTypeImpl(actualTypeArguments, parameterizedType.getOwnerType(), parameterizedType.getRawType());
            }
        }
        return parameterizedType;
    }

    public static Type[] getActualTypes(Type type, Type... typeVariables) {
        return ActualTypeMapperPool.getActualTypes(type, typeVariables);
    }
}
