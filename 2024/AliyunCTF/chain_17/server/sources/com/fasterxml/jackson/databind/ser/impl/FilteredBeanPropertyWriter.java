package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/FilteredBeanPropertyWriter.class */
public abstract class FilteredBeanPropertyWriter {
    public static BeanPropertyWriter constructViewBased(BeanPropertyWriter base, Class<?>[] viewsToIncludeIn) {
        if (viewsToIncludeIn.length == 1) {
            return new SingleView(base, viewsToIncludeIn[0]);
        }
        return new MultiView(base, viewsToIncludeIn);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/FilteredBeanPropertyWriter$SingleView.class */
    private static final class SingleView extends BeanPropertyWriter implements Serializable {
        private static final long serialVersionUID = 1;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?> _view;

        protected SingleView(BeanPropertyWriter delegate, Class<?> view) {
            super(delegate);
            this._delegate = delegate;
            this._view = view;
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public SingleView rename(NameTransformer transformer) {
            return new SingleView(this._delegate.rename(transformer), this._view);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public void assignSerializer(JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public void assignNullSerializer(JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsField(bean, gen, prov);
            } else {
                this._delegate.serializeAsOmittedField(bean, gen, prov);
            }
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
        public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsElement(bean, gen, prov);
            } else {
                this._delegate.serializeAsPlaceholder(bean, gen, prov);
            }
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
        public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
            Class<?> activeView = provider.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                super.depositSchemaProperty(v, provider);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/FilteredBeanPropertyWriter$MultiView.class */
    private static final class MultiView extends BeanPropertyWriter implements Serializable {
        private static final long serialVersionUID = 1;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?>[] _views;

        protected MultiView(BeanPropertyWriter delegate, Class<?>[] views) {
            super(delegate);
            this._delegate = delegate;
            this._views = views;
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public MultiView rename(NameTransformer transformer) {
            return new MultiView(this._delegate.rename(transformer), this._views);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public void assignSerializer(JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
        public void assignNullSerializer(JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            if (_inView(prov.getActiveView())) {
                this._delegate.serializeAsField(bean, gen, prov);
            } else {
                this._delegate.serializeAsOmittedField(bean, gen, prov);
            }
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
        public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            if (_inView(prov.getActiveView())) {
                this._delegate.serializeAsElement(bean, gen, prov);
            } else {
                this._delegate.serializeAsPlaceholder(bean, gen, prov);
            }
        }

        @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
        public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
            if (_inView(provider.getActiveView())) {
                super.depositSchemaProperty(v, provider);
            }
        }

        private final boolean _inView(Class<?> activeView) {
            if (activeView == null) {
                return true;
            }
            int len = this._views.length;
            for (int i = 0; i < len; i++) {
                if (this._views[i].isAssignableFrom(activeView)) {
                    return true;
                }
            }
            return false;
        }
    }
}
