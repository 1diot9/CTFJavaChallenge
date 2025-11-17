package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractListDeserializer.class */
public class AbstractListDeserializer extends AbstractDeserializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        if (obj != null) {
            throw error("expected list at " + obj.getClass().getName());
        }
        throw error("expected list at null");
    }
}
