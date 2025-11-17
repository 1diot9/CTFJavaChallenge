package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonObjectFormatVisitor.class */
public interface JsonObjectFormatVisitor extends JsonFormatVisitorWithSerializerProvider {
    void property(BeanProperty beanProperty) throws JsonMappingException;

    void property(String str, JsonFormatVisitable jsonFormatVisitable, JavaType javaType) throws JsonMappingException;

    void optionalProperty(BeanProperty beanProperty) throws JsonMappingException;

    void optionalProperty(String str, JsonFormatVisitable jsonFormatVisitable, JavaType javaType) throws JsonMappingException;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonObjectFormatVisitor$Base.class */
    public static class Base implements JsonObjectFormatVisitor {
        protected SerializerProvider _provider;

        public Base() {
        }

        public Base(SerializerProvider p) {
            this._provider = p;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWithSerializerProvider
        public SerializerProvider getProvider() {
            return this._provider;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWithSerializerProvider
        public void setProvider(SerializerProvider p) {
            this._provider = p;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor
        public void property(BeanProperty prop) throws JsonMappingException {
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor
        public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint) throws JsonMappingException {
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor
        public void optionalProperty(BeanProperty prop) throws JsonMappingException {
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor
        public void optionalProperty(String name, JsonFormatVisitable handler, JavaType propertyTypeHint) throws JsonMappingException {
        }
    }
}
