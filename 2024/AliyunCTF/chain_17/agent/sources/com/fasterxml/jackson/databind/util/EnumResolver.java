package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/EnumResolver.class */
public class EnumResolver implements Serializable {
    private static final long serialVersionUID = 1;
    protected final Class<Enum<?>> _enumClass;
    protected final Enum<?>[] _enums;
    protected final HashMap<String, Enum<?>> _enumsById;
    protected final Enum<?> _defaultValue;
    protected final boolean _isIgnoreCase;
    protected final boolean _isFromIntValue;

    protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase, boolean isFromIntValue) {
        this._enumClass = enumClass;
        this._enums = enums;
        this._enumsById = map;
        this._defaultValue = defaultValue;
        this._isIgnoreCase = isIgnoreCase;
        this._isFromIntValue = isFromIntValue;
    }

    public static EnumResolver constructFor(DeserializationConfig config, Class<?> enumCls) {
        return _constructFor(config, enumCls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v12, types: [java.lang.String[], java.lang.String[][]] */
    protected static EnumResolver _constructFor(DeserializationConfig config, Class<?> enumCls0) {
        AnnotationIntrospector annotationIntrospector = config.getAnnotationIntrospector();
        boolean isIgnoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        Class<Enum<?>> enumCls = _enumClass(enumCls0);
        Enum<?>[] enumConstants = _enumConstants(enumCls0);
        String[] names = annotationIntrospector.findEnumValues(enumCls, enumConstants, new String[enumConstants.length]);
        ?? r0 = new String[names.length];
        annotationIntrospector.findEnumAliases(enumCls, enumConstants, r0);
        HashMap hashMap = new HashMap();
        int len = enumConstants.length;
        for (int i = 0; i < len; i++) {
            Enum<?> enumValue = enumConstants[i];
            String name = names[i];
            if (name == null) {
                name = enumValue.name();
            }
            hashMap.put(name, enumValue);
            Object[] objArr = r0[i];
            if (objArr != 0) {
                for (Object[] objArr2 : objArr) {
                    hashMap.putIfAbsent(objArr2, enumValue);
                }
            }
        }
        return new EnumResolver(enumCls, enumConstants, hashMap, _enumDefault(annotationIntrospector, enumCls), isIgnoreCase, false);
    }

    public static EnumResolver constructUsingToString(DeserializationConfig config, Class<?> enumCls) {
        return _constructUsingToString(config, enumCls);
    }

    public static EnumResolver constructUsingIndex(DeserializationConfig config, Class<Enum<?>> enumCls) {
        return _constructUsingIndex(config, enumCls);
    }

    private static EnumResolver _constructUsingIndex(DeserializationConfig config, Class<Enum<?>> enumCls0) {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        boolean isIgnoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        Class<Enum<?>> enumCls = _enumClass(enumCls0);
        Enum<?>[] enumConstants = _enumConstants(enumCls0);
        HashMap<String, Enum<?>> map = new HashMap<>();
        int i = enumConstants.length;
        while (true) {
            i--;
            if (i >= 0) {
                Enum<?> enumValue = enumConstants[i];
                map.put(String.valueOf(i), enumValue);
            } else {
                return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase, false);
            }
        }
    }

    public static EnumResolver constructUsingEnumNamingStrategy(DeserializationConfig config, Class<?> enumCls, EnumNamingStrategy enumNamingStrategy) {
        return _constructUsingEnumNamingStrategy(config, enumCls, enumNamingStrategy);
    }

    private static EnumResolver _constructUsingEnumNamingStrategy(DeserializationConfig config, Class<?> enumCls0, EnumNamingStrategy enumNamingStrategy) {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        boolean isIgnoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        Class<Enum<?>> enumCls = _enumClass(enumCls0);
        Enum<?>[] enumConstants = _enumConstants(enumCls0);
        HashMap<String, Enum<?>> map = new HashMap<>();
        int i = enumConstants.length;
        while (true) {
            i--;
            if (i >= 0) {
                Enum<?> anEnum = enumConstants[i];
                String translatedExternalValue = enumNamingStrategy.convertEnumToExternalName(anEnum.name());
                map.put(translatedExternalValue, anEnum);
            } else {
                return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase, false);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [java.lang.String[], java.lang.String[][]] */
    protected static EnumResolver _constructUsingToString(DeserializationConfig config, Class<?> enumCls0) {
        AnnotationIntrospector annotationIntrospector = config.getAnnotationIntrospector();
        boolean isIgnoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        Class<Enum<?>> enumCls = _enumClass(enumCls0);
        Enum<?>[] enumConstants = _enumConstants(enumCls0);
        HashMap hashMap = new HashMap();
        ?? r0 = new String[enumConstants.length];
        if (annotationIntrospector != 0) {
            annotationIntrospector.findEnumAliases(enumCls, enumConstants, r0);
        }
        int i = enumConstants.length;
        while (true) {
            i--;
            if (i >= 0) {
                Enum<?> enumValue = enumConstants[i];
                hashMap.put(enumValue.toString(), enumValue);
                Object[] objArr = r0[i];
                if (objArr != 0) {
                    for (Object[] objArr2 : objArr) {
                        hashMap.putIfAbsent(objArr2, enumValue);
                    }
                }
            } else {
                return new EnumResolver(enumCls, enumConstants, hashMap, _enumDefault(annotationIntrospector, enumCls), isIgnoreCase, false);
            }
        }
    }

    public static EnumResolver constructUsingMethod(DeserializationConfig config, Class<?> enumCls, AnnotatedMember accessor) {
        return _constructUsingMethod(config, enumCls, accessor);
    }

    protected static EnumResolver _constructUsingMethod(DeserializationConfig config, Class<?> enumCls0, AnnotatedMember accessor) {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        boolean isIgnoreCase = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        Class<Enum<?>> enumCls = _enumClass(enumCls0);
        Enum<?>[] enumConstants = _enumConstants(enumCls0);
        HashMap<String, Enum<?>> map = new HashMap<>();
        int i = enumConstants.length;
        while (true) {
            i--;
            if (i >= 0) {
                Enum<?> en = enumConstants[i];
                try {
                    Object o = accessor.getValue(en);
                    if (o != null) {
                        map.put(o.toString(), en);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
                }
            } else {
                return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, enumCls), isIgnoreCase, _isIntType(accessor.getRawType()));
            }
        }
    }

    public CompactStringObjectMap constructLookup() {
        return CompactStringObjectMap.construct(this._enumsById);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected static Class<Enum<?>> _enumClass(Class<?> cls) {
        return cls;
    }

    protected static Enum<?>[] _enumConstants(Class<?> enumCls) {
        Enum<?>[] enumValues = _enumClass(enumCls).getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
        }
        return enumValues;
    }

    protected static Enum<?> _enumDefault(AnnotationIntrospector intr, Class<?> enumCls) {
        if (intr != null) {
            return intr.findDefaultEnumValue(_enumClass(enumCls));
        }
        return null;
    }

    protected static boolean _isIntType(Class<?> erasedType) {
        if (erasedType.isPrimitive()) {
            erasedType = ClassUtil.wrapperType(erasedType);
        }
        return erasedType == Long.class || erasedType == Integer.class || erasedType == Short.class || erasedType == Byte.class;
    }

    @Deprecated
    protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase) {
        this(enumClass, enums, map, defaultValue, isIgnoreCase, false);
    }

    public Enum<?> findEnum(String key) {
        Enum<?> en = this._enumsById.get(key);
        if (en == null && this._isIgnoreCase) {
            return _findEnumCaseInsensitive(key);
        }
        return en;
    }

    protected Enum<?> _findEnumCaseInsensitive(String key) {
        for (Map.Entry<String, Enum<?>> entry : this._enumsById.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Enum<?> getEnum(int index) {
        if (index < 0 || index >= this._enums.length) {
            return null;
        }
        return this._enums[index];
    }

    public Enum<?> getDefaultValue() {
        return this._defaultValue;
    }

    public Enum<?>[] getRawEnums() {
        return this._enums;
    }

    public List<Enum<?>> getEnums() {
        ArrayList<Enum<?>> enums = new ArrayList<>(this._enums.length);
        for (Enum<?> e : this._enums) {
            enums.add(e);
        }
        return enums;
    }

    public Collection<String> getEnumIds() {
        return this._enumsById.keySet();
    }

    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }

    public int lastValidIndex() {
        return this._enums.length - 1;
    }

    public boolean isFromIntValue() {
        return this._isFromIntValue;
    }
}
