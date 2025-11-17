package com.alibaba.com.caucho.hessian.io;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BeanSerializerFactory.class */
public class BeanSerializerFactory extends SerializerFactory {
    @Override // com.alibaba.com.caucho.hessian.io.SerializerFactory
    protected Serializer getDefaultSerializer(Class cl) {
        return new BeanSerializer(cl, getClassLoader());
    }

    @Override // com.alibaba.com.caucho.hessian.io.SerializerFactory
    protected Deserializer getDefaultDeserializer(Class cl) {
        return new BeanDeserializer(cl);
    }
}
