package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap.class */
public abstract class PropertySerializerMap {
    protected final boolean _resetWhenFull;

    public abstract JsonSerializer<Object> serializerFor(Class<?> cls);

    public abstract PropertySerializerMap newWith(Class<?> cls, JsonSerializer<Object> jsonSerializer);

    protected PropertySerializerMap(boolean resetWhenFull) {
        this._resetWhenFull = resetWhenFull;
    }

    protected PropertySerializerMap(PropertySerializerMap base) {
        this._resetWhenFull = base._resetWhenFull;
    }

    public final SerializerAndMapResult findAndAddPrimarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddPrimarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddSecondarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findContentValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddSecondarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findContentValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddRootValueSerializer(Class<?> type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, (BeanProperty) null);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult findAndAddRootValueSerializer(JavaType type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, (BeanProperty) null);
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public final SerializerAndMapResult findAndAddKeySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> serializer = provider.findKeySerializer(type, property);
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult addSerializer(Class<?> type, JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, newWith(type, serializer));
    }

    public final SerializerAndMapResult addSerializer(JavaType type, JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
    }

    public static PropertySerializerMap emptyForProperties() {
        return Empty.FOR_PROPERTIES;
    }

    public static PropertySerializerMap emptyForRootValues() {
        return Empty.FOR_ROOT_VALUES;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$SerializerAndMapResult.class */
    public static final class SerializerAndMapResult {
        public final JsonSerializer<Object> serializer;
        public final PropertySerializerMap map;

        public SerializerAndMapResult(JsonSerializer<Object> serializer, PropertySerializerMap map) {
            this.serializer = serializer;
            this.map = map;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$TypeAndSerializer.class */
    private static final class TypeAndSerializer {
        public final Class<?> type;
        public final JsonSerializer<Object> serializer;

        public TypeAndSerializer(Class<?> type, JsonSerializer<Object> serializer) {
            this.type = type;
            this.serializer = serializer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Empty.class */
    public static final class Empty extends PropertySerializerMap {
        public static final Empty FOR_PROPERTIES = new Empty(false);
        public static final Empty FOR_ROOT_VALUES = new Empty(true);

        protected Empty(boolean resetWhenFull) {
            super(resetWhenFull);
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            return new Single(this, type, serializer);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Single.class */
    private static final class Single extends PropertySerializerMap {
        private final Class<?> _type;
        private final JsonSerializer<Object> _serializer;

        public Single(PropertySerializerMap base, Class<?> type, JsonSerializer<Object> serializer) {
            super(base);
            this._type = type;
            this._serializer = serializer;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            if (type == this._type) {
                return this._serializer;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            return new Double(this, this._type, this._serializer, type, serializer);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Double.class */
    private static final class Double extends PropertySerializerMap {
        private final Class<?> _type1;
        private final Class<?> _type2;
        private final JsonSerializer<Object> _serializer1;
        private final JsonSerializer<Object> _serializer2;

        public Double(PropertySerializerMap base, Class<?> type1, JsonSerializer<Object> serializer1, Class<?> type2, JsonSerializer<Object> serializer2) {
            super(base);
            this._type1 = type1;
            this._serializer1 = serializer1;
            this._type2 = type2;
            this._serializer2 = serializer2;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public JsonSerializer<Object> serializerFor(Class<?> type) {
            if (type == this._type1) {
                return this._serializer1;
            }
            if (type == this._type2) {
                return this._serializer2;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            TypeAndSerializer[] ts = {new TypeAndSerializer(this._type1, this._serializer1), new TypeAndSerializer(this._type2, this._serializer2), new TypeAndSerializer(type, serializer)};
            return new Multi(this, ts);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap$Multi.class */
    private static final class Multi extends PropertySerializerMap {
        private static final int MAX_ENTRIES = 8;
        private final TypeAndSerializer[] _entries;

        public Multi(PropertySerializerMap base, TypeAndSerializer[] entries) {
            super(base);
            this._entries = entries;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Failed to find 'out' block for switch in B:15:0x0041. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:22:0x0089  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x009d  */
        /* JADX WARN: Removed duplicated region for block: B:30:0x00b1  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00c5  */
        /* JADX WARN: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public com.fasterxml.jackson.databind.JsonSerializer<java.lang.Object> serializerFor(java.lang.Class<?> r4) {
            /*
                Method dump skipped, instructions count: 204
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap.Multi.serializerFor(java.lang.Class):com.fasterxml.jackson.databind.JsonSerializer");
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap
        public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
            int len = this._entries.length;
            if (len == 8) {
                if (this._resetWhenFull) {
                    return new Single(this, type, serializer);
                }
                return this;
            }
            TypeAndSerializer[] entries = (TypeAndSerializer[]) Arrays.copyOf(this._entries, len + 1);
            entries[len] = new TypeAndSerializer(type, serializer);
            return new Multi(this, entries);
        }
    }
}
