package org.springframework.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/TypeUtils.class */
public abstract class TypeUtils {
    private static final Type[] IMPLICIT_LOWER_BOUNDS = {null};
    private static final Type[] IMPLICIT_UPPER_BOUNDS = {Object.class};

    public static boolean isAssignable(Type lhsType, Type rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.equals(rhsType) || Object.class == lhsType) {
            return true;
        }
        if (lhsType instanceof Class) {
            Class<?> lhsClass = (Class) lhsType;
            if (rhsType instanceof Class) {
                return ClassUtils.isAssignable(lhsClass, (Class) rhsType);
            }
            if (rhsType instanceof ParameterizedType) {
                ParameterizedType rhsParameterizedType = (ParameterizedType) rhsType;
                Type rhsRaw = rhsParameterizedType.getRawType();
                if (rhsRaw instanceof Class) {
                    Class<?> rhRawClass = (Class) rhsRaw;
                    return ClassUtils.isAssignable(lhsClass, rhRawClass);
                }
            } else if (lhsClass.isArray() && (rhsType instanceof GenericArrayType)) {
                GenericArrayType rhsGenericArrayType = (GenericArrayType) rhsType;
                Type rhsComponent = rhsGenericArrayType.getGenericComponentType();
                return isAssignable(lhsClass.componentType(), rhsComponent);
            }
        }
        if (lhsType instanceof ParameterizedType) {
            ParameterizedType lhsParameterizedType = (ParameterizedType) lhsType;
            if (rhsType instanceof Class) {
                Class<?> rhsClass = (Class) rhsType;
                Type lhsRaw = lhsParameterizedType.getRawType();
                if (lhsRaw instanceof Class) {
                    return ClassUtils.isAssignable((Class) lhsRaw, rhsClass);
                }
            } else if (rhsType instanceof ParameterizedType) {
                ParameterizedType rhsParameterizedType2 = (ParameterizedType) rhsType;
                return isAssignable(lhsParameterizedType, rhsParameterizedType2);
            }
        }
        if (lhsType instanceof GenericArrayType) {
            GenericArrayType lhsGenericArrayType = (GenericArrayType) lhsType;
            Type lhsComponent = lhsGenericArrayType.getGenericComponentType();
            if (rhsType instanceof Class) {
                Class<?> rhsClass2 = (Class) rhsType;
                if (rhsClass2.isArray()) {
                    return isAssignable(lhsComponent, rhsClass2.componentType());
                }
            }
            if (rhsType instanceof GenericArrayType) {
                GenericArrayType rhsGenericArrayType2 = (GenericArrayType) rhsType;
                Type rhsComponent2 = rhsGenericArrayType2.getGenericComponentType();
                return isAssignable(lhsComponent, rhsComponent2);
            }
        }
        if (lhsType instanceof WildcardType) {
            WildcardType lhsWildcardType = (WildcardType) lhsType;
            return isAssignable(lhsWildcardType, rhsType);
        }
        return false;
    }

    private static boolean isAssignable(ParameterizedType lhsType, ParameterizedType rhsType) {
        if (lhsType.equals(rhsType)) {
            return true;
        }
        Type[] lhsTypeArguments = lhsType.getActualTypeArguments();
        Type[] rhsTypeArguments = rhsType.getActualTypeArguments();
        if (lhsTypeArguments.length != rhsTypeArguments.length) {
            return false;
        }
        int size = lhsTypeArguments.length;
        for (int i = 0; i < size; i++) {
            Type lhsArg = lhsTypeArguments[i];
            Type rhsArg = rhsTypeArguments[i];
            if (!lhsArg.equals(rhsArg)) {
                if (lhsArg instanceof WildcardType) {
                    WildcardType wildcardType = (WildcardType) lhsArg;
                    if (!isAssignable(wildcardType, rhsArg)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isAssignable(WildcardType lhsType, Type rhsType) {
        Type[] lUpperBounds = getUpperBounds(lhsType);
        Type[] lLowerBounds = getLowerBounds(lhsType);
        if (rhsType instanceof WildcardType) {
            WildcardType rhsWcType = (WildcardType) rhsType;
            Type[] rUpperBounds = getUpperBounds(rhsWcType);
            Type[] rLowerBounds = getLowerBounds(rhsWcType);
            for (Type lBound : lUpperBounds) {
                for (Type rBound : rUpperBounds) {
                    if (!isAssignableBound(lBound, rBound)) {
                        return false;
                    }
                }
                for (Type rBound2 : rLowerBounds) {
                    if (!isAssignableBound(lBound, rBound2)) {
                        return false;
                    }
                }
            }
            for (Type lBound2 : lLowerBounds) {
                for (Type rBound3 : rUpperBounds) {
                    if (!isAssignableBound(rBound3, lBound2)) {
                        return false;
                    }
                }
                for (Type rBound4 : rLowerBounds) {
                    if (!isAssignableBound(rBound4, lBound2)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Type lBound3 : lUpperBounds) {
            if (!isAssignableBound(lBound3, rhsType)) {
                return false;
            }
        }
        for (Type lBound4 : lLowerBounds) {
            if (!isAssignableBound(rhsType, lBound4)) {
                return false;
            }
        }
        return true;
    }

    private static Type[] getLowerBounds(WildcardType wildcardType) {
        Type[] lowerBounds = wildcardType.getLowerBounds();
        return lowerBounds.length == 0 ? IMPLICIT_LOWER_BOUNDS : lowerBounds;
    }

    private static Type[] getUpperBounds(WildcardType wildcardType) {
        Type[] upperBounds = wildcardType.getUpperBounds();
        return upperBounds.length == 0 ? IMPLICIT_UPPER_BOUNDS : upperBounds;
    }

    public static boolean isAssignableBound(@Nullable Type lhsType, @Nullable Type rhsType) {
        if (rhsType == null) {
            return true;
        }
        if (lhsType == null) {
            return false;
        }
        return isAssignable(lhsType, rhsType);
    }
}
