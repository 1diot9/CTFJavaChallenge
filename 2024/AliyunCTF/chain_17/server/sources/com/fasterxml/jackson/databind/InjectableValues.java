package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/InjectableValues.class */
public abstract class InjectableValues {
    public abstract Object findInjectableValue(Object obj, DeserializationContext deserializationContext, BeanProperty beanProperty, Object obj2) throws JsonMappingException;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/InjectableValues$Std.class */
    public static class Std extends InjectableValues implements Serializable {
        private static final long serialVersionUID = 1;
        protected final Map<String, Object> _values;

        public Std() {
            this(new HashMap());
        }

        public Std(Map<String, Object> values) {
            this._values = values;
        }

        public Std addValue(String key, Object value) {
            this._values.put(key, value);
            return this;
        }

        public Std addValue(Class<?> classKey, Object value) {
            this._values.put(classKey.getName(), value);
            return this;
        }

        @Override // com.fasterxml.jackson.databind.InjectableValues
        public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
            if (!(valueId instanceof String)) {
                ctxt.reportBadDefinition(ClassUtil.classOf(valueId), String.format("Unrecognized inject value id type (%s), expecting String", ClassUtil.classNameOf(valueId)));
            }
            String key = (String) valueId;
            Object ob = this._values.get(key);
            if (ob == null && !this._values.containsKey(key)) {
                throw new IllegalArgumentException("No injectable id with value '" + key + "' found (for property '" + forProperty.getName() + "')");
            }
            return ob;
        }
    }
}
