package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/SqlDateSerializer.class */
public class SqlDateSerializer extends AbstractSerializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        Class cl = obj.getClass();
        if (out.addRef(obj)) {
            return;
        }
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            out.writeString("value");
            out.writeUTCDate(((Date) obj).getTime());
            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeInt(1);
                out.writeString("value");
                out.writeObjectBegin(cl.getName());
            }
            out.writeUTCDate(((Date) obj).getTime());
        }
    }
}
