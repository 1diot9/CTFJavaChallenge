package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractSerializer.class */
public abstract class AbstractSerializer implements Serializer {
    protected static final Logger log = Logger.getLogger(AbstractSerializer.class.getName());

    @Override // com.alibaba.com.caucho.hessian.io.Serializer
    public abstract void writeObject(Object obj, AbstractHessianOutput abstractHessianOutput) throws IOException;
}
