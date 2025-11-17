package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/security/PrivilegedGetTccl.class */
public class PrivilegedGetTccl implements PrivilegedAction<ClassLoader> {
    private final Thread currentThread;

    @Deprecated
    public PrivilegedGetTccl() {
        this(Thread.currentThread());
    }

    public PrivilegedGetTccl(Thread currentThread) {
        this.currentThread = currentThread;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public ClassLoader run() {
        return this.currentThread.getContextClassLoader();
    }
}
