package org.apache.tomcat.util.threads;

import java.util.concurrent.Executor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/ResizableExecutor.class */
public interface ResizableExecutor extends Executor {
    int getPoolSize();

    int getMaxThreads();

    int getActiveCount();

    boolean resizePool(int i, int i2);

    boolean resizeQueue(int i);
}
