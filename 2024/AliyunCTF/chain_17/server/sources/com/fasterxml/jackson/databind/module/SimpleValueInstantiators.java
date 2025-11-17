package com.fasterxml.jackson.databind.module;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.type.ClassKey;
import java.io.Serializable;
import java.util.HashMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/module/SimpleValueInstantiators.class */
public class SimpleValueInstantiators extends ValueInstantiators.Base implements Serializable {
    private static final long serialVersionUID = -8929386427526115130L;
    protected HashMap<ClassKey, ValueInstantiator> _classMappings = new HashMap<>();

    public SimpleValueInstantiators addValueInstantiator(Class<?> forType, ValueInstantiator inst) {
        this._classMappings.put(new ClassKey(forType), inst);
        return this;
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiators.Base, com.fasterxml.jackson.databind.deser.ValueInstantiators
    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
        ValueInstantiator inst = this._classMappings.get(new ClassKey(beanDesc.getBeanClass()));
        return inst == null ? defaultInstantiator : inst;
    }
}
