package org.springframework.boot.web.embedded.jetty;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.core.log.LogMessage;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/GracefulShutdown.class */
final class GracefulShutdown {
    private static final Log logger = LogFactory.getLog((Class<?>) GracefulShutdown.class);
    private final Server server;
    private final Supplier<Integer> activeRequests;
    private volatile boolean shuttingDown = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GracefulShutdown(Server server, Supplier<Integer> activeRequests) {
        this.server = server;
        this.activeRequests = activeRequests;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void shutDownGracefully(GracefulShutdownCallback callback) {
        logger.info("Commencing graceful shutdown. Waiting for active requests to complete");
        boolean jetty10 = isJetty10();
        for (Connector connector : this.server.getConnectors()) {
            shutdown(connector, !jetty10);
        }
        this.shuttingDown = true;
        new Thread(() -> {
            awaitShutdown(callback);
        }, "jetty-shutdown").start();
    }

    private void shutdown(Connector connector, boolean getResult) {
        Future<Void> result;
        try {
            result = connector.shutdown();
        } catch (NoSuchMethodError e) {
            Method shutdown = ReflectionUtils.findMethod(connector.getClass(), "shutdown");
            result = (Future) ReflectionUtils.invokeMethod(shutdown, connector);
        }
        if (getResult) {
            try {
                result.get();
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e3) {
            }
        }
    }

    private boolean isJetty10() {
        try {
            return CompletableFuture.class.equals(Connector.class.getMethod("shutdown", new Class[0]).getReturnType());
        } catch (Exception e) {
            return false;
        }
    }

    private void awaitShutdown(GracefulShutdownCallback callback) {
        while (this.shuttingDown && this.activeRequests.get().intValue() > 0) {
            sleep(100L);
        }
        System.out.println(this.activeRequests.get());
        this.shuttingDown = false;
        long activeRequests = this.activeRequests.get().intValue();
        if (activeRequests == 0) {
            logger.info("Graceful shutdown complete");
            callback.shutdownComplete(GracefulShutdownResult.IDLE);
        } else {
            logger.info(LogMessage.format("Graceful shutdown aborted with %d request(s) still active", Long.valueOf(activeRequests)));
            callback.shutdownComplete(GracefulShutdownResult.REQUESTS_ACTIVE);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void abort() {
        this.shuttingDown = false;
    }
}
