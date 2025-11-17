package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.util.Duration;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/SiftingAppenderBase.class */
public abstract class SiftingAppenderBase<E> extends AppenderBase<E> {
    protected AppenderTracker<E> appenderTracker;
    AppenderFactory<E> appenderFactory;
    Duration timeout = new Duration(1800000);
    int maxAppenderCount = Integer.MAX_VALUE;
    SiftModel siftModel;
    Discriminator<E> discriminator;

    protected abstract long getTimestamp(E e);

    protected abstract boolean eventMarksEndOfLife(E e);

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public SiftModel getSiftModel() {
        return this.siftModel;
    }

    public void setSiftModel(SiftModel siftModel) {
        this.siftModel = siftModel;
    }

    public int getMaxAppenderCount() {
        return this.maxAppenderCount;
    }

    public void setMaxAppenderCount(int maxAppenderCount) {
        this.maxAppenderCount = maxAppenderCount;
    }

    public void setAppenderFactory(AppenderFactory<E> appenderFactory) {
        this.appenderFactory = appenderFactory;
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errors = 0;
        if (this.discriminator == null) {
            addError("Missing discriminator. Aborting");
            errors = 0 + 1;
        }
        if (!this.discriminator.isStarted()) {
            addError("Discriminator has not started successfully. Aborting");
            errors++;
        }
        if (this.appenderFactory == null) {
            addError("AppenderFactory has not been set. Aborting");
            errors++;
        } else {
            this.appenderTracker = new AppenderTracker<>(this.context, this.appenderFactory);
            this.appenderTracker.setMaxComponents(this.maxAppenderCount);
            this.appenderTracker.setTimeout(this.timeout.getMilliseconds());
        }
        if (errors == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        for (Appender<E> appender : this.appenderTracker.allComponents()) {
            appender.stop();
        }
    }

    @Override // ch.qos.logback.core.AppenderBase
    protected void append(E event) {
        if (!isStarted()) {
            return;
        }
        String discriminatingValue = this.discriminator.getDiscriminatingValue(event);
        long timestamp = getTimestamp(event);
        Appender<E> appender = this.appenderTracker.getOrCreate(discriminatingValue, timestamp);
        if (eventMarksEndOfLife(event)) {
            this.appenderTracker.endOfLife(discriminatingValue);
        }
        this.appenderTracker.removeStaleComponents(timestamp);
        appender.doAppend(event);
    }

    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }

    public void setDiscriminator(Discriminator<E> discriminator) {
        this.discriminator = discriminator;
    }

    public AppenderTracker<E> getAppenderTracker() {
        return this.appenderTracker;
    }

    public String getDiscriminatorKey() {
        if (this.discriminator != null) {
            return this.discriminator.getKey();
        }
        return null;
    }
}
