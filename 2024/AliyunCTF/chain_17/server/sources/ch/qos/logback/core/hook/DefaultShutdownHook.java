package ch.qos.logback.core.hook;

import ch.qos.logback.core.util.Duration;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/hook/DefaultShutdownHook.class */
public class DefaultShutdownHook extends ShutdownHookBase {
    public static final Duration DEFAULT_DELAY = Duration.buildByMilliseconds(0.0d);
    private Duration delay = DEFAULT_DELAY;

    public Duration getDelay() {
        return this.delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.delay.getMilliseconds() > 0) {
            addInfo("Sleeping for " + String.valueOf(this.delay));
            try {
                Thread.sleep(this.delay.getMilliseconds());
            } catch (InterruptedException e) {
            }
        }
        super.stop();
    }
}
