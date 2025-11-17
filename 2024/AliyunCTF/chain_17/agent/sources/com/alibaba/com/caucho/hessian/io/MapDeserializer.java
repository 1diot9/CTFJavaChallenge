package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/MapDeserializer.class */
public class MapDeserializer extends AbstractMapDeserializer {
    private Class _type;
    private Constructor _ctor;

    public MapDeserializer(Class type) {
        type = type == null ? HashMap.class : type;
        this._type = type;
        Constructor[] ctors = type.getConstructors();
        for (int i = 0; i < ctors.length; i++) {
            if (ctors[i].getParameterTypes().length == 0) {
                this._ctor = ctors[i];
            }
        }
        if (this._ctor == null) {
            try {
                this._ctor = HashMap.class.getConstructor(new Class[0]);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractMapDeserializer, com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        if (this._type != null) {
            return this._type;
        }
        return HashMap.class;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        return readMap(in, null, null);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in, Class<?> expectKeyType, Class<?> expectValueType) throws IOException {
        Map map;
        if (this._type == null) {
            map = new HashMap();
        } else if (this._type.equals(Map.class)) {
            map = new HashMap();
        } else if (this._type.equals(SortedMap.class)) {
            map = new TreeMap();
        } else {
            try {
                map = (Map) this._ctor.newInstance(new Object[0]);
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }
        in.addRef(map);
        doReadMap(in, map, expectKeyType, expectValueType);
        in.readEnd();
        return map;
    }

    protected void doReadMap(AbstractHessianInput in, Map map, Class<?> keyType, Class<?> valueType) throws IOException {
        Deserializer keyDeserializer = null;
        Deserializer valueDeserializer = null;
        SerializerFactory factory = findSerializerFactory(in);
        if (keyType != null) {
            keyDeserializer = factory.getDeserializer(keyType.getName());
        }
        if (valueType != null) {
            valueDeserializer = factory.getDeserializer(valueType.getName());
        }
        while (!in.isEnd()) {
            map.put(keyDeserializer != null ? keyDeserializer.readObject(in) : in.readObject(), valueDeserializer != null ? valueDeserializer.readObject(in) : in.readObject());
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        Map map = createMap();
        in.addRef(map);
        for (String name : fieldNames) {
            map.put(name, in.readObject());
        }
        return map;
    }

    private Map createMap() throws IOException {
        if (this._type == null) {
            return new HashMap();
        }
        if (this._type.equals(Map.class)) {
            return new HashMap();
        }
        if (this._type.equals(SortedMap.class)) {
            return new TreeMap();
        }
        try {
            return (Map) this._ctor.newInstance(new Object[0]);
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
