package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/UnwrappingBeanPropertyWriter.class */
public class UnwrappingBeanPropertyWriter extends BeanPropertyWriter implements Serializable {
    private static final long serialVersionUID = 1;
    protected final NameTransformer _nameTransformer;

    public UnwrappingBeanPropertyWriter(BeanPropertyWriter base, NameTransformer unwrapper) {
        super(base);
        this._nameTransformer = unwrapper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UnwrappingBeanPropertyWriter(UnwrappingBeanPropertyWriter base, NameTransformer transformer, SerializedString name) {
        super(base, name);
        this._nameTransformer = transformer;
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    public UnwrappingBeanPropertyWriter rename(NameTransformer transformer) {
        String oldName = this._name.getValue();
        String newName = transformer.transform(oldName);
        return _new(NameTransformer.chainedTransformer(transformer, this._nameTransformer), new SerializedString(newName));
    }

    protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName) {
        return new UnwrappingBeanPropertyWriter(this, transformer, newName);
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    public boolean isUnwrapping() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        Object value = get(bean);
        if (value == null) {
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            } else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && _handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        if (!ser.isUnwrappingSerializer()) {
            gen.writeFieldName(this._name);
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    public void assignSerializer(JsonSerializer<Object> ser) {
        if (ser != null) {
            NameTransformer t = this._nameTransformer;
            if (ser.isUnwrappingSerializer() && (ser instanceof UnwrappingBeanSerializer)) {
                t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer) ser)._nameTransformer);
            }
            ser = ser.unwrappingSerializer(t);
        }
        super.assignSerializer(ser);
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
    public void depositSchemaProperty(final JsonObjectFormatVisitor visitor, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> ser = provider.findValueSerializer(getType(), this).unwrappingSerializer(this._nameTransformer);
        if (ser.isUnwrappingSerializer()) {
            ser.acceptJsonFormatVisitor(new JsonFormatVisitorWrapper.Base(provider) { // from class: com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter.1
                @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
                public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
                    return visitor;
                }
            }, getType());
        } else {
            super.depositSchemaProperty(visitor, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode) {
        JsonNode props = schemaNode.get("properties");
        if (props != null) {
            Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String name = entry.getKey();
                if (this._nameTransformer != null) {
                    name = this._nameTransformer.transform(name);
                }
                propertiesNode.set(name, entry.getValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    public JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        JsonSerializer<Object> serializer;
        if (this._nonTrivialBaseType != null) {
            JavaType subtype = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            serializer = provider.findValueSerializer(subtype, this);
        } else {
            serializer = provider.findValueSerializer(type, this);
        }
        NameTransformer t = this._nameTransformer;
        if (serializer.isUnwrappingSerializer() && (serializer instanceof UnwrappingBeanSerializer)) {
            t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer) serializer)._nameTransformer);
        }
        JsonSerializer<Object> serializer2 = serializer.unwrappingSerializer(t);
        this._dynamicSerializers = this._dynamicSerializers.newWith(type, serializer2);
        return serializer2;
    }
}
