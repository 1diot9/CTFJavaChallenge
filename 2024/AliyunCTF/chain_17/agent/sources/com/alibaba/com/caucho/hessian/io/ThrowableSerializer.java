package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ThrowableSerializer.class */
public class ThrowableSerializer extends JavaSerializer {
    public ThrowableSerializer(Class cl, ClassLoader loader) {
        super(cl, loader);
    }

    @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer, com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Throwable e = (Throwable) obj;
        e.getStackTrace();
        super.writeObject(obj, out);
    }
}
