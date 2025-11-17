package org.springframework.boot.autoconfigure.task;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task.execution")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties.class */
public class TaskExecutionProperties {
    private final Pool pool = new Pool();
    private final Simple simple = new Simple();
    private final Shutdown shutdown = new Shutdown();
    private String threadNamePrefix = "task-";

    public Simple getSimple() {
        return this.simple;
    }

    public Pool getPool() {
        return this.pool;
    }

    public Shutdown getShutdown() {
        return this.shutdown;
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties$Simple.class */
    public static class Simple {
        private Integer concurrencyLimit;

        public Integer getConcurrencyLimit() {
            return this.concurrencyLimit;
        }

        public void setConcurrencyLimit(Integer concurrencyLimit) {
            this.concurrencyLimit = concurrencyLimit;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties$Pool.class */
    public static class Pool {
        private int queueCapacity = Integer.MAX_VALUE;
        private int coreSize = 8;
        private int maxSize = Integer.MAX_VALUE;
        private boolean allowCoreThreadTimeout = true;
        private Duration keepAlive = Duration.ofSeconds(60);

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public int getCoreSize() {
            return this.coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public boolean isAllowCoreThreadTimeout() {
            return this.allowCoreThreadTimeout;
        }

        public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
            this.allowCoreThreadTimeout = allowCoreThreadTimeout;
        }

        public Duration getKeepAlive() {
            return this.keepAlive;
        }

        public void setKeepAlive(Duration keepAlive) {
            this.keepAlive = keepAlive;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutionProperties$Shutdown.class */
    public static class Shutdown {
        private boolean awaitTermination;
        private Duration awaitTerminationPeriod;

        public boolean isAwaitTermination() {
            return this.awaitTermination;
        }

        public void setAwaitTermination(boolean awaitTermination) {
            this.awaitTermination = awaitTermination;
        }

        public Duration getAwaitTerminationPeriod() {
            return this.awaitTerminationPeriod;
        }

        public void setAwaitTerminationPeriod(Duration awaitTerminationPeriod) {
            this.awaitTerminationPeriod = awaitTerminationPeriod;
        }
    }
}
