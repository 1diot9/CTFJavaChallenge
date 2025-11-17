package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8OptionalBeanPropertyWriter.class */
public class Jdk8OptionalBeanPropertyWriter extends BeanPropertyWriter {
    private static final long serialVersionUID = 1;
    protected final Object _empty;

    /* JADX INFO: Access modifiers changed from: protected */
    public Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base, Object empty) {
        super(base);
        this._empty = empty;
    }

    protected Jdk8OptionalBeanPropertyWriter(Jdk8OptionalBeanPropertyWriter base, PropertyName newName) {
        super(base, newName);
        this._empty = base._empty;
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    protected BeanPropertyWriter _new(PropertyName newName) {
        return new Jdk8OptionalBeanPropertyWriter(this, newName);
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter
    public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, unwrapper, this._empty);
    }

    @Override // com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsField(Object bean, JsonGenerator g, SerializerProvider prov) throws Exception {
        Object value;
        if (this._nullSerializer == null && ((value = get(bean)) == null || value.equals(this._empty))) {
            return;
        }
        super.serializeAsField(bean, g, prov);
    }
}
