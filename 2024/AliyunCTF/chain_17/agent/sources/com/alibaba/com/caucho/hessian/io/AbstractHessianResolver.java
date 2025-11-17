package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractHessianResolver.class */
public class AbstractHessianResolver implements HessianRemoteResolver {
    @Override // com.alibaba.com.caucho.hessian.io.HessianRemoteResolver
    public Object lookup(String type, String url) throws IOException {
        return new HessianRemote(type, url);
    }
}
