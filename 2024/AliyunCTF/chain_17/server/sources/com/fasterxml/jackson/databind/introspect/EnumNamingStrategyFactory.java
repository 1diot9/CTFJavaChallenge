package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.util.ClassUtil;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/EnumNamingStrategyFactory.class */
public class EnumNamingStrategyFactory {
    private EnumNamingStrategyFactory() {
    }

    public static EnumNamingStrategy createEnumNamingStrategyInstance(Object namingDef, boolean canOverrideAccessModifiers) {
        if (namingDef == null) {
            return null;
        }
        if (namingDef instanceof EnumNamingStrategy) {
            return (EnumNamingStrategy) namingDef;
        }
        if (!(namingDef instanceof Class)) {
            throw new IllegalArgumentException(String.format("AnnotationIntrospector returned EnumNamingStrategy definition of type %s; expected type `Class<EnumNamingStrategy>` instead", ClassUtil.classNameOf(namingDef)));
        }
        Class<?> namingClass = (Class) namingDef;
        if (namingClass == EnumNamingStrategy.class) {
            return null;
        }
        if (!EnumNamingStrategy.class.isAssignableFrom(namingClass)) {
            throw new IllegalArgumentException(String.format("Problem with AnnotationIntrospector returned Class %s; expected `Class<EnumNamingStrategy>`", ClassUtil.classNameOf(namingClass)));
        }
        return (EnumNamingStrategy) ClassUtil.createInstance(namingClass, canOverrideAccessModifiers);
    }
}
