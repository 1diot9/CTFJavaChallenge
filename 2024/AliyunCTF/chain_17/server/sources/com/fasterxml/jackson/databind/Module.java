package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import java.util.Collection;
import java.util.Collections;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/Module.class */
public abstract class Module implements Versioned {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/Module$SetupContext.class */
    public interface SetupContext {
        Version getMapperVersion();

        <C extends ObjectCodec> C getOwner();

        TypeFactory getTypeFactory();

        boolean isEnabled(MapperFeature mapperFeature);

        boolean isEnabled(DeserializationFeature deserializationFeature);

        boolean isEnabled(SerializationFeature serializationFeature);

        boolean isEnabled(JsonFactory.Feature feature);

        boolean isEnabled(JsonParser.Feature feature);

        boolean isEnabled(JsonGenerator.Feature feature);

        MutableConfigOverride configOverride(Class<?> cls);

        void addDeserializers(Deserializers deserializers);

        void addKeyDeserializers(KeyDeserializers keyDeserializers);

        void addSerializers(Serializers serializers);

        void addKeySerializers(Serializers serializers);

        void addBeanDeserializerModifier(BeanDeserializerModifier beanDeserializerModifier);

        void addBeanSerializerModifier(BeanSerializerModifier beanSerializerModifier);

        void addAbstractTypeResolver(AbstractTypeResolver abstractTypeResolver);

        void addTypeModifier(TypeModifier typeModifier);

        void addValueInstantiators(ValueInstantiators valueInstantiators);

        void setClassIntrospector(ClassIntrospector classIntrospector);

        void insertAnnotationIntrospector(AnnotationIntrospector annotationIntrospector);

        void appendAnnotationIntrospector(AnnotationIntrospector annotationIntrospector);

        void registerSubtypes(Class<?>... clsArr);

        void registerSubtypes(NamedType... namedTypeArr);

        void registerSubtypes(Collection<Class<?>> collection);

        void setMixInAnnotations(Class<?> cls, Class<?> cls2);

        void addDeserializationProblemHandler(DeserializationProblemHandler deserializationProblemHandler);

        void setNamingStrategy(PropertyNamingStrategy propertyNamingStrategy);
    }

    public abstract String getModuleName();

    @Override // com.fasterxml.jackson.core.Versioned
    public abstract Version version();

    public abstract void setupModule(SetupContext setupContext);

    public Object getTypeId() {
        return getClass().getName();
    }

    public Iterable<? extends Module> getDependencies() {
        return Collections.emptyList();
    }
}
