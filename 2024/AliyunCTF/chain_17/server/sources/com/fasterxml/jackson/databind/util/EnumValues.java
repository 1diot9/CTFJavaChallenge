package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.EnumFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/EnumValues.class */
public final class EnumValues implements Serializable {
    private static final long serialVersionUID = 1;
    private final Class<Enum<?>> _enumClass;
    private final Enum<?>[] _values;
    private final SerializableString[] _textual;
    private transient EnumMap<?, SerializableString> _asMap;

    private EnumValues(Class<Enum<?>> enumClass, SerializableString[] textual) {
        this._enumClass = enumClass;
        this._values = enumClass.getEnumConstants();
        this._textual = textual;
    }

    public static EnumValues construct(SerializationConfig config, Class<Enum<?>> enumClass) {
        if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            return constructFromToString(config, enumClass);
        }
        return constructFromName(config, enumClass);
    }

    public static EnumValues constructFromName(MapperConfig<?> config, Class<Enum<?>> enumClass) {
        Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
        Enum<?>[] enumValues = (Enum[]) enumCls.getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
        }
        String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, enumValues, new String[enumValues.length]);
        SerializableString[] textual = new SerializableString[enumValues.length];
        int len = enumValues.length;
        for (int i = 0; i < len; i++) {
            Enum<?> en = enumValues[i];
            String name = names[i];
            if (name == null) {
                name = en.name();
            }
            if (config.isEnabled(EnumFeature.WRITE_ENUMS_TO_LOWERCASE)) {
                name = name.toLowerCase();
            }
            textual[en.ordinal()] = config.compileString(name);
        }
        return construct(enumClass, textual);
    }

    public static EnumValues constructFromToString(MapperConfig<?> config, Class<Enum<?>> enumClass) {
        Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
        Enum<?>[] values = (Enum[]) cls.getEnumConstants();
        if (values == null) {
            throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
        }
        ArrayList<String> external = new ArrayList<>(values.length);
        for (Enum<?> en : values) {
            external.add(en.toString());
        }
        return construct(config, enumClass, external);
    }

    public static EnumValues constructUsingEnumNamingStrategy(MapperConfig<?> config, Class<Enum<?>> enumClass, EnumNamingStrategy namingStrategy) {
        Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
        Enum<?>[] values = (Enum[]) cls.getEnumConstants();
        if (values == null) {
            throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
        }
        ArrayList<String> external = new ArrayList<>(values.length);
        for (Enum<?> en : values) {
            external.add(namingStrategy.convertEnumToExternalName(en.name()));
        }
        return construct(config, enumClass, external);
    }

    public static EnumValues construct(MapperConfig<?> config, Class<Enum<?>> enumClass, List<String> externalValues) {
        int len = externalValues.size();
        SerializableString[] textual = new SerializableString[len];
        for (int i = 0; i < len; i++) {
            textual[i] = config.compileString(externalValues.get(i));
        }
        return construct(enumClass, textual);
    }

    public static EnumValues construct(Class<Enum<?>> enumClass, SerializableString[] externalValues) {
        return new EnumValues(enumClass, externalValues);
    }

    public SerializableString serializedValueFor(Enum<?> key) {
        return this._textual[key.ordinal()];
    }

    public Collection<SerializableString> values() {
        return Arrays.asList(this._textual);
    }

    public List<Enum<?>> enums() {
        return Arrays.asList(this._values);
    }

    public EnumMap<?, SerializableString> internalMap() {
        EnumMap<?, SerializableString> result = this._asMap;
        if (result == null) {
            Map<Enum<?>, SerializableString> map = new LinkedHashMap<>();
            for (Enum<?> en : this._values) {
                map.put(en, this._textual[en.ordinal()]);
            }
            result = new EnumMap<>(map);
        }
        return result;
    }

    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }
}
