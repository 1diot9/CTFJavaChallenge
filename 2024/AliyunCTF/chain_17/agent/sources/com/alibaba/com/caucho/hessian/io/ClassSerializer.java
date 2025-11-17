package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ClassSerializer.class */
public class ClassSerializer extends AbstractSerializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Class cl = (Class) obj;
        if (cl == null) {
            out.writeNull();
            return;
        }
        if (out.addRef(obj)) {
            return;
        }
        int ref = out.writeObjectBegin("java.lang.Class");
        if (ref < -1) {
            out.writeString("name");
            out.writeString(cl.getName());
            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeInt(1);
                out.writeString("name");
                out.writeObjectBegin("java.lang.Class");
            }
            out.writeString(cl.getName());
        }
    }
}
