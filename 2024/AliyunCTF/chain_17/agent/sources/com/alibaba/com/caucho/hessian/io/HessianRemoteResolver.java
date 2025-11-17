package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianRemoteResolver.class */
public interface HessianRemoteResolver {
    Object lookup(String str, String str2) throws IOException;
}
