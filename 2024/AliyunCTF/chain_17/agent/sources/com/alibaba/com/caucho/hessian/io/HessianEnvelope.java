package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianEnvelope.class */
public abstract class HessianEnvelope {
    public abstract Hessian2Output wrap(Hessian2Output hessian2Output) throws IOException;

    public abstract Hessian2Input unwrap(Hessian2Input hessian2Input) throws IOException;

    public abstract Hessian2Input unwrapHeaders(Hessian2Input hessian2Input) throws IOException;
}
