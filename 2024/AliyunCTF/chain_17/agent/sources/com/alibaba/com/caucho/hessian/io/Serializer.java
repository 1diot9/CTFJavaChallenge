package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Serializer.class */
public interface Serializer {
    void writeObject(Object obj, AbstractHessianOutput abstractHessianOutput) throws IOException;
}
