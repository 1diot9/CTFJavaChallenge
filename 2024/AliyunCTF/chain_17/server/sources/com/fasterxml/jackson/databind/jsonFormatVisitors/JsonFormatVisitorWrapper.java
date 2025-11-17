package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWrapper.class */
public interface JsonFormatVisitorWrapper extends JsonFormatVisitorWithSerializerProvider {
    JsonObjectFormatVisitor expectObjectFormat(JavaType javaType) throws JsonMappingException;

    JsonArrayFormatVisitor expectArrayFormat(JavaType javaType) throws JsonMappingException;

    JsonStringFormatVisitor expectStringFormat(JavaType javaType) throws JsonMappingException;

    JsonNumberFormatVisitor expectNumberFormat(JavaType javaType) throws JsonMappingException;

    JsonIntegerFormatVisitor expectIntegerFormat(JavaType javaType) throws JsonMappingException;

    JsonBooleanFormatVisitor expectBooleanFormat(JavaType javaType) throws JsonMappingException;

    JsonNullFormatVisitor expectNullFormat(JavaType javaType) throws JsonMappingException;

    JsonAnyFormatVisitor expectAnyFormat(JavaType javaType) throws JsonMappingException;

    JsonMapFormatVisitor expectMapFormat(JavaType javaType) throws JsonMappingException;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWrapper$Base.class */
    public static class Base implements JsonFormatVisitorWrapper {
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

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
        public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
            return null;
        }
    }
}
