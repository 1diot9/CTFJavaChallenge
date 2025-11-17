package com.fasterxml.jackson.databind.module;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.ClassKey;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/module/SimpleSerializers.class */
public class SimpleSerializers extends Serializers.Base implements Serializable {
    private static final long serialVersionUID = 3;
    protected HashMap<ClassKey, JsonSerializer<?>> _classMappings = null;
    protected HashMap<ClassKey, JsonSerializer<?>> _interfaceMappings = null;
    protected boolean _hasEnumSerializer = false;

    public SimpleSerializers() {
    }

    public SimpleSerializers(List<JsonSerializer<?>> sers) {
        addSerializers(sers);
    }

    public void addSerializer(JsonSerializer<?> ser) {
        Class<?> cls = ser.handledType();
        if (cls == null || cls == Object.class) {
            throw new IllegalArgumentException("JsonSerializer of type " + ser.getClass().getName() + " does not define valid handledType() -- must either register with method that takes type argument  or make serializer extend 'com.fasterxml.jackson.databind.ser.std.StdSerializer'");
        }
        _addSerializer(cls, ser);
    }

    public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        _addSerializer(type, ser);
    }

    public void addSerializers(List<JsonSerializer<?>> sers) {
        for (JsonSerializer<?> ser : sers) {
            addSerializer(ser);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        JsonSerializer<?> ser;
        JsonSerializer<?> ser2;
        Class<?> cls = type.getRawClass();
        ClassKey key = new ClassKey(cls);
        if (cls.isInterface()) {
            if (this._interfaceMappings != null && (ser2 = this._interfaceMappings.get(key)) != null) {
                return ser2;
            }
        } else if (this._classMappings != null) {
            JsonSerializer<?> ser3 = this._classMappings.get(key);
            if (ser3 != null) {
                return ser3;
            }
            if (this._hasEnumSerializer && type.isEnumType()) {
                key.reset(Enum.class);
                JsonSerializer<?> ser4 = this._classMappings.get(key);
                if (ser4 != null) {
                    return ser4;
                }
            }
            Class<?> cls2 = cls;
            while (true) {
                Class<?> curr = cls2;
                if (curr == null) {
                    break;
                }
                key.reset(curr);
                JsonSerializer<?> ser5 = this._classMappings.get(key);
                if (ser5 == null) {
                    cls2 = curr.getSuperclass();
                } else {
                    return ser5;
                }
            }
        }
        if (this._interfaceMappings != null) {
            JsonSerializer<?> ser6 = _findInterfaceMapping(cls, key);
            if (ser6 != null) {
                return ser6;
            }
            if (cls.isInterface()) {
                return null;
            }
            do {
                Class<? super Object> superclass = cls.getSuperclass();
                cls = superclass;
                if (superclass != null) {
                    ser = _findInterfaceMapping(cls, key);
                } else {
                    return null;
                }
            } while (ser == null);
            return ser;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return findSerializer(config, type, beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return findSerializer(config, type, beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return findSerializer(config, type, beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return findSerializer(config, type, beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return findSerializer(config, type, beanDesc);
    }

    protected JsonSerializer<?> _findInterfaceMapping(Class<?> cls, ClassKey key) {
        for (Class<?> iface : cls.getInterfaces()) {
            key.reset(iface);
            JsonSerializer<?> ser = this._interfaceMappings.get(key);
            if (ser != null) {
                return ser;
            }
            JsonSerializer<?> ser2 = _findInterfaceMapping(iface, key);
            if (ser2 != null) {
                return ser2;
            }
        }
        return null;
    }

    protected void _addSerializer(Class<?> cls, JsonSerializer<?> ser) {
        ClassKey key = new ClassKey(cls);
        if (cls.isInterface()) {
            if (this._interfaceMappings == null) {
                this._interfaceMappings = new HashMap<>();
            }
            this._interfaceMappings.put(key, ser);
        } else {
            if (this._classMappings == null) {
                this._classMappings = new HashMap<>();
            }
            this._classMappings.put(key, ser);
            if (cls == Enum.class) {
                this._hasEnumSerializer = true;
            }
        }
    }
}
