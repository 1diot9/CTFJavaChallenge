package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/StringValueSerializer.class */
public class StringValueSerializer extends AbstractSerializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        if (out.addRef(obj)) {
            return;
        }
        Class cl = obj.getClass();
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            out.writeString("value");
            out.writeString(obj.toString());
            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeInt(1);
                out.writeString("value");
                out.writeObjectBegin(cl.getName());
            }
            out.writeString(obj.toString());
        }
    }
}
