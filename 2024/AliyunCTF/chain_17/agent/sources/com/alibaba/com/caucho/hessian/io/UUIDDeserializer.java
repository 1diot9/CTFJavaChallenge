package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.UUID;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/UUIDDeserializer.class */
public class UUIDDeserializer extends AbstractDeserializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return UUID.class;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        String uuidString = in.readString();
        return UUID.fromString(uuidString);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        String uuidString = in.readString();
        return UUID.fromString(uuidString);
    }
}
