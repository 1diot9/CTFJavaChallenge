package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/CoreXMLSerializers.class */
public class CoreXMLSerializers extends Serializers.Base {
    @Override // com.fasterxml.jackson.databind.ser.Serializers.Base, com.fasterxml.jackson.databind.ser.Serializers
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (Duration.class.isAssignableFrom(raw) || QName.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (XMLGregorianCalendar.class.isAssignableFrom(raw)) {
            return XMLGregorianCalendarSerializer.instance;
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/CoreXMLSerializers$XMLGregorianCalendarSerializer.class */
    public static class XMLGregorianCalendarSerializer extends StdSerializer<XMLGregorianCalendar> implements ContextualSerializer {
        static final XMLGregorianCalendarSerializer instance = new XMLGregorianCalendarSerializer();
        final JsonSerializer<Object> _delegate;

        public XMLGregorianCalendarSerializer() {
            this(CalendarSerializer.instance);
        }

        protected XMLGregorianCalendarSerializer(JsonSerializer<?> del) {
            super(XMLGregorianCalendar.class);
            this._delegate = del;
        }

        @Override // com.fasterxml.jackson.databind.JsonSerializer
        public JsonSerializer<?> getDelegatee() {
            return this._delegate;
        }

        @Override // com.fasterxml.jackson.databind.JsonSerializer
        public boolean isEmpty(SerializerProvider provider, XMLGregorianCalendar value) {
            return this._delegate.isEmpty(provider, _convert(value));
        }

        @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
        public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            this._delegate.serialize(_convert(value), gen, provider);
        }

        @Override // com.fasterxml.jackson.databind.JsonSerializer
        public void serializeWithType(XMLGregorianCalendar value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, XMLGregorianCalendar.class, JsonToken.VALUE_STRING));
            serialize(value, g, provider);
            typeSer.writeTypeSuffix(g, typeIdDef);
        }

        @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            this._delegate.acceptJsonFormatVisitor(visitor, null);
        }

        @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
        public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
            JsonSerializer<?> ser = prov.handlePrimaryContextualization(this._delegate, property);
            if (ser != this._delegate) {
                return new XMLGregorianCalendarSerializer(ser);
            }
            return this;
        }

        protected Calendar _convert(XMLGregorianCalendar input) {
            if (input == null) {
                return null;
            }
            return input.toGregorianCalendar();
        }
    }
}
