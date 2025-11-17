package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8UnwrappingOptionalBeanPropertyWriter.class */
public class Jdk8UnwrappingOptionalBeanPropertyWriter extends UnwrappingBeanPropertyWriter {
    private static final long serialVersionUID = 1;
    protected final Object _empty;

    public Jdk8UnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base, NameTransformer transformer, Object empty) {
        super(base, transformer);
        this._empty = empty;
    }

    protected Jdk8UnwrappingOptionalBeanPropertyWriter(Jdk8UnwrappingOptionalBeanPropertyWriter base, NameTransformer transformer, SerializedString name) {
        super(base, transformer, name);
        this._empty = base._empty;
    }

    @Override // com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter
    protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName) {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
    }

    @Override // com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter, com.fasterxml.jackson.databind.ser.BeanPropertyWriter, com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        Object value;
        if (this._nullSerializer == null && ((value = get(bean)) == null || value.equals(this._empty))) {
            return;
        }
        super.serializeAsField(bean, gen, prov);
    }
}
