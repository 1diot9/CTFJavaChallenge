package org.springframework.boot.jdbc;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/HikariCheckpointRestoreLifecycle.class */
public class HikariCheckpointRestoreLifecycle implements Lifecycle {
    private static final Log logger = LogFactory.getLog((Class<?>) HikariCheckpointRestoreLifecycle.class);
    private static final Field CLOSE_CONNECTION_EXECUTOR;
    private final Function<HikariPool, Boolean> hasOpenConnections = pool -> {
        ThreadPoolExecutor closeConnectionExecutor = (ThreadPoolExecutor) ReflectionUtils.getField(CLOSE_CONNECTION_EXECUTOR, pool);
        Assert.notNull(closeConnectionExecutor, "CloseConnectionExecutor was null");
        return Boolean.valueOf(closeConnectionExecutor.getActiveCount() > 0);
    };
    private final HikariDataSource dataSource;

    static {
        Field closeConnectionExecutor = ReflectionUtils.findField(HikariPool.class, "closeConnectionExecutor");
        Assert.notNull(closeConnectionExecutor, "Unable to locate closeConnectionExecutor for HikariPool");
        Assert.isAssignable((Class<?>) ThreadPoolExecutor.class, closeConnectionExecutor.getType(), "Expected ThreadPoolExecutor for closeConnectionExecutor but found %s".formatted(closeConnectionExecutor.getType()));
        ReflectionUtils.makeAccessible(closeConnectionExecutor);
        CLOSE_CONNECTION_EXECUTOR = closeConnectionExecutor;
    }

    public HikariCheckpointRestoreLifecycle(DataSource dataSource) {
        this.dataSource = (HikariDataSource) DataSourceUnwrapper.unwrap(dataSource, HikariConfigMXBean.class, HikariDataSource.class);
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        if (this.dataSource == null || this.dataSource.isRunning()) {
            return;
        }
        Assert.state(!this.dataSource.isClosed(), "DataSource has been closed and cannot be restarted");
        if (this.dataSource.isAllowPoolSuspension()) {
            logger.info("Resuming Hikari pool");
            this.dataSource.getHikariPoolMXBean().resumePool();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        if (this.dataSource == null || !this.dataSource.isRunning()) {
            return;
        }
        if (this.dataSource.isAllowPoolSuspension()) {
            logger.info("Suspending Hikari pool");
            this.dataSource.getHikariPoolMXBean().suspendPool();
        }
        closeConnections(Duration.ofMillis(this.dataSource.getConnectionTimeout() + 250));
    }

    private void closeConnections(Duration shutdownTimeout) {
        logger.info("Evicting Hikari connections");
        this.dataSource.getHikariPoolMXBean().softEvictConnections();
        logger.debug("Waiting for Hikari connections to be closed");
        CompletableFuture<Void> allConnectionsClosed = CompletableFuture.runAsync(this::waitForConnectionsToClose);
        try {
            allConnectionsClosed.get(shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS);
            logger.debug("Hikari connections closed");
        } catch (InterruptedException ex) {
            logger.warn("Interrupted while waiting for connections to be closed", ex);
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex2) {
            throw new RuntimeException("Failed to close Hikari connections", ex2);
        } catch (TimeoutException ex3) {
            logger.warn(LogMessage.format("Hikari connections could not be closed within %s", shutdownTimeout), ex3);
        }
    }

    private void waitForConnectionsToClose() {
        while (this.hasOpenConnections.apply((HikariPool) this.dataSource.getHikariPoolMXBean()).booleanValue()) {
            try {
                TimeUnit.MILLISECONDS.sleep(50L);
            } catch (InterruptedException ex) {
                logger.error("Interrupted while waiting for datasource connections to be closed", ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.dataSource != null && this.dataSource.isRunning();
    }
}
