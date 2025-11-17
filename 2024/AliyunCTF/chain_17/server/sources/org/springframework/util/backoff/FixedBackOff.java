package org.springframework.util.backoff;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/backoff/FixedBackOff.class */
public class FixedBackOff implements BackOff {
    public static final long DEFAULT_INTERVAL = 5000;
    public static final long UNLIMITED_ATTEMPTS = Long.MAX_VALUE;
    private long interval;
    private long maxAttempts;

    public FixedBackOff() {
        this.interval = 5000L;
        this.maxAttempts = Long.MAX_VALUE;
    }

    public FixedBackOff(long interval, long maxAttempts) {
        this.interval = 5000L;
        this.maxAttempts = Long.MAX_VALUE;
        this.interval = interval;
        this.maxAttempts = maxAttempts;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setMaxAttempts(long maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getMaxAttempts() {
        return this.maxAttempts;
    }

    @Override // org.springframework.util.backoff.BackOff
    public BackOffExecution start() {
        return new FixedBackOffExecution();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/backoff/FixedBackOff$FixedBackOffExecution.class */
    private class FixedBackOffExecution implements BackOffExecution {
        private long currentAttempts = 0;

        private FixedBackOffExecution() {
        }

        @Override // org.springframework.util.backoff.BackOffExecution
        public long nextBackOff() {
            this.currentAttempts++;
            if (this.currentAttempts <= FixedBackOff.this.getMaxAttempts()) {
                return FixedBackOff.this.getInterval();
            }
            return -1L;
        }

        public String toString() {
            String valueOf = FixedBackOff.this.maxAttempts == Long.MAX_VALUE ? "unlimited" : String.valueOf(FixedBackOff.this.maxAttempts);
            long j = FixedBackOff.this.interval;
            return "FixedBackOff{interval=" + j + ", currentAttempts=" + j + ", maxAttempts=" + this.currentAttempts + "}";
        }
    }
}
