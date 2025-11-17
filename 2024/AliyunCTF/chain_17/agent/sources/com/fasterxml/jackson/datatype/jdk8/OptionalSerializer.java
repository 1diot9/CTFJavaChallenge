package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Optional;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/OptionalSerializer.class */
public class OptionalSerializer extends ReferenceTypeSerializer<Optional<?>> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    public OptionalSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected OptionalSerializer(OptionalSerializer base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Object suppressableValue, boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper, suppressableValue, suppressNulls);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer
    protected ReferenceTypeSerializer<Optional<?>> withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper) {
        return new OptionalSerializer(this, prop, vts, valueSer, unwrapper, this._suppressableValue, this._suppressNulls);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer
    public ReferenceTypeSerializer<Optional<?>> withContentInclusion(Object suppressableValue, boolean suppressNulls) {
        return new OptionalSerializer(this, this._property, this._valueTypeSerializer, this._valueSerializer, this._unwrapper, suppressableValue, suppressNulls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer
    public boolean _isValuePresent(Optional<?> value) {
        return value.isPresent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer
    public Object _getReferenced(Optional<?> value) {
        return value.get();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer
    public Object _getReferencedIfPresent(Optional<?> value) {
        if (value.isPresent()) {
            return value.get();
        }
        return null;
    }
}
