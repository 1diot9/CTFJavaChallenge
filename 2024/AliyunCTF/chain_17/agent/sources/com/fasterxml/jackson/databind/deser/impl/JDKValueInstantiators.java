package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JDKValueInstantiators.class */
public abstract class JDKValueInstantiators {
    public static ValueInstantiator findStdValueInstantiator(DeserializationConfig config, Class<?> raw) {
        if (raw == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        if (Collection.class.isAssignableFrom(raw)) {
            if (raw == ArrayList.class) {
                return ArrayListInstantiator.INSTANCE;
            }
            if (Collections.EMPTY_SET.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_SET);
            }
            if (Collections.EMPTY_LIST.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_LIST);
            }
            return null;
        }
        if (Map.class.isAssignableFrom(raw)) {
            if (raw == LinkedHashMap.class) {
                return LinkedHashMapInstantiator.INSTANCE;
            }
            if (raw == HashMap.class) {
                return HashMapInstantiator.INSTANCE;
            }
            if (Collections.EMPTY_MAP.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_MAP);
            }
            return null;
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JDKValueInstantiators$ArrayListInstantiator.class */
    private static class ArrayListInstantiator extends ValueInstantiator.Base implements Serializable {
        private static final long serialVersionUID = 2;
        public static final ArrayListInstantiator INSTANCE = new ArrayListInstantiator();

        public ArrayListInstantiator() {
            super((Class<?>) ArrayList.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canInstantiate() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canCreateUsingDefault() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
            return new ArrayList();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JDKValueInstantiators$HashMapInstantiator.class */
    private static class HashMapInstantiator extends ValueInstantiator.Base implements Serializable {
        private static final long serialVersionUID = 2;
        public static final HashMapInstantiator INSTANCE = new HashMapInstantiator();

        public HashMapInstantiator() {
            super((Class<?>) HashMap.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canInstantiate() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canCreateUsingDefault() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
            return new HashMap();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JDKValueInstantiators$LinkedHashMapInstantiator.class */
    private static class LinkedHashMapInstantiator extends ValueInstantiator.Base implements Serializable {
        private static final long serialVersionUID = 2;
        public static final LinkedHashMapInstantiator INSTANCE = new LinkedHashMapInstantiator();

        public LinkedHashMapInstantiator() {
            super((Class<?>) LinkedHashMap.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canInstantiate() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canCreateUsingDefault() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
            return new LinkedHashMap();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JDKValueInstantiators$ConstantValueInstantiator.class */
    private static class ConstantValueInstantiator extends ValueInstantiator.Base implements Serializable {
        private static final long serialVersionUID = 2;
        protected final Object _value;

        public ConstantValueInstantiator(Object value) {
            super(value.getClass());
            this._value = value;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canInstantiate() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public boolean canCreateUsingDefault() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
        public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
            return this._value;
        }
    }
}
