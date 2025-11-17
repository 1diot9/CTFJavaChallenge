package com.alibaba.com.caucho.hessian.io;

import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ExtSerializerFactory.class */
public class ExtSerializerFactory extends AbstractSerializerFactory {
    private HashMap _serializerMap = new HashMap();
    private HashMap _deserializerMap = new HashMap();

    public void addSerializer(Class cl, Serializer serializer) {
        this._serializerMap.put(cl, serializer);
    }

    public void addDeserializer(Class cl, Deserializer deserializer) {
        this._deserializerMap.put(cl, deserializer);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializerFactory
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        return (Serializer) this._serializerMap.get(cl);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializerFactory
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        return (Deserializer) this._deserializerMap.get(cl);
    }
}
