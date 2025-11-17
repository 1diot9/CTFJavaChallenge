package org.springframework.boot.autoconfigure.web.embedded;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import org.apache.coyote.http11.Constants;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.autoconfigure.web.ServerProperties;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/JettyThreadPool.class */
final class JettyThreadPool {
    private JettyThreadPool() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static QueuedThreadPool create(ServerProperties.Jetty.Threads properties) {
        BlockingQueue<Runnable> queue = determineBlockingQueue(properties.getMaxQueueCapacity());
        int maxThreadCount = properties.getMax().intValue() > 0 ? properties.getMax().intValue() : 200;
        int minThreadCount = properties.getMin().intValue() > 0 ? properties.getMin().intValue() : 8;
        int threadIdleTimeout = properties.getIdleTimeout() != null ? (int) properties.getIdleTimeout().toMillis() : Constants.DEFAULT_CONNECTION_TIMEOUT;
        return new QueuedThreadPool(maxThreadCount, minThreadCount, threadIdleTimeout, queue);
    }

    private static BlockingQueue<Runnable> determineBlockingQueue(Integer maxQueueCapacity) {
        if (maxQueueCapacity == null) {
            return null;
        }
        if (maxQueueCapacity.intValue() == 0) {
            return new SynchronousQueue();
        }
        return new BlockingArrayQueue(maxQueueCapacity.intValue());
    }
}
