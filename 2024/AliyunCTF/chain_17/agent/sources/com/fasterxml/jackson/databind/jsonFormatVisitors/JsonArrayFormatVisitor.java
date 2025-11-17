package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonArrayFormatVisitor.class */
public interface JsonArrayFormatVisitor extends JsonFormatVisitorWithSerializerProvider {
    void itemsFormat(JsonFormatVisitable jsonFormatVisitable, JavaType javaType) throws JsonMappingException;

    void itemsFormat(JsonFormatTypes jsonFormatTypes) throws JsonMappingException;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonArrayFormatVisitor$Base.class */
    public static class Base implements JsonArrayFormatVisitor {
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

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor
        public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) throws JsonMappingException {
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor
        public void itemsFormat(JsonFormatTypes format) throws JsonMappingException {
        }
    }
}
