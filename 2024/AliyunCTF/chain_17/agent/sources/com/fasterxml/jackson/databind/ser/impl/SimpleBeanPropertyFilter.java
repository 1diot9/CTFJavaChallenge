package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/SimpleBeanPropertyFilter.class */
public class SimpleBeanPropertyFilter implements BeanPropertyFilter, PropertyFilter {
    protected SimpleBeanPropertyFilter() {
    }

    public static SimpleBeanPropertyFilter serializeAll() {
        return SerializeExceptFilter.INCLUDE_ALL;
    }

    public static SimpleBeanPropertyFilter filterOutAll() {
        return FilterExceptFilter.EXCLUDE_ALL;
    }

    @Deprecated
    public static SimpleBeanPropertyFilter serializeAll(Set<String> properties) {
        return new FilterExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter filterOutAllExcept(Set<String> properties) {
        return new FilterExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter filterOutAllExcept(String... propertyArray) {
        HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new FilterExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter serializeAllExcept(Set<String> properties) {
        return new SerializeExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter serializeAllExcept(String... propertyArray) {
        HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new SerializeExceptFilter(properties);
    }

    public static PropertyFilter from(final BeanPropertyFilter src) {
        return new PropertyFilter() { // from class: com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.1
            @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
            public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer) throws Exception {
                BeanPropertyFilter.this.serializeAsField(pojo, jgen, prov, (BeanPropertyWriter) writer);
            }

            @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
            public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
                BeanPropertyFilter.this.depositSchemaProperty((BeanPropertyWriter) writer, propertiesNode, provider);
            }

            @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
            public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
                BeanPropertyFilter.this.depositSchemaProperty((BeanPropertyWriter) writer, objectVisitor, provider);
            }

            @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
            public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer) throws Exception {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected boolean include(BeanPropertyWriter writer) {
        return true;
    }

    protected boolean include(PropertyWriter writer) {
        return true;
    }

    protected boolean includeElement(Object elementValue) {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyFilter
    @Deprecated
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider provider, BeanPropertyWriter writer) throws Exception {
        if (include(writer)) {
            writer.serializeAsField(bean, jgen, provider);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(bean, jgen, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyFilter
    @Deprecated
    public void depositSchemaProperty(BeanPropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
        if (include(writer)) {
            writer.depositSchemaProperty(propertiesNode, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyFilter
    @Deprecated
    public void depositSchemaProperty(BeanPropertyWriter writer, JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
        if (include(writer)) {
            writer.depositSchemaProperty(objectVisitor, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (include(writer)) {
            writer.serializeAsField(pojo, jgen, provider);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (includeElement(elementValue)) {
            writer.serializeAsElement(elementValue, jgen, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
    @Deprecated
    public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
        if (include(writer)) {
            writer.depositSchemaProperty(propertiesNode, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyFilter
    public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
        if (include(writer)) {
            writer.depositSchemaProperty(objectVisitor, provider);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/SimpleBeanPropertyFilter$FilterExceptFilter.class */
    public static class FilterExceptFilter extends SimpleBeanPropertyFilter implements Serializable {
        private static final long serialVersionUID = 1;
        static final FilterExceptFilter EXCLUDE_ALL = new FilterExceptFilter(Collections.emptySet());
        protected final Set<String> _propertiesToInclude;

        public FilterExceptFilter(Set<String> properties) {
            this._propertiesToInclude = properties;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
        protected boolean include(BeanPropertyWriter writer) {
            return this._propertiesToInclude.contains(writer.getName());
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
        protected boolean include(PropertyWriter writer) {
            return this._propertiesToInclude.contains(writer.getName());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/SimpleBeanPropertyFilter$SerializeExceptFilter.class */
    public static class SerializeExceptFilter extends SimpleBeanPropertyFilter implements Serializable {
        private static final long serialVersionUID = 1;
        static final SerializeExceptFilter INCLUDE_ALL = new SerializeExceptFilter(Collections.emptySet());
        protected final Set<String> _propertiesToExclude;

        public SerializeExceptFilter(Set<String> properties) {
            this._propertiesToExclude = properties;
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
        protected boolean include(BeanPropertyWriter writer) {
            return !this._propertiesToExclude.contains(writer.getName());
        }

        @Override // com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
        protected boolean include(PropertyWriter writer) {
            return !this._propertiesToExclude.contains(writer.getName());
        }
    }
}
