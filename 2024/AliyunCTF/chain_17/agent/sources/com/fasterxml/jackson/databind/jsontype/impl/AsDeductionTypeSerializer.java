package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/AsDeductionTypeSerializer.class */
public class AsDeductionTypeSerializer extends TypeSerializerBase {
    private static final AsDeductionTypeSerializer INSTANCE = new AsDeductionTypeSerializer();

    protected AsDeductionTypeSerializer() {
        super(null, null);
    }

    public static AsDeductionTypeSerializer instance() {
        return INSTANCE;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public AsDeductionTypeSerializer forProperty(BeanProperty prop) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeSerializerBase, com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXISTING_PROPERTY;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeSerializerBase, com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
        if (idMetadata.valueShape.isStructStart()) {
            if (g.canWriteTypeId()) {
                idMetadata.wrapperWritten = false;
                if (idMetadata.valueShape == JsonToken.START_OBJECT) {
                    g.writeStartObject(idMetadata.forValue);
                } else if (idMetadata.valueShape == JsonToken.START_ARRAY) {
                    g.writeStartArray(idMetadata.forValue);
                }
                return idMetadata;
            }
            return g.writeTypePrefix(idMetadata);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeSerializerBase, com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
        if (idMetadata == null) {
            return null;
        }
        return g.writeTypeSuffix(idMetadata);
    }
}
