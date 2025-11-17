package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractMapDeserializer.class */
public class AbstractMapDeserializer extends AbstractDeserializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return HashMap.class;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        if (obj != null) {
            throw error("expected map/object at " + obj.getClass().getName());
        }
        throw error("expected map/object at null");
    }
}
