package com.fasterxml.jackson.core.type;

import com.fasterxml.jackson.core.JsonToken;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/type/WritableTypeId.class */
public class WritableTypeId {
    public Object forValue;
    public Class<?> forValueType;
    public Object id;
    public String asProperty;
    public Inclusion include;
    public JsonToken valueShape;
    public boolean wrapperWritten;
    public Object extra;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/type/WritableTypeId$Inclusion.class */
    public enum Inclusion {
        WRAPPER_ARRAY,
        WRAPPER_OBJECT,
        METADATA_PROPERTY,
        PAYLOAD_PROPERTY,
        PARENT_PROPERTY;

        public boolean requiresObjectContext() {
            return this == METADATA_PROPERTY || this == PAYLOAD_PROPERTY;
        }
    }

    public WritableTypeId() {
    }

    public WritableTypeId(Object value, JsonToken valueShape) {
        this(value, valueShape, (Object) null);
    }

    public WritableTypeId(Object value, Class<?> valueType, JsonToken valueShape) {
        this(value, valueShape, (Object) null);
        this.forValueType = valueType;
    }

    public WritableTypeId(Object value, JsonToken valueShape, Object id) {
        this.forValue = value;
        this.id = id;
        this.valueShape = valueShape;
    }
}
