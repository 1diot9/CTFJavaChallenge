package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/BeanAsArraySerializer.class */
public class BeanAsArraySerializer extends BeanSerializerBase {
    private static final long serialVersionUID = 1;
    protected final BeanSerializerBase _defaultSerializer;

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
    protected /* bridge */ /* synthetic */ BeanSerializerBase withByNameInclusion(Set set, Set set2) {
        return withByNameInclusion((Set<String>) set, (Set<String>) set2);
    }

    public BeanAsArraySerializer(BeanSerializerBase src) {
        super(src, (ObjectIdWriter) null);
        this._defaultSerializer = src;
    }

    protected BeanAsArraySerializer(BeanSerializerBase src, Set<String> toIgnore) {
        this(src, toIgnore, (Set<String>) null);
    }

    protected BeanAsArraySerializer(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
        super(src, toIgnore, toInclude);
        this._defaultSerializer = src;
    }

    protected BeanAsArraySerializer(BeanSerializerBase src, ObjectIdWriter oiw, Object filterId) {
        super(src, oiw, filterId);
        this._defaultSerializer = src;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
        return this._defaultSerializer.unwrappingSerializer(transformer);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isUnwrappingSerializer() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return this._defaultSerializer.withObjectIdWriter(objectIdWriter);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public BeanSerializerBase withFilterId(Object filterId) {
        return new BeanAsArraySerializer(this, this._objectIdWriter, filterId);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
    protected BeanAsArraySerializer withByNameInclusion(Set<String> toIgnore, Set<String> toInclude) {
        return new BeanAsArraySerializer(this, toIgnore, toInclude);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
    protected BeanSerializerBase withProperties(BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        if (this._objectIdWriter != null) {
            _serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_ARRAY);
        typeSer.writeTypePrefix(gen, typeIdDef);
        gen.setCurrentValue(bean);
        serializeAsArray(bean, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.BeanSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && hasSingleElement(provider)) {
            serializeAsArray(bean, gen, provider);
            return;
        }
        gen.writeStartArray(bean);
        serializeAsArray(bean, gen, provider);
        gen.writeEndArray();
    }

    private boolean hasSingleElement(SerializerProvider provider) {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        } else {
            props = this._props;
        }
        return props.length == 1;
    }

    protected final void serializeAsArray(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        } else {
            props = this._props;
        }
        int i = 0;
        try {
            int len = props.length;
            while (i < len) {
                BeanPropertyWriter prop = props[i];
                if (prop == null) {
                    gen.writeNull();
                } else {
                    prop.serializeAsElement(bean, gen, provider);
                }
                i++;
            }
        } catch (Exception e) {
            wrapAndThrow(provider, e, bean, props[i].getName());
        } catch (StackOverflowError e2) {
            DatabindException mapE = JsonMappingException.from(gen, "Infinite recursion (StackOverflowError)", e2);
            mapE.prependPath(bean, props[i].getName());
            throw mapE;
        }
    }

    public String toString() {
        return "BeanAsArraySerializer for " + handledType().getName();
    }
}
