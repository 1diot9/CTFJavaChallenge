package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.Optional;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/OptionalDeserializer.class */
final class OptionalDeserializer extends ReferenceTypeDeserializer<Optional<?>> {
    private static final long serialVersionUID = 1;
    protected final boolean _cfgReadAbsentAsNull;

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    /* renamed from: withResolved, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ ReferenceTypeDeserializer<Optional<?>> withResolved2(TypeDeserializer typeDeserializer, JsonDeserializer jsonDeserializer) {
        return withResolved(typeDeserializer, (JsonDeserializer<?>) jsonDeserializer);
    }

    public OptionalDeserializer(JavaType fullType, ValueInstantiator inst, TypeDeserializer typeDeser, JsonDeserializer<?> deser, boolean cfgReadAbsentAsNull) {
        super(fullType, inst, typeDeser, deser);
        this._cfgReadAbsentAsNull = cfgReadAbsentAsNull;
    }

    @Deprecated
    public OptionalDeserializer(JavaType fullType, ValueInstantiator inst, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        this(fullType, inst, typeDeser, deser, false);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public ReferenceTypeDeserializer<Optional<?>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new OptionalDeserializer(this._fullType, this._valueInstantiator, typeDeser, valueDeser, this._cfgReadAbsentAsNull);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public Optional<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return Optional.ofNullable(this._valueDeserializer.getNullValue(ctxt));
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        if (this._cfgReadAbsentAsNull) {
            return null;
        }
        return getNullValue(ctxt);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Optional<?> referenceValue(Object contents) {
        return Optional.ofNullable(contents);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Object getReferenced(Optional<?> reference) {
        return reference.orElse(null);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer
    public Optional<?> updateReference(Optional<?> reference, Object contents) {
        return Optional.ofNullable(contents);
    }
}
