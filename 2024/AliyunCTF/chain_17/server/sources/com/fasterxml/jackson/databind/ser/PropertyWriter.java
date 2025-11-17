package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/PropertyWriter.class */
public abstract class PropertyWriter extends ConcreteBeanPropertyBase implements Serializable {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.BeanProperty, com.fasterxml.jackson.databind.util.Named
    public abstract String getName();

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract PropertyName getFullName();

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract <A extends Annotation> A getAnnotation(Class<A> cls);

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract <A extends Annotation> A getContextAnnotation(Class<A> cls);

    public abstract void serializeAsField(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws Exception;

    public abstract void serializeAsOmittedField(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws Exception;

    public abstract void serializeAsElement(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws Exception;

    public abstract void serializeAsPlaceholder(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws Exception;

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract void depositSchemaProperty(JsonObjectFormatVisitor jsonObjectFormatVisitor, SerializerProvider serializerProvider) throws JsonMappingException;

    @Deprecated
    public abstract void depositSchemaProperty(ObjectNode objectNode, SerializerProvider serializerProvider) throws JsonMappingException;

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyWriter(PropertyMetadata md) {
        super(md);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyWriter(BeanPropertyDefinition propDef) {
        super(propDef.getMetadata());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyWriter(PropertyWriter base) {
        super(base);
    }

    public <A extends Annotation> A findAnnotation(Class<A> cls) {
        Annotation annotation = getAnnotation(cls);
        if (annotation == null) {
            annotation = getContextAnnotation(cls);
        }
        return (A) annotation;
    }
}
