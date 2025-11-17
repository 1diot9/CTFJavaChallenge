package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import org.springframework.validation.DefaultBindingErrorProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/StdSerializer.class */
public abstract class StdSerializer<T> extends JsonSerializer<T> implements JsonFormatVisitable, SchemaAware, Serializable {
    private static final long serialVersionUID = 1;
    private static final Object KEY_CONTENT_CONVERTER_LOCK = new Object();
    protected final Class<T> _handledType;

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public abstract void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public StdSerializer(Class<T> t) {
        this._handledType = t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StdSerializer(JavaType javaType) {
        this._handledType = (Class<T>) javaType.getRawClass();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public StdSerializer(Class<?> cls, boolean dummy) {
        this._handledType = cls;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StdSerializer(StdSerializer<?> stdSerializer) {
        this._handledType = (Class<T>) stdSerializer._handledType;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public Class<T> handledType() {
        return this._handledType;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitor.expectAnyFormat(typeHint);
    }

    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return createSchemaNode("string");
    }

    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint, boolean isOptional) throws JsonMappingException {
        ObjectNode schema = (ObjectNode) getSchema(provider, typeHint);
        if (!isOptional) {
            schema.put(DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, !isOptional);
        }
        return schema;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectNode createSchemaNode(String type) {
        ObjectNode schema = JsonNodeFactory.instance.objectNode();
        schema.put("type", type);
        return schema;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectNode createSchemaNode(String type, boolean isOptional) {
        ObjectNode schema = createSchemaNode(type);
        if (!isOptional) {
            schema.put(DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, !isOptional);
        }
        return schema;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitStringFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitor.expectStringFormat(typeHint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitStringFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonValueFormat format) throws JsonMappingException {
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            v2.format(format);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitIntFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (_neitherNull(v2, numberType)) {
            v2.numberType(numberType);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitIntFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType, JsonValueFormat format) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            if (numberType != null) {
                v2.numberType(numberType);
            }
            if (format != null) {
                v2.format(format);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitFloatFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType) throws JsonMappingException {
        JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
        if (v2 != null) {
            v2.numberType(numberType);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitArrayFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonSerializer<?> itemSerializer, JavaType itemType) throws JsonMappingException {
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (_neitherNull(v2, itemSerializer)) {
            v2.itemsFormat(itemSerializer, itemType);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void visitArrayFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonFormatTypes itemType) throws JsonMappingException {
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(itemType);
        }
    }

    public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, String fieldName) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JacksonException)) {
                throw ((IOException) t);
            }
        } else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }

    public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, int index) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JacksonException)) {
                throw ((IOException) t);
            }
        } else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonSerializer<?> findContextualConvertingSerializer(SerializerProvider provider, BeanProperty property, JsonSerializer<?> existingSerializer) throws JsonMappingException {
        Map<Object, Object> conversions = (Map) provider.getAttribute(KEY_CONTENT_CONVERTER_LOCK);
        if (conversions != null) {
            Object lock = conversions.get(property);
            if (lock != null) {
                return existingSerializer;
            }
        } else {
            conversions = new IdentityHashMap<>();
            provider.setAttribute(KEY_CONTENT_CONVERTER_LOCK, (Object) conversions);
        }
        conversions.put(property, Boolean.TRUE);
        try {
            JsonSerializer<?> ser = findConvertingContentSerializer(provider, property, existingSerializer);
            if (ser != null) {
                JsonSerializer<?> handleSecondaryContextualization = provider.handleSecondaryContextualization(ser, property);
                conversions.remove(property);
                return handleSecondaryContextualization;
            }
            conversions.remove(property);
            return existingSerializer;
        } catch (Throwable th) {
            conversions.remove(property);
            throw th;
        }
    }

    @Deprecated
    protected JsonSerializer<?> findConvertingContentSerializer(SerializerProvider provider, BeanProperty prop, JsonSerializer<?> existingSerializer) throws JsonMappingException {
        AnnotatedMember m;
        Object convDef;
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (_neitherNull(intr, prop) && (m = prop.getMember()) != null && (convDef = intr.findSerializationContentConverter(m)) != null) {
            Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
            if (existingSerializer == null && !delegateType.isJavaLangObject()) {
                existingSerializer = provider.findValueSerializer(delegateType);
            }
            return new StdDelegatingSerializer(conv, delegateType, existingSerializer);
        }
        return existingSerializer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyFilter findPropertyFilter(SerializerProvider provider, Object filterId, Object valueToFilter) throws JsonMappingException {
        FilterProvider filters = provider.getFilterProvider();
        if (filters == null) {
            return (PropertyFilter) provider.reportBadDefinition((Class<?>) handledType(), "Cannot resolve PropertyFilter with id '" + filterId + "'; no FilterProvider configured");
        }
        return filters.findPropertyFilter(filterId, valueToFilter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonFormat.Value findFormatOverrides(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyFormat(typeForDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean findFormatFeature(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
        JsonFormat.Value format = findFormatOverrides(provider, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonInclude.Value findIncludeOverrides(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyInclusion(provider.getConfig(), typeForDefaults);
        }
        return provider.getDefaultPropertyInclusion(typeForDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonSerializer<?> findAnnotatedContentSerializer(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        Object serDef;
        if (property != null) {
            AnnotatedMember m = property.getMember();
            AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            if (m != null && (serDef = intr.findContentSerializer(m)) != null) {
                return serializers.serializerInstance(m, serDef);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDefaultSerializer(JsonSerializer<?> serializer) {
        return ClassUtil.isJacksonStdImpl(serializer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final boolean _neitherNull(Object a, Object b) {
        return (a == null || b == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final boolean _nonEmpty(Collection<?> c) {
        return (c == null || c.isEmpty()) ? false : true;
    }
}
