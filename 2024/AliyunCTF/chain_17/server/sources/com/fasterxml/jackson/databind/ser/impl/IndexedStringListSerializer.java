package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;
import java.io.IOException;
import java.util.List;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/IndexedStringListSerializer.class */
public final class IndexedStringListSerializer extends StaticListSerializerBase<List<String>> {
    private static final long serialVersionUID = 1;
    public static final IndexedStringListSerializer instance = new IndexedStringListSerializer();

    protected IndexedStringListSerializer() {
        super(List.class);
    }

    public IndexedStringListSerializer(IndexedStringListSerializer src, Boolean unwrapSingle) {
        super(src, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase
    public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
        return new IndexedStringListSerializer(this, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase
    protected JsonNode contentSchema() {
        return createSchemaNode("string", true);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase
    protected void acceptContentVisitor(JsonArrayFormatVisitor visitor) throws JsonMappingException {
        visitor.itemsFormat(JsonFormatTypes.STRING);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(List<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
        int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            serializeContents(value, g, provider, 1);
            return;
        }
        g.writeStartArray(value, len);
        serializeContents(value, g, provider, len);
        g.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(List<String> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        g.setCurrentValue(value);
        serializeContents(value, g, provider, value.size());
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private final void serializeContents(List<String> value, JsonGenerator g, SerializerProvider provider, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            try {
                String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(g);
                } else {
                    g.writeString(str);
                }
            } catch (Exception e) {
                wrapAndThrow(provider, e, value, i);
                return;
            }
        }
    }
}
