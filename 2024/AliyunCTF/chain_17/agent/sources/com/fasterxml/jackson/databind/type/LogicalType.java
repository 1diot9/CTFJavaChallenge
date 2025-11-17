package com.fasterxml.jackson.databind.type;

import java.util.Collection;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/type/LogicalType.class */
public enum LogicalType {
    Array,
    Collection,
    Map,
    POJO,
    Untyped,
    Integer,
    Float,
    Boolean,
    Enum,
    Textual,
    Binary,
    DateTime,
    OtherScalar;

    public static LogicalType fromClass(Class<?> raw, LogicalType defaultIfNotRecognized) {
        if (raw.isEnum()) {
            return Enum;
        }
        if (raw.isArray()) {
            if (raw == byte[].class) {
                return Binary;
            }
            return Array;
        }
        if (Collection.class.isAssignableFrom(raw)) {
            return Collection;
        }
        if (Map.class.isAssignableFrom(raw)) {
            return Map;
        }
        if (raw == String.class) {
            return Textual;
        }
        return defaultIfNotRecognized;
    }
}
